package com.example.smart_control.ui.user.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.smart_control.R;
import com.example.smart_control.handler.DatabaseHandler;
import com.example.smart_control.model.AlarmModel;
import com.example.smart_control.mqtt.MqttHelper;
import com.example.smart_control.network.ApiInterface;
import com.example.smart_control.network.ApiLocalClient;
import com.example.smart_control.ui.user.feeder.HomeFeederActivity;
import com.example.smart_control.ui.user.feeder.TimerActivity;
import com.example.smart_control.utils.SharedPrefManager;
import com.google.android.gms.common.api.Api;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailDevicesActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView mImageView, img_back;
    TextView mResultView;
    View mColorView;

    Bitmap bitmap;
    String id, time, count, time_now;
    EditText ip_time_before, ip_count, ip_count_new;
    Button btn_update_timer;

    TimePicker datePicker1;
    ProgressDialog mDialog;
    Context context;
    SharedPrefManager sharedPrefManager;
    ApiInterface apiInterface;
    MqttHelper mqttHelper;
    DatabaseHandler db;
    AlarmModel alarmModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_devices);

        context = getApplicationContext();
        sharedPrefManager = new SharedPrefManager(context);
        apiInterface = ApiLocalClient.getClient().create(ApiInterface.class);
        mqttHelper = new MqttHelper(context);
        mDialog = new ProgressDialog(DetailDevicesActivity.this);
        alarmModel = new AlarmModel();
        db = DatabaseHandler.getInstance(context);

        id  = getIntent().getStringExtra("IT_ID");
        time  = getIntent().getStringExtra("IT_TIME");
        count  = getIntent().getStringExtra("IT_COUNT");

        Log.d("idneyyayaa", "== " + id);

        ip_time_before = findViewById(R.id.ip_time_before);
        ip_count = findViewById(R.id.ip_count);
        ip_count_new = findViewById(R.id.ip_count_new);

        img_back = findViewById(R.id.img_back);
        img_back.setOnClickListener(this);

        datePicker1 = findViewById(R.id.datePicker1);
        btn_update_timer = findViewById(R.id.btn_update_timer);

        datePicker1.setIs24HourView(true);

        ip_time_before.setText(time);
        ip_count.setText(count);

        datePicker1.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                time_now = String.format("%02d:%02d",hourOfDay, minute);
                setTime(time_now);
            }

        });

        btn_update_timer.setOnClickListener(this);

        mImageView = findViewById(R.id.imageView);
        mResultView = findViewById(R.id.result);
        mColorView = findViewById(R.id.colorView);

        mImageView.setDrawingCacheEnabled(true);
        mImageView.buildDrawingCache(true);

        mImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE){
                    bitmap = mImageView.getDrawingCache();

                    int pixel = bitmap.getPixel((int) event.getX(), (int)event.getY());

                    int r   = Color.red(pixel);
                    int g   = Color.green(pixel);
                    int b   = Color.blue(pixel);

                    String hex = "#" + Integer.toHexString(pixel);

                    mColorView.setBackgroundColor(Color.rgb(r,g,b));

                    mResultView.setText("RGB: "+ r +", " +g +", "+b
                            + "\nHEX: " + hex);

                }
                return true;
            }
        });
    }

    private void setTime(String time) {
        time_now = time;
        Log.d("timessskkk", "= " + time_now);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_update_timer:
                Log.d("networkkkk", "= " + isInternetConnected());
                if (isInternetConnected()){
                    AlertDialog.Builder b=  new  AlertDialog.Builder(this);
                            b.setTitle("Apakah anda yakin memperbarui jadwal pakan ?");
                            b.setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            // do something...
                                            setOnlineAlarm();
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
                } else {
                    AlertDialog.Builder b=  new  AlertDialog.Builder(this);
                            b.setTitle("Apakah anda yakin memperbarui jadwal pakan ?");
                            b.setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            // do something...
                                            setOfflineAlarm();

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

                }

                break;

            case R.id.img_back:
                startActivity(new Intent(context, HomeFeederActivity.class));
                break;
        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void setOfflineAlarm(){
        mDialog.show();
        mDialog.setMessage("Sedang memperbarui jadwal pakan. Mohon tunggu sebentar...");
        Integer count_new = Integer.valueOf(ip_count_new.getText().toString());

        Call<String> edit = apiInterface.editAlarm(
                time_now,
                count_new,
                time,
                sharedPrefManager.getSpSecretKey());

        Log.d("offlineEditAlarm", edit.toString());
        edit.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
//                Log.d("respon_Add" , "=" + response.body().toString());
                mDialog.dismiss();
                Toast.makeText(context, "Timer berhasil di perbarui", Toast.LENGTH_LONG).show();
                startActivity(new Intent(context, HomeFeederActivity.class));
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

        alarmModel.setId(Integer.parseInt(id));
        alarmModel.setName("adadasd");
        alarmModel.setTall("casca");
        alarmModel.setTime(time_now);
        alarmModel.setCount(ip_count_new.getText().toString());
        alarmModel.setOld_time(time);

        db.updateAlarm(alarmModel);

        Log.d("dbbbbb", String.valueOf(alarmModel.getId() + "==" + alarmModel.getTime() + db.updateAlarm(alarmModel)));
        Toast.makeText(this, "Timer berhasil di perbarui", Toast.LENGTH_LONG).show();
        startActivity(new Intent(context, HomeFeederActivity.class));
    }
    private void setOnlineAlarm() {
        Log.d("timee", time_now);
        Log.d("secret_keyssss", sharedPrefManager.getSpSecretKey());

        String json = "{\"time\":\""+ time_now +"\",\"count\":"+Integer.parseInt(ip_count_new.getText().toString())+",\"old_time\":\""+ time +"\",\"secret_key\":"+sharedPrefManager.getSpSecretKey()+"}";
        String user = "";

        try {
            mDialog.show();
            mDialog.setMessage("Sedang memperbarui jadwal pakan. Mohon tunggu sebentar...");
            Log.d("mqttttsss", String.valueOf(mqttHelper.mqttAndroidClient.isConnected()));
            if (!mqttHelper.mqttAndroidClient.isConnected()){
                mqttHelper.mqttAndroidClient.connect();
                mDialog.dismiss();
                Toast.makeText(context, "Server disconnect", Toast.LENGTH_LONG).show();
                return;
            } else {
                MqttMessage message = new MqttMessage();
                message.setPayload(json.getBytes());
                message.setQos(0);
                mqttHelper.mqttAndroidClient.publish(sharedPrefManager.getSpIdDevice() + "/control/timer/edit", message,null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.i("OnlineLog", "Message published= " + message.toString());

                        try {
                            //set time in mili
                            Thread.sleep(3000);

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        mDialog.dismiss();

//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                // change image
//                            }
//
//                        }, 1000); // 5000ms delay
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.i("TAGGGGGGGG", "publish failed!") ;
                    }
                });
            }

        } catch (MqttException e) {
            Log.e("TAG", e.toString());
            e.printStackTrace();
        }

        alarmModel.setId(Integer.parseInt(id));
        alarmModel.setName("adadasd");
        alarmModel.setTall("casca");
        alarmModel.setTime(time_now);
        alarmModel.setCount(ip_count_new.getText().toString());
        alarmModel.setOld_time(time);

        db.updateAlarm(alarmModel);

        Toast.makeText(this, "Timer berhasil di perbarui", Toast.LENGTH_LONG).show();
        startActivity(new Intent(context, HomeFeederActivity.class));
    }

    public boolean isInternetConnected() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            Toast.makeText(AddWifiActivity.this, "version> = marshmallow", Toast.LENGTH_SHORT).show();
            try {
                InetAddress address = InetAddress.getByName("www.google.com");
                return !address.equals("");
            } catch (UnknownHostException e) {
                // Log error
            }
            return false;
        } else {
            ConnectivityManager cm =
                    (ConnectivityManager) DetailDevicesActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            String SSID = "\"FEEDR-" + sharedPrefManager.getSpIdDevice().toString() + "\"";

            Log.d("networkkk", activeNetwork.getExtraInfo().toString() + "= " + "\"FEEDR-" + sharedPrefManager.getSpIdDevice().toString() + "\"");

            if (!activeNetwork.getExtraInfo().equals(SSID)) {
                return true;
            } else {
                return false;
            }
        }
    }
}