package com.example.smart_control.ui.user.feeder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.smart_control.R;
import com.example.smart_control.network.ApiInterface;
import com.example.smart_control.network.ApiLocalClient;
import com.example.smart_control.ui.loginFirebase.LoginFirebaseActivity;
import com.example.smart_control.utils.SharedPrefManager;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import cz.msebera.android.httpclient.util.TextUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanWifiActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private WifiManager mWifiManager;
    private WifiConfiguration conf;
    private WifiInfo info;
    private File mFile;

    private static String CONFIG_PATH_RELATIVE = "/pre_resource/config_ku/config.txt";
    public static String config_path = "";
    public static String SSID ;
    public static String PSWD ;
    public static String SSID_CONFIG = "ssid";
    public static String PSWD_CONFIG = "pswd";
    private static int DELATE_TIME = 5000;

    SharedPrefManager sharedPrefManager;
    ApiInterface apiInterface;

    EditText edt_ssid, edt_pass;
    Button btn_konek;
    ImageView img_back;
//    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_wifi);

        sharedPrefManager = new SharedPrefManager(this);
        apiInterface = ApiLocalClient.getClient().create(ApiInterface.class);

        Log.d("wifipppp", "ssid wifi= " + sharedPrefManager.getSpSsid() +"pass wifi= " + sharedPrefManager.getSpPass() +"pass devices= " + PSWD);

//        name = getIntent().getStringExtra("name");

        edt_ssid = findViewById(R.id.edt_ssid);
        edt_pass = findViewById(R.id.edt_pass);

        btn_konek = findViewById(R.id.btn_konek);

        img_back = findViewById(R.id.img_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ScanWifiActivity.this, SettingActivity.class));
            }
        });

        edt_ssid.setText("FEEDR-" + sharedPrefManager.getSpIdDevice());

        mWifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        conf = new WifiConfiguration();
        Log.d("mFile", conf.toString());

//        external wifi
        config_path = Environment.getExternalStorageDirectory().getAbsolutePath() + CONFIG_PATH_RELATIVE;
        mFile = new File(config_path);

        Log.d("mFile", mFile.toString());

