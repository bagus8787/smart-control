package com.example.smart_control.ui.user.feeder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.smart_control.R;
import com.example.smart_control.utils.SharedPrefManager;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.UUID;

public class ScanDeviceActivity extends AppCompatActivity {

    IntentIntegrator intentIntegrator;
    WifiManager mWifiManager;
    SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_device);

        sharedPrefManager = new SharedPrefManager(this);

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
                String hasil = result.getContents();
                String secret_key = hasil + "-" + UUID.randomUUID().toString();

                Log.d("secret_key", secret_key);

                sharedPrefManager.saveSPString(SharedPrefManager.SP_DEVICE_SSID, String.valueOf(hasil));
                sharedPrefManager.saveSPString(SharedPrefManager.SP_SECRET_KEY, secret_key);

                Toast.makeText(this, hasil, Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, ScanWifiActivity.class)
                .putExtra("name", hasil));

//                Log.d("SHAREDSSID", "=" + sharedPrefManager.getSpDeviceSsid());
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}