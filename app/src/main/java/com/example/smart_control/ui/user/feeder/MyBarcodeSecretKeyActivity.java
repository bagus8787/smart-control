package com.example.smart_control.ui.user.feeder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smart_control.R;
import com.example.smart_control.model.BarcodeImage;
import com.example.smart_control.network.ApiBarcode;
import com.example.smart_control.network.ApiInterface;
import com.example.smart_control.ui.loginFirebase.LoginFirebaseActivity;
import com.example.smart_control.utils.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyBarcodeSecretKeyActivity extends AppCompatActivity {

    ImageView img_my_barcode;
    TextView txt_no_data;
    SharedPrefManager sharedPrefManager;
    ApiInterface apiInterface;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_barcode_secret_key);

        context = getApplicationContext();
        sharedPrefManager = new SharedPrefManager(context);
        apiInterface = ApiBarcode.getClient().create(ApiInterface.class);
        img_my_barcode = findViewById(R.id.img_my_barcode);
        txt_no_data = findViewById(R.id.txt_no_data);

        Call<String> myBarcode = apiInterface.getMyBarcode(sharedPrefManager.getSpSecretKey());
        Log.d("myBarcodesss", myBarcode.toString());

        myBarcode.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
//                Log.d("TextResponse", "Test");
                String textResponse = response.body();
//                Log.d("TextResponse", textResponse);

                BarcodeImage barcode = new BarcodeImage(textResponse);
                img_my_barcode.setImageBitmap(barcode.getImage());
                img_my_barcode.setVisibility(View.VISIBLE);
                Log.d("TextResponse", barcode.getBarcodeString());

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("TextResponse", "Failed");
                txt_no_data.setVisibility(View.VISIBLE);
            }
        });

    }
}