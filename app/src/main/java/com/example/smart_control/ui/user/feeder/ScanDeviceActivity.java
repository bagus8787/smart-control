package com.example.smart_control.ui.user.feeder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.smart_control.R;
import com.example.smart_control.network.ApiInterface;
import com.example.smart_control.network.ApiLocalClient;
import com.example.smart_control.utils.SharedPrefManager;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanDeviceActivity extends AppCompatActivity {

    IntentIntegrator intentIntegrator;
    WifiManager mWifiManager;
    SharedPrefManager sharedPrefManager;
    ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_device);

        sharedPrefManager = new SharedPrefManager(this);
        apiInterface = ApiLocalClient.getClient().create(ApiInterface.class);

//        Set WIFI to enabled
        mWifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mWifiManager.setWifiEnabled(false);

        intentIntegrator = new IntentIntegrator(ScanDeviceActivity.this);
        intentIntegrator.setCaptureActivity(Potrait.class);
        intentIntegrator.setCameraId(0);
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        intentIntegrator.setPrompt("Scan Barcode Device");
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Scan
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null){
            if (result.getContents() == null){
                Toast.makeText(this, "Hasil tidak ditemukan", Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, ScanWifiActivity.class));
            }else{
                String device_id = result.getContents();
//                String device_id = "USW1000001";

                String secret_key = device_id + "-" + UUID.randomUUID().toString();
//                Log.d("secret_key", secret_key);

                sharedPrefManager.saveSPString(SharedPrefManager.SP_ID_DEVICE, String.valueOf(device_id));

                sharedPrefManager.saveSPString(SharedPrefManager.SP_DEVICE_SSID, String.valueOf(device_id));
                sharedPrefManager.saveSPString(SharedPrefManager.SP_SECRET_KEY, secret_key);

                Log.d("secret_key", sharedPrefManager.getSpIdDevice());

                Toast.makeText(this, device_id, Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, AddWifiActivity.class)
                .putExtra("name", device_id));

//                Log.d("SHAREDSSID", "=" + sharedPrefManager.getSpDeviceSsid());
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}