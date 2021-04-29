package com.example.smart_control.base.scan_broadcast;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.smart_control.Myapp;
import com.github.druk.rx2dnssd.BonjourService;
import com.github.druk.rx2dnssd.Rx2Dnssd;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ServiceBrowserViewModel extends AndroidViewModel {

    protected Rx2Dnssd mRxDnssd;
    protected Disposable mDisposable;

    public ServiceBrowserViewModel(@NonNull Application application) {
        super(application);
        mRxDnssd = Myapp.getRxDnssd(application);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    public void startDiscovery(String reqType,
                               String domain,
                               Consumer<BonjourService> servicesAction,
                               Consumer<Throwable> errorAction) {
        mDisposable = mRxDnssd.browse(reqType, domain)
                .compose(mRxDnssd.resolve())
                .compose(mRxDnssd.queryIPRecords())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(servicesAction, errorAction);
    }

}
