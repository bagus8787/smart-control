package com.example.smart_control.ui.user.activity.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.smart_control.R;
import com.example.smart_control.network.ApiClient;
import com.example.smart_control.network.ApiInterface;
import com.example.smart_control.ui.user.activity.HomeActivity;
import com.example.smart_control.utils.SharedPrefManager;

public class WelcomeActivity extends AppCompatActivity {

    Context mContext;
    ApiInterface apiInterface;
    SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mContext = this;

        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        sharedPrefManager = new SharedPrefManager(this);

        Log.d("cek" + "=",String.valueOf(sharedPrefManager.getSPSudahLogin()));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (sharedPrefManager.getSPSudahLogin()) {
                    Log.d("login", String.valueOf(sharedPrefManager.getSPSudahLogin()));
                    Intent login = new Intent(WelcomeActivity.this, HomeActivity.class);
                    startActivity(login);
                    finish();
                } else {
//                    if (sharedPrefManager.getRole().equals(Role.ROLE_MASYARAKAT)) {
//                        startActivity(new Intent(WelcomeActivity.this, HomeUserActivity.class)
//                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
//                        finish();
//                    } else if (sharedPrefManager.getRole().equals(Role.ROLE_KEPALA_DESA)) {
//                        startActivity(new Intent(WelcomeActivity.this, HomeUserActivity.class)
//                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
//                        finish();
////                    } else if (sharedPrefManager.getRole().equals(Role.ROLE_ADMIN)) {
////                        startActivity(new Intent(WelcomeActivity.this, HomeAdminActivity.class)
////                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
////                        finish();
//                    }
                }
            }
        }, 0);

        Button btn_login    = findViewById(R.id.btn_login);
        Button btn_daftar   = findViewById(R.id.btn_daftar);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent b = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(b);
            }
        });

        btn_daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent d = new Intent(WelcomeActivity.this, RegisterActivity.class);
                startActivity(d);
            }
        });



//        getSupportActionBar().hide();
    }

}