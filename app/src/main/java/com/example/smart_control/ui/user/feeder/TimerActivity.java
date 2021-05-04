package com.example.smart_control.ui.user.feeder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.smart_control.R;
import com.example.smart_control.handler.DatabaseHandler;
import com.example.smart_control.model.AlarmModel;
import com.example.smart_control.mqtt.MqttHelper;
import com.example.smart_control.network.ApiClient;
import com.example.smart_control.network.ApiInterface;
import com.example.smart_control.network.ApiLocalClient;
import com.example.smart_control.receiver.AlarmReceiver;
import com.example.smart_control.repository.AlarmRepository;
import com.example.smart_control.utils.SharedPrefManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TimerActivity extends AppCompatActivity implements View.OnClickListener {
    ProgressDialog mDialog;
    Button alarmbutton, cancelButton;
    EditText text, txt_count;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;
    Intent intent;
    CheckBox cb_ulangi, cb_senin, cb_selasa, cb_rabu, cb_kamis, cb_jumat, cb_sabtu, cb_minggu;
    LinearLayout ly_ulangi;
    TimePicker picker;
    String token, time;

    private DatabaseHandler db;

    ApiInterface apiInterface;
    SharedPrefManager sharedPrefManager;
    Context context;
    MqttHelper mqttHelper;

    AlarmRepository alarmRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        alarmRepository = new AlarmRepository();

        sharedPrefManager = new SharedPrefManager(this);
        apiInterface = ApiLocalClient.getClient().create(ApiInterface.class);
        context = getApplicationContext();
        mqttHelper = new MqttHelper(this);
        mDialog = new ProgressDialog(this);

        AlarmModel db_model = new AlarmModel();
        db = DatabaseHandler.getInstance(this);

        alarmbutton = (Button) findViewById(R.id.button);
        cancelButton = (Button) findViewById(R.id.button2);
        text = (EditText) findViewById(R.id.editText);
        txt_count = findViewById(R.id.txt_count);

        ly_ulangi = findViewById(R.id.ly_ulangi);

        cb_senin = findViewById(R.id.cb_senin);
        cb_selasa = findViewById(R.id.cb_selasa);
        cb_rabu = findViewById(R.id.cb_rabu);
        cb_kamis = findViewById(R.id.cb_kamis);
        cb_jumat = findViewById(R.id.cb_jumat);
        cb_sabtu = findViewById(R.id.cb_sabtu);
        cb_minggu = findViewById(R.id.cb_minggu);

        cb_ulangi = findViewById(R.id.cb_ulangi);
        cb_ulangi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((CheckBox) v).isChecked();
                if (checked){
                    cb_senin.setChecked(true);
                    cb_selasa.setChecked(true);
                    cb_rabu.setChecked(true);
                    cb_kamis.setChecked(true);
                    cb_jumat.setChecked(true);
                    cb_sabtu.setChecked(true);
                    cb_minggu.setChecked(true);
//            ly_ulangi.setVisibility(View.INVISIBLE);
                } else {
                    cb_senin.setChecked(false);
                    cb_selasa.setChecked(false);
                    cb_rabu.setChecked(false);
                    cb_kamis.setChecked(false);
                    cb_jumat.setChecked(false);
                    cb_sabtu.setChecked(false);
                    cb_minggu.setChecked(false);
//            ly_ulangi.setVisibility(View.VISIBLE);
                }
            }
        });

        picker  =  findViewById(R.id.datePicker1);
        picker.setIs24HourView(true);

//        setHourAndMinute
        picker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                time = String.format("%02d:%02d",hourOfDay, minute);
//                Log.d("timer", "= " + time);
                setTime(time);
//                SaveToDb(time);
            }

        });

//        SaveToDb(time);
        Log.d("time", "=" + time);

        // Get all posts from database
        List<AlarmModel> posts = db.getAllRecord();
        Log.d("post", "=" + posts.toString());

        for (AlarmModel post : posts) {
            // do something
            if (post.getTime() == null){
                Log.d("cek_db", "=" + "KOSONG");
            } else {
                Log.d("cek_db", "=" + post.getTime().toString());
            }
        }

        alarmRepository.getAlarmLocal(getApplicationContext());

        alarmbutton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                token = task.getResult().getToken();
                Log.d("FCM_TOKEN", "AAAA =" + token);

//                startAlertAtParticularTime();
            }
        });
