package com.example.smart_control;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.smart_control.base.scan_broadcast.FavouritesManager;
import com.example.smart_control.base.scan_broadcast.RegTypeManager;
import com.example.smart_control.base.scan_broadcast.RegistrationManager;
import com.github.druk.rx2dnssd.Rx2Dnssd;
import com.github.druk.rx2dnssd.Rx2DnssdBindable;

public class Myapp extends Application {

    private static final String TAG = "MyApp";
    private Rx2Dnssd mRxDnssd;
    private RegistrationManager mRegistrationManager;
    private RegTypeManager mRegTypeManager;
    private FavouritesManager mFavouritesManager;

    private static Myapp instance;

    public static Myapp getInstance() {
        return instance;
    }

    public static Context getContext(){
        return instance;
        // or return instance.getApplicationContext();
    }


    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();

        if (BuildConfig.DEBUG) {

            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .build());
        }
        mRxDnssd = createDnssd();
        mRegistrationManager = new RegistrationManager();
        mRegTypeManager = new RegTypeManager(this);
        mFavouritesManager = new FavouritesManager(this);
    }

    public static Myapp getApplication(@NonNull Context context){
        return ((Myapp)context.getApplicationContext());
    }

    public static Rx2Dnssd getRxDnssd(@NonNull Context context){
        return ((Myapp)context.getApplicationContext()).mRxDnssd;
    }

    public static RegistrationManager getRegistrationManager(@NonNull Context context){
        return ((Myapp) context.getApplicationContext()).mRegistrationManager;
    }

    public static RegTypeManager getRegTypeManager(@NonNull Context context){
        return ((Myapp) context.getApplicationContext()).mRegTypeManager;
    }

    public static FavouritesManager getFavouritesManager(@NonNull Context context){
        return ((Myapp)context.getApplicationContext()).mFavouritesManager;
    }

    private Rx2Dnssd createDnssd() {
        Log.i(TAG, "Using bindable version of dns sd");
        return new Rx2DnssdBindable(this);
    }
}
