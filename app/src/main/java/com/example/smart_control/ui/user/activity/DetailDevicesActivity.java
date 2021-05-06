package com.example.smart_control.ui.user.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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
    ImageView mImageView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_devices);

        context = getApplicationContext();
        sharedPrefManager = new SharedPrefManager(context);
        apiInterface = ApiLocalClient.getClient().create(ApiInterface.class);
        mqttHelper = new MqttHelper(context);
        mDialog = new ProgressDialog(context);

        id  = getIntent().getStringExtra("IT_ID");
        time  = getIntent().getStringExtra("IT_TIME");
        count  = getIntent().getStringExtra("IT_COUNT");

        ip_time_before = findViewById(R.id.ip_time_before);
        ip_count = findViewById(R.id.ip_count);
        ip_count_new = findViewById(R.id.ip_count_new);

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
                if (isInternetAvailable()==true){
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
        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void setOfflineAlarm(){
//        mDialog.show();
//        mDialog.setMessage("Sedang memperbarui jadwal pakan. Mohon tunggu sebentar");
        Call<String> edit = apiInterface.editAlarm(
                time_now,
                Integer.parseInt(ip_count_new.getText().toString()),
                time,
                sharedPrefManager.getSpSecretKey());
        edit.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
//                Log.d("respon_Add" , "=" + response.body().toString());
//                mDialog.dismiss();
                Toast.makeText(context, "Timer berhasil di perbarui", Toast.LENGTH_LONG).show();
                startActivity(new Intent(context, HomeFeederActivity.class));
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }
    private void setOnlineAlarm() {
        Log.d("timee", time_now);
        Log.d("secret_keyssss", sharedPrefManager.getSpSecretKey());
        String json = "{\"time\" : \""+ time_now +"\" ,\"count\":"+ Integer.parseInt(ip_count_new.getText().toString()) +", \"old_time\":"+ time +", \"secret_key\":" + sharedPrefManager.getSpSecretKey() +"}";
        String user = "";
        try {
            JSONObject obj = new JSONObject(json);
            MqttHelper mqttHelperOnline;
            mqttHelperOnline = new MqttHelper(context);
            mqttHelperOnline.serverUri.toString();

            MemoryPersistence memPer = new MemoryPersistence();
            final MqttAndroidClient client = new MqttAndroidClient(
                    context, mqttHelper.serverUri.toString(), user, memPer);
            Log.d("clientt", client.toString());

            mDialog.setMessage("Sedang memperbarui jadwal pakan. Mohon tunggu sebentar");
            mDialog.show();

            try {
                mDialog.setIndeterminate(true);
                client.connect(null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken mqttToken) {
                        Log.i("OnlineLog", "Client connected");

                        MqttMessage mqttMessage = new MqttMessage();
                        mqttMessage.setPayload(json.getBytes());
                        mqttMessage.setQos(2);
                        mqttMessage.setRetained(false);
                        try {
                            client.publish(sharedPrefManager.getSpIdDevice() + "/control/timer/edit", mqttMessage);
                            Log.i("OnlineLog", "Message published");

                        } catch (MqttPersistenceException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();

                        } catch (MqttException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(IMqttToken arg0, Throwable arg1) {
                        // TODO Auto-generated method stub
                        Log.i("OnlineLog", "Client connection failed: "+arg1.getMessage());

                    }
                });
                mDialog.dismiss();

            } catch (MqttException e) {
                e.printStackTrace();
            }
            Log.d("MyAppmmm", obj.toString());
            Log.d("phonetypevalue", obj.getString("phonetype"));

        } catch (Throwable tx) {
            Log.e("MyApp", "Could not parse malformed JSON: \"" + json + "\""
            );
        }

//        SaveToDb(time, "Online");

        Toast.makeText(this, "Timer berhasil di perbarui", Toast.LENGTH_LONG).show();
        startActivity(new Intent(context, HomeFeederActivity.class));
    }

    public boolean isInternetAvailable() {
//        Runtime runtime = Runtime.getRuntime();
//        try {
//            Process ipProcess = runtime.exec("/system/bin/ping -c 1 " + mqttHelper.serverIp);
//            int     exitValue = ipProcess.waitFor();
//            return (exitValue == 0);
//        }
//        catch (IOException e)          { e.printStackTrace(); }
//        catch (InterruptedException e) { e.printStackTrace(); }
//        return false;

        try {
            InetAddress address = InetAddress.getByName("www.google.com");
            return !address.equals("");
        } catch (UnknownHostException e) {
            // Log error
        }
        return false;
    }
}