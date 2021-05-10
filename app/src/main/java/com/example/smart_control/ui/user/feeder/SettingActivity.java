package com.example.smart_control.ui.user.feeder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.smart_control.R;
import com.example.smart_control.ui.loginFirebase.LoginFirebaseActivity;
import com.example.smart_control.utils.SharedPrefManager;
import com.google.firebase.auth.FirebaseAuth;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;

    ImageView img_logout, img_back;
    TextView txt_nama, txt_email,txt_device_id;
    RelativeLayout rv_logout, rv_my_barcode, rv_setting;

    SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        sharedPrefManager = new SharedPrefManager(this);
        auth = FirebaseAuth.getInstance();
        Log.d("autaaaaaah", "= " + auth.getTenantId());

        txt_nama    = findViewById(R.id.txt_nama);
        txt_email   = findViewById(R.id.txt_email);
        txt_device_id = findViewById(R.id.txt_device_id);

        img_back = findViewById(R.id.img_back);

        rv_logout   = findViewById(R.id.rv_logout);
        rv_my_barcode = findViewById(R.id.rv_my_barcode);
        rv_setting  = findViewById(R.id.rv_setting);

        rv_logout.setOnClickListener(this);
        rv_my_barcode.setOnClickListener(this);
        rv_setting.setOnClickListener(this);

        img_back.setOnClickListener(this);

        txt_nama.setText(sharedPrefManager.getSpNama());
        txt_email.setText(sharedPrefManager.getSpEmail());
        txt_device_id.setText("Device ID : " + sharedPrefManager.getSpIdDevice());

//      Auth Listener
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(SettingActivity.this, LoginFirebaseActivity.class));
                    finish();
                }
            }
        };

        img_logout  = findViewById(R.id.img_logout);
        img_logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rv_logout:
                AlertDialog.Builder b=  new  AlertDialog.Builder(this);
                b.setTitle("Apakah anda yakin mau log out ?");
                b.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // do something...
//                                sharedPrefManager.saveSPString(SharedPrefManager.SP_SSID, "");
//                                sharedPrefManager.saveSPString(SharedPrefManager.SP_ID_DEVICE, "");
//                                sharedPrefManager.saveSPString(SharedPrefManager.SP_SECRET_KEY, "");
                                auth.signOut();
                                sharedPrefManager.saveSPBoolean(SharedPrefManager.SP_SUDAH_LOGIN, false);

//                                sharedPrefManager.setSpDeviceSsid("");
                                startActivity(new Intent(SettingActivity.this, LoginFirebaseActivity.class));
                            }
                        }
                );
                b.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }
                );
                b.show();
                break;

            case R.id.img_back:
                startActivity(new Intent(SettingActivity.this, HomeFeederActivity.class));
                break;

            case R.id.rv_my_barcode:
                startActivity(new Intent(SettingActivity.this, MyBarcodeSecretKeyActivity.class));
                break;

            case R.id.rv_setting:
                startActivity(new Intent(SettingActivity.this, ScanWifiActivity.class));
                break;
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(SettingActivity.this, HomeFeederActivity.class));
    }
}