package com.example.smart_control.base.scan_broadcast.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.smart_control.BuildConfig;
import com.example.smart_control.Myapp;
import com.example.smart_control.R;
import com.example.smart_control.adapter.ServicesAdapter;
import com.example.smart_control.base.scan_broadcast.FavouritesManager;
import com.example.smart_control.base.scan_broadcast.ServiceBrowserViewModel;
import com.github.druk.rx2dnssd.BonjourService;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ServiceBrowserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ServiceBrowserFragment extends Fragment {

    private static final String KEY_REG_TYPE = "reg_type";
    private static final String KEY_DOMAIN = "domain";
    private static final String KEY_SELECTED_POSITION = "selected_position";

    protected FavouritesManager favouritesManager;

    protected ServicesAdapter mAdapter;
    protected String mReqType;
    protected String mDomain;
    protected RecyclerView mRecyclerView;
    protected LinearLayout mProgressView;
    protected LinearLayout mErrorView;

    protected View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = mRecyclerView.getLayoutManager().getPosition(v);
            mAdapter.setSelectedItemId(mAdapter.getItemId(position));
            mAdapter.notifyDataSetChanged();
            if (ServiceBrowserFragment.this.isAdded()) {
                BonjourService service = mAdapter.getItem(position);
                ((ServiceListener) ServiceBrowserFragment.this.getActivity()).onServiceWasSelected(mDomain, mReqType, service);
            }
        }
    };

    public static Fragment newInstance(String domain, String regType) {
        return fillArguments(new ServiceBrowserFragment(), domain, regType);
    }

    protected static Fragment fillArguments(Fragment fragment, String domain, String regType) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_DOMAIN, domain);
        bundle.putString(KEY_REG_TYPE, regType);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (!(context instanceof ServiceListener)) {
            throw new IllegalArgumentException("Fragment context should implement ServiceListener interface");
        }

        favouritesManager = Myapp.getFavouritesManager(context);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mReqType = getArguments().getString(KEY_REG_TYPE);
            mDomain = getArguments().getString(KEY_DOMAIN);
        }

        mAdapter = new ServicesAdapter(getActivity()) {
            @Override
            public void onBindViewHolder(ViewHolder viewHolder, int i) {
                BonjourService service = getItem(i);
                viewHolder.text1.setText(service.getServiceName());
                if (service.getInet4Address() != null) {
                    viewHolder.text2.setText(service.getInet4Address().getHostAddress());
                }
                else if (service.getInet6Address() != null) {
                    viewHolder.text2.setText(service.getInet6Address().getHostAddress());
                }
                else {
                    viewHolder.text2.setText(R.string.not_resolved_yet);
                }
                viewHolder.itemView.setOnClickListener(mListener);
                viewHolder.itemView.setBackgroundResource(getBackground(i));
            }
        };

        createViewModel();
        setHasOptionsMenu(favouriteMenuSupport());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_domain, menu);
        MenuItem item = menu.findItem(R.id.action_star);
        boolean isFavourite = favouritesManager.isFavourite(mReqType);
        item.setChecked(isFavourite);
        item.setIcon(isFavourite ? R.drawable.ic_baseline_account_circle : R.drawable.ic_baseline_account_circle);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_star) {
            if (!item.isChecked()) {
                favouritesManager.addToFavourites(mReqType);
                item.setChecked(true);
                item.setIcon(R.drawable.ic_baseline_account_circle);
                Toast.makeText(getContext(), mReqType + " saved to Favourites", Toast.LENGTH_LONG).show();
            }
            else {
                favouritesManager.removeFromFavourites(mReqType);
                item.setChecked(false);
                item.setIcon(R.drawable.ic_baseline_account_circle);
                Toast.makeText(getContext(), mReqType + " removed from Favourites", Toast.LENGTH_LONG).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected boolean favouriteMenuSupport() {
        return true;
    }

    protected void createViewModel() {
        ServiceBrowserViewModel viewModel = new ViewModelProvider.AndroidViewModelFactory(Myapp.getApplication(requireContext()))
                .create(ServiceBrowserViewModel.class);
        viewModel.startDiscovery(mReqType, mDomain, service -> {
            int itemsCount = mAdapter.getItemCount();
            if (!service.isLost()) {
                mAdapter.add(service);
            } else {
                mAdapter.remove(service);
            }
            ServiceBrowserFragment.this.showList();
            mAdapter.notifyDataSetChanged();
        }, throwable -> {
            Log.e("DNSSD", "Error: ", throwable);
            ServiceBrowserFragment.this.showError(throwable);
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FrameLayout rootView = (FrameLayout) inflater.inflate(R.layout.fragment_services_browser, container, false);
        mRecyclerView = rootView.findViewById(R.id.recycler_view);
        mProgressView = rootView.findViewById(R.id.progress);
        mErrorView = rootView.findViewById(R.id.error_container);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        if (savedInstanceState != null) {
            mAdapter.setSelectedItemId(savedInstanceState.getLong(KEY_SELECTED_POSITION, -1L));
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(KEY_SELECTED_POSITION, mAdapter.getSelectedItemId());
    }

    protected void showList(){
        if (mAdapter.getItemCount() > 0) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mProgressView.setVisibility(View.GONE);
        }
        else {
            mRecyclerView.setVisibility(View.GONE);
            mProgressView.setVisibility(View.VISIBLE);
        }
    }

    protected void showError(final Throwable e){
        if (BuildConfig.BUILD_TYPE.equals("iot")) {
            Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            return;
        }
        getActivity().runOnUiThread(() -> {
            mRecyclerView.animate().alpha(0.0f).setInterpolator(new AccelerateDecelerateInterpolator()).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRecyclerView.setVisibility(View.GONE);
                }
            }).start();
            mProgressView.animate().alpha(0.0f).setInterpolator(new AccelerateDecelerateInterpolator()).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(View.GONE);
                }
            }).start();
            mErrorView.setAlpha(0.0f);
            mErrorView.setVisibility(View.VISIBLE);
            mErrorView.animate().alpha(1.0f).setInterpolator(new AccelerateDecelerateInterpolator()).start();
            mErrorView.findViewById(R.id.send_report).setOnClickListener(v -> Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e));
        });
    }

    public interface ServiceListener {
        void onServiceWasSelected(String domain, String regType, BonjourService service);
    }
}
