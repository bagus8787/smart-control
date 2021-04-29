package com.example.smart_control.receiver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import com.example.smart_control.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

public class WifiActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private WifiManager mWifiManager;
    private WifiConfiguration conf;
    private WifiInfo info;
    private File mFile;

    private static String CONFIG_PATH_RELATIVE = "/pre_resource/config/config.txt";
    public static String config_path = "";
    public static String SSID ;
    public static String PSWD ;
    public static String SSID_CONFIG = "ssid";
    public static String PSWD_CONFIG = "pswd";
    private static int DELATE_TIME = 5000;

    EditText edt_ssid, edt_pass;
    Button btn_konek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        edt_ssid = findViewById(R.id.edt_ssid);
        edt_pass = findViewById(R.id.edt_pass);

        btn_konek = findViewById(R.id.btn_konek);

        btn_konek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "WifiManagerActivity---connectedClick");

                if (edt_ssid.getText().toString().isEmpty()){
                    edt_ssid.setError("SSID harus diisi");
                    edt_ssid.requestFocus();
                    return;
                }

                if (edt_pass.getText().toString().isEmpty()){
                    edt_pass.setError("Password harus diisi");
                    edt_pass.requestFocus();
                    return;
                }

                SSID = edt_ssid.getText().toString();
                PSWD = edt_pass.getText().toString();

                startWifiConnected();
            }
        });

        mWifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        conf = new WifiConfiguration();
        Log.d("mFile", conf.toString());

//        external wifi
        config_path = Environment.getExternalStorageDirectory().getAbsolutePath() + CONFIG_PATH_RELATIVE;
        mFile = new File(config_path);

        Log.d("mFile", mFile.toString());

        GetWifiConnectedState();

        if (mWifiManager != null && !mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);

        }
    }

    public void connectedClick(View view) {
        Log.i(TAG, "WifiManagerActivity---connectedClick");

        SSID = edt_ssid.getText().toString();
        PSWD = edt_pass.getText().toString();

        startWifiConnected();
    }

    public void startWifiConnected() {
        Log.i(TAG, "WifiManagerActivity---startWifiConnected");

        Log.d("SSID", SSID);

//        connectWifi(SSID, PSWD);

        if (mWifiManager != null && !mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);

            connectWifi(SSID, PSWD);
        }
        if (mFile.exists()) {
            Dictionary<String, String> mdDictionary = readConfig();
            if (!mdDictionary.isEmpty()) {
                SSID = mdDictionary.get(SSID_CONFIG);
                PSWD = mdDictionary.get(PSWD_CONFIG);
                Log.i(TAG, "SSID = " + SSID);
                Log.i(TAG, "PSWD = " + PSWD);
                connectWifi(SSID, PSWD);

                Log.d("SSID", SSID.toString());
                Log.d("PSWD", PSWD.toString());

                // mHanlder.post(task);
            }
        }
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
}
