package com.example.smart_control.ui.user.feeder;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.PopUpToBuilder;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smart_control.R;
import com.example.smart_control.mqtt.MqttTesActivity;
import com.example.smart_control.network.ApiInterface;
import com.example.smart_control.network.ApiLocalClient;
import com.example.smart_control.receiver.WifiReceiver;
import com.example.smart_control.utils.SharedPrefManager;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.io.DataOutputStream;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import cz.msebera.android.httpclient.util.TextUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddWifiActivity extends AppCompatActivity implements View.OnClickListener {

    String LOG = "WifiScanActivity";

    int bv = Build.VERSION.SDK_INT;
    private final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 1;

    public static String SSID_DEVICE, SSID_WIFI;
    public static String PSWD_DEVICE;
    private static int DELATE_TIME = 5000;

    Button btn_konek;
    TextView edt_pass;
    SearchableSpinner spinner_wifi;

    SharedPrefManager sharedPrefManager;
    ApiInterface apiInterface;

    WifiManager mWifiManager;
    WifiReceiver receiverWifi;
    ListView wifiList;

    private static final String TAG = "ScanDeviceSSID";
    private WifiConfiguration conf;
    private WifiInfo info;
    private File mFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wifi);

        sharedPrefManager = new SharedPrefManager(this);
        apiInterface = ApiLocalClient.getClient().create(ApiInterface.class);

        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        conf = new WifiConfiguration();

        SSID_DEVICE = "FEEDR-" + sharedPrefManager.getSpIdDevice();
        PSWD_DEVICE = "12348765";

        wifiList = findViewById(R.id.wifiList);

        btn_konek = findViewById(R.id.btn_konek);
        edt_pass = findViewById(R.id.edt_pass);

        spinner_wifi = findViewById(R.id.spinner_wifi);
        spinner_wifi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorBlack));
                String nama = parent.getItemAtPosition(position).toString();
                setSSID(nama);
