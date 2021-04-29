package com.example.smart_control.base.scan_broadcast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.smart_control.Myapp;
import com.example.smart_control.R;
import com.github.druk.rx2dnssd.BonjourService;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class RegistrationActivity extends AppCompatActivity {

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, RegistrationActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content, new RegistrationsFragment()).commit();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public static class RegistrationsFragment extends Fragment {

        private static final int REGISTER_REQUEST_CODE = 100;
        private static final int STOP_REQUEST_CODE = 101;

        private ServiceAdapter adapter;
        private Disposable mDisposable;
        private RecyclerView mRecyclerView;
        private View mNoServiceView;

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            adapter = new ServiceAdapter(getContext()) {
                @Override
                public void onBindViewHolder(ViewHolder holder, final int position) {
                    holder.text1.setText(getItem(position).getServiceName());
                    holder.text2.setText(getItem(position).getRegType());
                    holder.itemView.setOnClickListener(v -> startActivityForResult(ServiceActivity.startActivity(getContext(), getItem(position), true), STOP_REQUEST_CODE));
                }
            };
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == REGISTER_REQUEST_CODE) {
                if (resultCode == Activity.RESULT_OK) {
                    BonjourService bonjourService = RegisterServiceActivity.parseResult(data);
                    mDisposable = Myapp.getRegistrationManager(getContext())
                            .register(getContext(), bonjourService)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(service -> RegistrationsFragment.this.updateServices(), throwable -> Toast.makeText(RegistrationsFragment.this.getContext(), "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show());
                }
                return;
            }
            else if (requestCode == STOP_REQUEST_CODE) {
                if (resultCode == Activity.RESULT_OK) {
                    BonjourService bonjourService = ServiceActivity.parseResult(data);
                    Myapp.getRegistrationManager(getContext()).unregister(bonjourService);
                    updateServices();
                }
                return;
            }
            super.onActivityResult(requestCode, resultCode, data);
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_registrations, container, false);
            mRecyclerView = view.findViewById(R.id.recycler_view);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
            mRecyclerView.setAdapter(adapter);
            mNoServiceView = view.findViewById(R.id.no_service);
            view.findViewById(R.id.fab).setOnClickListener(v -> RegistrationsFragment.this.startActivityForResult(RegisterServiceActivity.createIntent(getContext()), REGISTER_REQUEST_CODE));
            updateServices();
            return view;
        }

        @Override
        public void onStop() {
            super.onStop();
            if (mDisposable != null && !mDisposable.isDisposed()) {
                mDisposable.dispose();
            }
        }

        private void updateServices() {
            List<BonjourService> registeredServices = Myapp.getRegistrationManager(getContext()).getRegisteredServices();
            adapter.swap(registeredServices);
            mNoServiceView.setVisibility(registeredServices.size() > 0 ? View.GONE : View.VISIBLE);
        }
    }
}