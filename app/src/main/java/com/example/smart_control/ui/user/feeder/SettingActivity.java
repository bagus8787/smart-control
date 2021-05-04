package com.example.smart_control.ui.user.feeder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smart_control.R;
import com.example.smart_control.ui.loginFirebase.LoginFirebaseActivity;
import com.example.smart_control.utils.SharedPrefManager;
import com.google.firebase.auth.FirebaseAuth;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;

    ImageView img_logout;
    TextView txt_nama, txt_email;

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

        txt_nama.setText(sharedPrefManager.getSpNama());
        txt_email.setText(sharedPrefManager.getSpEmail());

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
            case R.id.img_logout:
                auth.signOut();

                sharedPrefManager.saveSPString(SharedPrefManager.SP_SSID, "");

                sharedPrefManager.setSpDeviceSsid("");
                startActivity(new Intent(SettingActivity.this, LoginFirebaseActivity.class));
                break;
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}