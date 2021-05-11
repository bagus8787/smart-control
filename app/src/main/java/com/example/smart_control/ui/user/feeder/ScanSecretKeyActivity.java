package com.example.smart_control.ui.user.feeder;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.smart_control.R;
import com.example.smart_control.network.ApiInterface;
import com.example.smart_control.network.ApiLocalClient;
import com.example.smart_control.ui.loginFirebase.LoginFirebaseActivity;
import com.example.smart_control.utils.SharedPrefManager;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.loopj.android.http.Base64;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ScanSecretKeyActivity extends AppCompatActivity {

    IntentIntegrator intentIntegrator;
    WifiManager mWifiManager;
    SharedPrefManager sharedPrefManager;
    ApiInterface apiInterface;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_device);

        sharedPrefManager = new SharedPrefManager(this);
        apiInterface = ApiLocalClient.getClient().create(ApiInterface.class);
        context = getApplicationContext();

//        Set WIFI to enabled
        mWifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        mWifiManager.setWifiEnabled(false);

        intentIntegrator = new IntentIntegrator(ScanSecretKeyActivity.this);
        intentIntegrator.setCaptureActivity(Potrait.class);
        intentIntegrator.setCameraId(0);
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        intentIntegrator.setPrompt("Scan Secret Key");
        intentIntegrator.initiateScan();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Scan
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null){
            if (result.getContents() == null){
                Toast.makeText(this, "Hasil tidak ditemukan", Toast.LENGTH_LONG).show();
//                startActivity(new Intent(this, ScanWifiActivity.class));
                onBackPressed();

            }else{
//                String device_id = result.getContents();
//                String device_id = "USW1000001";

                String secret_key = result.getContents().toString();

                // Receiving side
                byte[] datas = Base64.decode(secret_key, Base64.DEFAULT);
                String text = new String(datas, StandardCharsets.UTF_8);

                String device_id = text.substring(0,10);
                String secret_key_usr = text.substring(11);

                Log.d("secret_key_after", text + " == " + device_id + "||||" + secret_key_usr);

                sharedPrefManager.saveSPString(SharedPrefManager.SP_ID_DEVICE, device_id);
                sharedPrefManager.saveSPString(SharedPrefManager.SP_SECRET_KEY, text);

                Log.d("devices_id", sharedPrefManager.getSpIdDevice() + "||||" + sharedPrefManager.getSpSecretKey());

                Toast.makeText(context, text, Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, HomeFeederActivity.class)
                        .putExtra("secret_key", text));

                Log.d("SHAREDSSID", "=" + sharedPrefManager.getSpDeviceSsid());
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

}