//        GetWifiConnectedState();
        mWifiManager.setWifiEnabled(true);

        WifiManager wifiManager =
                (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        readtvWifiState(wifiManager);

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        Log.d("infowifineee", "= " + wifiInfo.toString());

//        String status = wifiInfo.getSupplicantState().toString();

        if(wifiInfo.getSupplicantState().toString() == "DISCONNECTED"){
//            Log.d("infowifi", "= " + wifiInfo.getSSID());
            Log.d("infowifi", "= " + wifiInfo.getSupplicantState());

        }else{
            Log.d("infowifi", "= " + wifiInfo.getSSID());
        }

        getCurrentSsid(getApplicationContext());

        btn_konek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!localConnected()){
                    Toast.makeText(ScanWifiActivity.this, "Matikan data selluler terlebih dahulu dan pastikan WiFi tersambung dengan " + sharedPrefManager.getSpDeviceSsid() + ".", Toast.LENGTH_LONG).show();
                } else {
                    Log.i(TAG, "WifiManagerActivity---connectedClick");

                    if (edt_ssid.getText().toString().isEmpty()){
                        edt_ssid.setError("SSID harus diisi");
                        edt_ssid.requestFocus();
                        return;
                    }

                    if (edt_pass.getText().toString().isEmpty()){
                        edt_pass.setText("12348765");
                    }

//                    SSID = edt_ssid.getText().toString();
                    PSWD = edt_pass.getText().toString();

//                    startWifiConnected();
                    Log.d("wifipppp", "ssid wifi= " + sharedPrefManager.getSpSsid() +" pass wifi= " + sharedPrefManager.getSpPass() +" pass devices= " + PSWD);
                    Call device_config = apiInterface.config(
                            sharedPrefManager.getSpSsid(),
                            sharedPrefManager.getSpPass(),
                            PSWD,
                            sharedPrefManager.getSpSecretKey()
                    );

                    device_config.enqueue(new Callback() {
                        @Override
                        public void onResponse(Call call, Response response) {
                            mWifiManager.setWifiEnabled(false);
                        }

                        @Override
                        public void onFailure(Call call, Throwable t) {

                        }
                    });

//                    sharedPrefManager.saveSPString(SharedPrefManager.SP_DEVICE_PASS, PSWD);

                    if (PSWD == null){
                        sharedPrefManager.saveSPString(SharedPrefManager.SP_DEVICE_PASS, "12348765");
                    } else {
                        sharedPrefManager.saveSPString(SharedPrefManager.SP_DEVICE_PASS, PSWD);
                    }
//                    sharedPrefManager.saveSPString(SharedPrefManager.SP_PASS, "12348765");

                    mWifiManager.setWifiEnabled(true);

                    Toast.makeText(ScanWifiActivity.this, "Device berhasil di konfigurasi ulang", Toast.LENGTH_LONG).show();

                    startActivity(new Intent(ScanWifiActivity.this, SettingActivity.class));
                }

            }
        });
    }

    // "android.permission.ACCESS_WIFI_STATE" is needed
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

    public void connectedClick(View view) {
        Log.i(TAG, "WifiManagerActivity---connectedClick");

        SSID = edt_ssid.getText().toString();
        PSWD = edt_pass.getText().toString();

        startWifiConnected();
    }

    public void startWifiConnected() {
        Log.i(TAG, "WifiManagerActivity---startWifiConnected");
//        connectWifi(SSID, PSWD);

        if (mWifiManager != null && !mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);

            connectWifi(SSID, PSWD);
            Log.d("SSIDDDDD", "= " + SSID);

        } else {
            connectWifi(SSID, PSWD);
            Log.d("SSIDDDDD", "= " + SSID);
        }

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

    public void connectWifi(final String ssid, final String pswd) {
        Log.i(TAG, "WifiManagerActivity---connectWifi");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "new Thread run!");
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
            }

        }).start();
    }

    public static String convertToQuotedString(String string) {
        return "\"" + string + "\"";
    }

    @SuppressWarnings("resource")
    public Dictionary<String, String> readConfig() {
        Log.i(TAG, "WifiManagerActivity---readConfig");
        Dictionary<String, String> configDict = new Hashtable<String, String>();
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(mFile), "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String mimeTypeLine = "";
            List<String> config_listList = new LinkedList<String>();
            while ((mimeTypeLine = br.readLine()) != null) {
                config_listList.add(mimeTypeLine);
            }
            if (config_listList.size() >= 1) {
                for (int i = 0; i < config_listList.size(); i++) {
                    String string = config_listList.get(i);
                    if (string != null) {
                        string = string.replace(" ", "");
                        String[] params = string.split(":");
                        if (params.length == 2) {
                            Log.i(TAG, "params[0] = " + params[0] + "   params[1] = " + params[1]);
                            configDict.put(params[0], params[1]);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return configDict;
    }

    public boolean GetWifiConnectedState() {
        Log.i(TAG, "WifiManagerActivity---GetWifiConnectedState");
        info = mWifiManager.getConnectionInfo();
        if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            Log.i(TAG, "info.getSSID() = " + info.getSSID());
            Log.i(TAG, "SSID = " + SSID);
            return info.getSSID().equals(convertToQuotedString(SSID));
        } else {
            return false;
        }
    }

    public boolean localConnected() {
        ConnectivityManager cm =
                (ConnectivityManager)ScanWifiActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        String SSID = "\"FEEDR-"+ sharedPrefManager.getSpIdDevice().toString() + "\"";

        Log.d("networkkk", activeNetwork.getExtraInfo().toString() + "= " + "\"FEEDR-"+ sharedPrefManager.getSpIdDevice().toString() + "\"");

        if (activeNetwork.getExtraInfo().equals(SSID)){
            return true;
        }
        return false;

//        try {
//            InetAddress address = InetAddress.getByName("www.google.com");
//            return !address.equals("");
//        } catch (UnknownHostException e) {
//            // Log error
//        }
//
//        return false;
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
//        startActivity(new Intent(ScanWifiActivity.this, LoginFirebaseActivity.class));
//        finish();
    }
}