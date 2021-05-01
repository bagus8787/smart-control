package com.example.smart_control.ui.user.feeder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.PopUpToBuilder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.smart_control.R;
import com.example.smart_control.network.ApiInterface;
import com.example.smart_control.network.ApiLocalClient;
import com.example.smart_control.utils.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddWifiActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_konek;
    TextView edt_ssid, edt_pass;

    SharedPrefManager sharedPrefManager;
    ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wifi);

        sharedPrefManager = new SharedPrefManager(this);
        apiInterface = ApiLocalClient.getClient().create(ApiInterface.class);

        btn_konek   = findViewById(R.id.btn_konek);

        edt_ssid    = findViewById(R.id.edt_ssid);
        edt_pass    = findViewById(R.id.edt_pass);

        btn_konek.setOnClickListener(this);

    }

    public void register(){
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
        switch (v.getId()){
            case R.id.btn_konek:
                if (edt_ssid.getText().toString().isEmpty()){
                    edt_ssid.setError("Masukkan SSID");
                    return;
                }

                register();

                Call device_config = apiInterface.config(
                        edt_ssid.getText().toString(),
                        edt_pass.getText().toString(),
                        sharedPrefManager.getSpDevicePass(),
                        sharedPrefManager.getSpSecretKey()
                );

                device_config.enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        Log.d("responsee", response.toString());
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {

                    }
                });

                startActivity(new Intent(AddWifiActivity.this, HomeFeederActivity.class));
                break;
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}