//                Toast.makeText(AddWifiActivity.this, nama, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn_konek.setOnClickListener(this);

        mWifiManager.setWifiEnabled(true);
        WifiManager wifiManager =
                (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        readtvWifiState(wifiManager);

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        Log.d("infowifineee", "= " + wifiInfo.toString());

        if (wifiInfo.getSupplicantState().toString() == "DISCONNECTED") {
//            Log.d("infowifi", "= " + wifiInfo.getSSID());
            Log.d("infowifi", "= " + wifiInfo.getSupplicantState());

        } else {
            Log.d("infowifi", "= " + wifiInfo.getSSID());
        }

        getCurrentSsid(getApplicationContext());

        startWifiConnected();

        sharedPrefManager.saveSPString(SharedPrefManager.SP_DEVICE_SSID, SSID_DEVICE);
        Log.d("SSIDDDD", sharedPrefManager.getSpDeviceSsid());

        sharedPrefManager.saveSPString(SharedPrefManager.SP_DEVICE_PASS, PSWD_DEVICE);

        if (isInternetAvailable() == true) {
            turnOffData();
        }
    }

    public void setSSID(String SSIDs) {
        SSID_WIFI = SSIDs;
        Log.d(LOG, "= " + SSID_WIFI);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        receiverWifi = new WifiReceiver(mWifiManager, wifiList, spinner_wifi);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(receiverWifi, intentFilter);
        getWifi();
    }

    private void getWifi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            Toast.makeText(AddWifiActivity.this, "version> = marshmallow", Toast.LENGTH_SHORT).show();
            if (ContextCompat.checkSelfPermission(AddWifiActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(AddWifiActivity.this, "location turned off", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(AddWifiActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
            } else {
//                Toast.makeText(AddWifiActivity.this, "location turned on", Toast.LENGTH_SHORT).show();
                mWifiManager.startScan();
            }
        } else {
//            Toast.makeText(AddWifiActivity.this, "scanning", Toast.LENGTH_SHORT).show();
            mWifiManager.startScan();
        }
    }

    public void startWifiConnected() {
        Log.i(TAG, "WifiManagerActivity---startWifiConnected");
        if (mWifiManager != null && !mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);

            connectWifi(SSID_DEVICE, PSWD_DEVICE);
            Log.d("SSIDDDDD", "= " + SSID_DEVICE);
            Log.d("PASWWDD", "= " + PSWD_DEVICE);

        } else {
            connectWifi(SSID_DEVICE, PSWD_DEVICE);
            Log.d("SSIDDDDD", "= " + SSID_DEVICE);
            Log.d("PASWWDDZZZ", "= " + PSWD_DEVICE);
        }

    }

    public void connectWifi(final String ssid, final String pswd) {
        Log.i(TAG, "WifiManagerActivity---connectWifi");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "new Thread run!");

                Log.d(TAG, SSID_DEVICE);
                Log.d(TAG, PSWD_DEVICE);

                WifiConfiguration conf = new WifiConfiguration();
                conf.SSID = convertToQuotedString(SSID_DEVICE);
                conf.preSharedKey = convertToQuotedString(PSWD_DEVICE);
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

    private String readtvWifiState(WifiManager wm) {
        String result = "";
        switch (wm.getWifiState()) {
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
            conf.SSID = convertToQuotedString(SSID_DEVICE);
            conf.preSharedKey = convertToQuotedString(PSWD_DEVICE);
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
        if (networkInfo == null) {
            return false;
        }
        return networkInfo.isAvailable();
    }

    public void register() {
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
        switch (v.getId()) {
            case R.id.btn_konek:
                Log.d(LOG, "= " + SSID_WIFI);

                if (isInternetAvailable() == true) {
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
                            SSID_WIFI,
                            edt_pass.getText().toString(),
                            sharedPrefManager.getSpDevicePass(),
                            sharedPrefManager.getSpSecretKey()
                    );

                    sharedPrefManager.saveSPString(SharedPrefManager.SP_SSID, SSID_WIFI);
                    sharedPrefManager.saveSPString(SharedPrefManager.SP_PASS, edt_pass.getText().toString());

                    device_config.enqueue(new Callback() {
                        @Override
                        public void onResponse(Call call, Response response) {
                            Log.d("responsee", response.toString());

                        }

                        @Override
                        public void onFailure(Call call, Throwable t) {

                        }
                    });

                    mWifiManager.setWifiEnabled(false);

                    ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.show();
                    progressDialog.setMessage("Mohon tunggu sebentar");
                    progressDialog.setCancelable(false);
                    int SPLASH_DISPLAY_LENGHT = 5000;

                    if (isInternetAvailable()==true){
                        new Handler().postDelayed(new Runnable(){
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                /* Create an Intent that will start the Menu-Activity. */
                                Intent mainIntent = new Intent(AddWifiActivity.this,HomeFeederActivity.class);
                                startActivity(mainIntent);
                                finish();
                            }
                        }, SPLASH_DISPLAY_LENGHT);
                    } else {
                        mWifiManager.setWifiEnabled(true);
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            List<WifiConfiguration> list = mWifiManager.getConfiguredNetworks();
                            for( WifiConfiguration i : list ) {
                                Log.d(LOG, SSID_WIFI);
                                if(i.SSID != null && i.SSID.equals("\"" + SSID_WIFI + "\"")) {
                                    mWifiManager.disconnect();
                                    mWifiManager.enableNetwork(i.networkId, true);
                                    mWifiManager.reconnect();
                                }
                            }
                        }
                        new Handler().postDelayed(new Runnable(){
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                /* Create an Intent that will start the Menu-Activity. */
                                Intent mainIntent = new Intent(AddWifiActivity.this,HomeFeederActivity.class);
                                startActivity(mainIntent);
                                finish();
                            }
                        }, SPLASH_DISPLAY_LENGHT);
                    }

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