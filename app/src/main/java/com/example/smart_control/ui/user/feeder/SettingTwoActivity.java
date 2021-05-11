package com.example.smart_control.ui.user.feeder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.smart_control.R;

public class SettingTwoActivity extends AppCompatActivity implements View.OnClickListener {
    RelativeLayout rv_phone_to_device, rv_device_to_wifi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_two);

        rv_phone_to_device = findViewById(R.id.rv_phone_to_device);

        rv_phone_to_device.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

    }
}