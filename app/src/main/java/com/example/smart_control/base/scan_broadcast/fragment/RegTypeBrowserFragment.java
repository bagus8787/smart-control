package com.example.smart_control.base.scan_broadcast.fragment;

import android.app.Application;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.smart_control.Myapp;
import com.example.smart_control.R;
import com.example.smart_control.adapter.ServicesAdapter;
import com.example.smart_control.base.scan_broadcast.Config;
import com.example.smart_control.base.scan_broadcast.RegTypeBrowserViewModel;
import com.example.smart_control.base.scan_broadcast.RegTypeManager;
import com.github.druk.rx2dnssd.BonjourService;
import com.github.druk.rx2dnssd.Rx2Dnssd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.example.smart_control.base.scan_broadcast.Config.EMPTY_DOMAIN;
import static com.example.smart_control.base.scan_broadcast.Config.TCP_REG_TYPE_SUFFIX;
import static com.example.smart_control.base.scan_broadcast.Config.UDP_REG_TYPE_SUFFIX;

public class RegTypeBrowserFragment extends ServiceBrowserFragment {

    private static final String TAG = "RegTypeBrowser";

    private RegTypeManager mRegTypeManager;

    public static Fragment newInstance(String regType) {
        return fillArguments(new RegTypeBrowserFragment(), EMPTY_DOMAIN, regType);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRegTypeManager = Myapp.getRegTypeManager(getContext());
        mAdapter = new ServicesAdapter(getActivity()) {

            Drawable drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_account_circle);

            @Override
            public void onBindViewHolder(ViewHolder viewHolder, int i) {
                RegTypeBrowserViewModel.BonjourDomain domain = (RegTypeBrowserViewModel.BonjourDomain) getItem(i);
                String regType = domain.getServiceName() + "." + domain.getRegType().split(Config.REG_TYPE_SEPARATOR)[0] + ".";
                String regTypeDescription = mRegTypeManager.getRegTypeDescription(regType);
                if (regTypeDescription != null) {
                    viewHolder.text1.setText(regType + " (" + regTypeDescription + ")");
                } else {
                    viewHolder.text1.setText(regType);
                }

                if (favouritesManager.isFavourite(regType)) {
                    viewHolder.text1.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
                }
                else {
                    viewHolder.text1.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                }

                viewHolder.text2.setText(domain.serviceCount + " services");
                viewHolder.itemView.setOnClickListener(mListener);
                viewHolder.itemView.setBackgroundResource(getBackground(i));
            }

            @Override
            public void sortServices(ArrayList<BonjourService> services) {
                Collections.sort(services, (lhs, rhs) -> {
                    String lhsRegType = lhs.getServiceName() + "." + lhs.getRegType().split(Config.REG_TYPE_SEPARATOR)[0] + ".";
                    String rhsRegType = rhs.getServiceName() + "." + rhs.getRegType().split(Config.REG_TYPE_SEPARATOR)[0] + ".";
                    boolean isLhsFavourite = favouritesManager.isFavourite(lhsRegType);
                    boolean isRhsFavourite = favouritesManager.isFavourite(rhsRegType);
                    if (isLhsFavourite && isRhsFavourite) {
                        return lhs.getServiceName().compareTo(rhs.getServiceName());
                    }
                    else if (isLhsFavourite) {
                        return -1;
                    }
                    else if (isRhsFavourite) {
                        return 1;
                    }
                    return lhs.getServiceName().compareTo(rhs.getServiceName());
                });
            }
        };
    }

    @Override
    protected void createViewModel() {
        RegTypeBrowserViewModel viewModel = new ViewModelProvider.AndroidViewModelFactory(Myapp.getApplication(requireContext()))
                .create(RegTypeBrowserViewModel.class);

        final io.reactivex.functions.Consumer<Throwable> errorAction = throwable -> {
            Log.e("DNSSD", "Error: ", throwable);
            RegTypeBrowserFragment.this.showError(throwable);
        };

        final Consumer<Collection<RegTypeBrowserViewModel.BonjourDomain>> servicesAction = services -> {
            final int itemsCount = mAdapter.getItemCount();
            mAdapter.clear();
            for (RegTypeBrowserViewModel.BonjourDomain bonjourDomain: services) {
                if (bonjourDomain.serviceCount > 0) {
                    mAdapter.add(bonjourDomain);
                }
            }
            RegTypeBrowserFragment.this.showList();
            mAdapter.notifyDataSetChanged();
        };

        viewModel.startDiscovery(servicesAction, errorAction);
    }

    @Override
    protected boolean favouriteMenuSupport() {
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Favourites can be changed
        mAdapter.sortServices();
        mAdapter.notifyDataSetChanged();
    }
}
