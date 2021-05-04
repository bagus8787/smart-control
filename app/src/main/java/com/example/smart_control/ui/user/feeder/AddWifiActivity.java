package com.example.smart_control.ui.user.feeder;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.PopUpToBuilder;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.smart_control.R;
import com.example.smart_control.network.ApiInterface;
import com.example.smart_control.network.ApiLocalClient;
import com.example.smart_control.utils.SharedPrefManager;

import java.io.DataOutputStream;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;

import cz.msebera.android.httpclient.util.TextUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddWifiActivity extends AppCompatActivity implements View.OnClickListener {

    int bv = Build.VERSION.SDK_INT;

    public static String SSID ;
    public static String PSWD ;
    private static int DELATE_TIME = 5000;
    Button btn_konek;
    TextView edt_ssid, edt_pass;

    SharedPrefManager sharedPrefManager;
    ApiInterface apiInterface;

    WifiManager mWifiManager;
    private static final String TAG = "ScanDeviceSSID";
    private WifiConfiguration conf;
    private WifiInfo info;
    private File mFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wifi);

//        setMobileDataState(false);

        sharedPrefManager = new SharedPrefManager(this);
        apiInterface = ApiLocalClient.getClient().create(ApiInterface.class);

        mWifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        conf = new WifiConfiguration();

        SSID = "FEEDR-" + sharedPrefManager.getSpIdDevice();
        PSWD = "12348765";

        btn_konek   = findViewById(R.id.btn_konek);
        edt_ssid    = findViewById(R.id.edt_ssid);
        edt_pass    = findViewById(R.id.edt_pass);

        btn_konek.setOnClickListener(this);

        mWifiManager.setWifiEnabled(true);
        WifiManager wifiManager =
                (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        readtvWifiState(wifiManager);

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        Log.d("infowifineee", "= " + wifiInfo.toString());

        if(wifiInfo.getSupplicantState().toString() == "DISCONNECTED"){
//            Log.d("infowifi", "= " + wifiInfo.getSSID());
            Log.d("infowifi", "= " + wifiInfo.getSupplicantState());

        }else{
            Log.d("infowifi", "= " + wifiInfo.getSSID());
        }

        getCurrentSsid(getApplicationContext());

        startWifiConnected();

        sharedPrefManager.saveSPString(SharedPrefManager.SP_DEVICE_SSID, SSID);
        Log.d("SSIDDDD", sharedPrefManager.getSpDeviceSsid());

        sharedPrefManager.saveSPString(SharedPrefManager.SP_DEVICE_PASS, PSWD);

        if (isInternetAvailable()==true){
            turnOffData();
        }
    }

    public void startWifiConnected() {
        Log.i(TAG, "WifiManagerActivity---startWifiConnected");
        if (mWifiManager != null && !mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);

            connectWifi(SSID, PSWD);
            Log.d("SSIDDDDD", "= " + SSID);
            Log.d("PASWWDD", "= " + SSID);

        } else {
            connectWifi(SSID, PSWD);
            Log.d("SSIDDDDD", "= " + SSID);
            Log.d("PASWWDDZZZ", "= " + SSID);

        }

    }

    public void connectWifi(final String ssid, final String pswd) {
        Log.i(TAG, "WifiManagerActivity---connectWifi");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "new Thread run!");

                Log.d(TAG, SSID);
                Log.d(TAG, PSWD);

                WifiConfiguration conf = new WifiConfiguration();
                conf.SSID = convertToQuotedString(SSID);
                conf.preSharedKey = convertToQuotedString(PSWD);
                conf.hiddenSSID = true;
                conf.status = WifiConfiguration.Status.ENABLED;
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                int id = mWifiManager.addNetwork(conf);
                mWifiManager.enableNetwork(id, true);

                Log.d(TAG, mWifiManager.toString());
            }

        }).start();
    }

    public static String convertToQuotedString(String string) {
        return "\"" + string + "\"";
    }

    public static String getCurrentSsid(Context context) {
        String ssid = null;
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo.isConnected()) {
            final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null && !TextUtils.isBlank(connectionInfo.getSSID())) {
                ssid = connectionInfo.getSSID();

                Log.d("ssiddddd", "= " + ssid);
            }
        }
        return ssid;
    }

    private String readtvWifiState(WifiManager wm){
        String result = "";
        switch (wm.getWifiState()){
            case WifiManager.WIFI_STATE_DISABLED:
                result = "WIFI_STATE_DISABLED";
                break;
            case WifiManager.WIFI_STATE_DISABLING:
                result = "WIFI_STATE_DISABLING";
                break;
            case WifiManager.WIFI_STATE_ENABLED:
                result = "WIFI_STATE_ENABLED";
                break;
            case WifiManager.WIFI_STATE_ENABLING:
                result = "WIFI_STATE_ENABLING";
                break;
            case WifiManager.WIFI_STATE_UNKNOWN:
                result = "WIFI_STATE_UNKNOWN";
                break;
            default:
        }
        return result;
    }
    
    private Handler mHanlder = new Handler();

    private Runnable task = new Runnable() {

        @Override
        public void run() {
            Log.i(TAG, "WifiManagerActivity---new Runnable run!");
            conf.SSID = convertToQuotedString(SSID);
            conf.preSharedKey = convertToQuotedString(PSWD);
            conf.hiddenSSID = true;
            conf.status = WifiConfiguration.Status.ENABLED;
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            int id = mWifiManager.addNetwork(conf);
            mWifiManager.enableNetwork(id, true);
            info = mWifiManager.getConnectionInfo();
            Log.i(TAG, "info = " + info);
            if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED && isNetworkConnected()) {
                Log.i(TAG, "WifiManagerActivity---Runnable over!");
                mHanlder.removeCallbacks(task);
            } else {
                Log.i(TAG, "WifiManagerActivity---new task run!");
                mHanlder.postDelayed(task, DELATE_TIME);
            }
        }
    };

    private boolean isNetworkConnected() {
        Log.i(TAG, "WifiManagerActivity---isNetworkConnected");
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressWarnings("deprecation")
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        Log.i(TAG, "networkInfo = " + networkInfo);
        if(networkInfo == null){
            return false;
        }
        return networkInfo.isAvailable();
    }

    public void register(){
        Call register = apiInterface.register(
                sharedPrefManager.getSpIdDevice(),
                sharedPrefManager.getSpSecretKey()
        );
        register.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("respon", response.toString());
            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_konek:

                if (edt_ssid.getText().toString().isEmpty()){
                    edt_ssid.setError("Masukkan SSID");
                    edt_ssid.setFocusable(true);
                    return;
                }

                if (isInternetAvailable()==true){
                    // setup the alert builder
                    String message = "Matikan data selluler terlebih dahulu dan pastikan WiFi tersambung dengan " + sharedPrefManager.getSpDeviceSsid() + ".";
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Matikan Data Selluler");
                    builder.setMessage(message);

                    // add a button
                    builder.setPositiveButton("OK", null);

                    // create and show the alert dialog
                    AlertDialog dialog = builder.create();
                    dialog.show();

                } else {
                    register();

                    Call device_config = apiInterface.config(
                            edt_ssid.getText().toString(),
                            edt_pass.getText().toString(),
                            sharedPrefManager.getSpDevicePass(),
                            sharedPrefManager.getSpSecretKey()
                    );

                    sharedPrefManager.saveSPString(SharedPrefManager.SP_SSID, edt_ssid.getText().toString());
                    sharedPrefManager.saveSPString(SharedPrefManager.SP_PASS, edt_pass.getText().toString());

                    device_config.enqueue(new Callback() {
                        @Override
                        public void onResponse(Call call, Response response) {
                            Log.d("responsee", response.toString());
                            mWifiManager.setWifiEnabled(false);

                        }

                        @Override
                        public void onFailure(Call call, Throwable t) {

                        }
                    });

                    startActivity(new Intent(AddWifiActivity.this, HomeFeederActivity.class));
                    finish();
                }
                break;

        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void turnOffData(){
        // setup the alert builder
        String message = "Matikan data selluler dan pastikan WiFi tersambung dengan " + sharedPrefManager.getSpDeviceSsid() + ".";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Matikan Data Selluler");
        builder.setMessage(message);

        // add a button
        builder.setPositiveButton("OK", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        //intent to android settings data
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.setComponent(new ComponentName("com.android.settings",
//                "com.android.settings.Settings$DataUsageSummaryActivity"));
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
    }

    public boolean isInternetAvailable() {
        try {
            InetAddress address = InetAddress.getByName("www.google.com");
            return !address.equals("");
        } catch (UnknownHostException e) {
            // Log error
        }
        return false;
    }

}