//        SaveToDb();
    }

    // This method to be called at Start button click and set repeating at every 10 seconds interval
    public void startAlert(View view) {
        if (!text.getText().toString().equals("")) {
            int i = Integer.parseInt(text.getText().toString());
            intent = new Intent(this, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(
                    this.getApplicationContext(), 280192, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + (i * 1000), 10000
                    , pendingIntent);

            Toast.makeText(this, "Alarm will set in " + i + " seconds",
                    Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this,"Please Provide time ", Toast.LENGTH_SHORT).show();
        }

    }

    public void setTime(String timee){
//        Log.d("timeer", "= " + timee);
        time = timee;
        Log.d("timeerkkkk", "= " + time.toString());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button:
                StringBuilder result = new StringBuilder();
                result.append("Alarm disetel hari: ");

                if (txt_count.getText().toString().isEmpty()){
                    txt_count.setError("Masukkan total dahulu");
                    txt_count.requestFocus();
                    return;
                }

                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        token = task.getResult().getToken();
                        Log.d("FCM_TOKEN", "AAAA =" + token);

                        if (cb_senin.isChecked()) {
                            setAlarm(2, token);
                            result.append("Senin ,");
                        }

                        if (cb_selasa.isChecked()) {
                            result.append("Selasa ,");
                            setAlarm(3, token);
                            Log.d("tokennn", token);
                        }

                        if (cb_rabu.isChecked()) {
                            result.append("Rabu ,");
                            setAlarm(4, token);
                        }

                        if (cb_kamis.isChecked()) {
                            result.append("Kamis ,");
                            setAlarm(5, token);
                        }

                        if (cb_jumat.isChecked()) {
                            result.append("Jumat ,");
                            setAlarm(6, token);
                        }

                        if (cb_sabtu.isChecked()) {
                            result.append("Sabtu ,");
                            setAlarm(7, token);
                        }

                        if (cb_minggu.isChecked()) {
                            result.append("Minggu ,");
                            setAlarm(1, token);
                        }

                        if (isInternetAvailable() == false){
                            setOfflineAlarm();
                        } else {
                            setOnlineAlarm(token);
                        }

                    }
                });

                break;
        }

    }

    private void setOnlineAlarm(String token) {
        Log.d("timee", time);
        Log.d("secret_keyssss", sharedPrefManager.getSpSecretKey());
        String json = "{\"time\" : \""+ time +"\" ,\"count\":"+ Integer.parseInt(txt_count.getText().toString()) +", \"secret_key\":" + sharedPrefManager.getSpSecretKey() +"}";
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

            try {
                mDialog.setMessage("Sedang memberi pakan. Mohon tunggu sebentar");
                mDialog.setIndeterminate(true);
                mDialog.show();

                client.connect(null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken mqttToken) {
                        Log.i("OnlineLog", "Client connected");

                        MqttMessage mqttMessage = new MqttMessage();
                        mqttMessage.setPayload(json.getBytes());
                        mqttMessage.setQos(2);
                        mqttMessage.setRetained(false);
                        try {
                            client.publish(sharedPrefManager.getSpIdDevice() + "/control/timer/add", mqttMessage);
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

        SaveToDb(time);

        Toast.makeText(this, "Jadwal berhasil di set", Toast.LENGTH_LONG).show();
        startActivity(new Intent(TimerActivity.this, HomeFeederActivity.class));
    }

    private void setOfflineAlarm() {
        Log.d("dddddd", time);

        SaveToDb(time);

        Toast.makeText(this, "Jadwal berhasil di set", Toast.LENGTH_LONG).show();
        startActivity(new Intent(TimerActivity.this, HomeFeederActivity.class));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    public void startAlertAtParticularTime() {

        // alarm first vibrate at 14 hrs and 40 min and repeat itself at ONE_HOUR interval
        Log.d("tokene", "=" + token);
        intent = new Intent(this, AlarmReceiver.class).
                putExtra("token", token);

        pendingIntent = PendingIntent.getBroadcast(
                this.getApplicationContext(), 280192, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 20);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

//        Interval per-menit
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                1000 * 60 * 1, pendingIntent);

        // With setInexactRepeating(), you have to use one of the AlarmManager interval
        // constants--in this case, AlarmManager.INTERVAL_DAY.
//        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                AlarmManager.INTERVAL_DAY, alarmIntent);

        Toast.makeText(this, "Alarm will vibrate at time specified",
                Toast.LENGTH_LONG).show();

    }

    public void setAlarm(int weekno, String tokens) {
        Log.d("weekno", "w =" + weekno);
        Log.d("weekno", "t =" + tokens);

        // alarm first vibrate at 14 hrs and 40 min and repeat itself at ONE_HOUR interval
        weekno = 3;

//        startAlertAtParticularTime();

        intent = new Intent(this, AlarmReceiver.class).
                putExtra("token", tokens);
        pendingIntent = PendingIntent.getBroadcast(
                this.getApplicationContext(), 280192, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.DAY_OF_WEEK, weekno);
        calendar.set(Calendar.HOUR_OF_DAY, 15);
        calendar.set(Calendar.MINUTE, 00);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

//        Interval per-menit
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                1000 * 60 * 1, pendingIntent);

        // With setInexactRepeating(), you have to use one of the AlarmManager interval
        // constants--in this case, AlarmManager.INTERVAL_DAY.
//        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                AlarmManager.INTERVAL_DAY, alarmIntent);

//        Toast.makeText(this, "Alarm will vibrate at time specified Week",
//                Toast.LENGTH_LONG).show();

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void SaveToDb(String timee){
        Log.d("timeek" , "= " + timee);

        Call<String> addAlarm = apiInterface.addAlarm(
                timee,
                Integer.parseInt(txt_count.getText().toString()),
                sharedPrefManager.getSpSecretKey());
        addAlarm.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
//                Log.d("respon_Add" , "=" + response.body().toString());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

        AlarmModel db_model = new AlarmModel();
        db_model.setName("adadasd");
        db_model.setTall("casca");
        db_model.setTime(timee);
        db_model.setCount(txt_count.getText().toString());
        db_model.setOld_time("cavca");
        db = DatabaseHandler.getInstance(this);
        db = new DatabaseHandler(TimerActivity.this);
        db.addRecord(db_model);
    }

    public boolean isInternetAvailable() {
        try {
            InetAddress address = InetAddress.getByName("www.google.com");
            return !address.equals("");
        } catch (UnknownHostException e) {
            // Log error
        }
        return false;
    }
}