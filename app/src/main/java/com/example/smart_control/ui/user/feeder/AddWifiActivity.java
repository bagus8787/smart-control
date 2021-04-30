package com.example.smart_control.ui.user.feeder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.smart_control.R;

public class AddWifiActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_konek;
    TextView edt_ssid, edt_pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wifi);

        btn_konek   = findViewById(R.id.btn_konek);

        edt_ssid    = findViewById(R.id.edt_ssid);
        edt_pass    = findViewById(R.id.edt_pass);

        btn_konek.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_konek:
                startActivity(new Intent(this, HomeFeederActivity.class));
                break;
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}