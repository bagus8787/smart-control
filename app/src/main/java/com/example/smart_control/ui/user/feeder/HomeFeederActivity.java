package com.example.smart_control.ui.user.feeder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smart_control.R;
import com.example.smart_control.adapter.AdapterListAlarm;
import com.example.smart_control.base.scan_broadcast.ScanBroadcastActivity;
import com.example.smart_control.handler.DatabaseHandler;
import com.example.smart_control.model.AlarmModel;
import com.example.smart_control.mqtt.MqttActivity;
import com.example.smart_control.mqtt.MqttHelper;
import com.example.smart_control.mqtt.MqttTesActivity;
import com.example.smart_control.network.ApiInterface;
import com.example.smart_control.network.ApiLocalClient;
import com.example.smart_control.receiver.WifiActivity;
import com.example.smart_control.repository.AlarmRepository;
import com.example.smart_control.ui.loginFirebase.LoginFirebaseActivity;
import com.example.smart_control.utils.SharedPrefManager;
import com.example.smart_control.viewmodel.AlarmViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Url;

import static com.example.smart_control.Myapp.getContext;

public class HomeFeederActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private static String AUTH_KEY = "key=AAAAPZwfg1o:APA91bFyoAPcGEvSiGjIlw7upmN7C0tvliqdatgybhwRND6MLlcgxVncaRNQLyrAd-7pSYDHZQoM1ofQlWceCYbCmXhTH6GIIUluNwU7n26QrJ0-47bhRUvEP0foHmr0lKGx9sU94BwQ";

//    private static String AUTH_KEY;
    LinearLayout ly_timer, ly_wifi, ly_img22;
    TextView mTextView, txt_nama;
    Button btn_beri_pakan;
    ImageView img_setting, img_notif, img_delete_alarm;
    RecyclerView rv_time_alarm;

    AdapterListAlarm adapterListAlarm;
    AlarmRepository alarmRepository;
    AlarmViewModel alarmViewModel;

    ApiInterface apiInterface;
    SharedPrefManager sharedPrefManager;
    DatabaseHandler db;
    WifiManager wifiManager;

    ConnectivityManager connManager;
    NetworkInfo mWifi;

    private String token;
    String m_Text;
    Context context;

    private TextView txtProgress;
    private ProgressBar progressBar;

    private int pStatus = 0;
    private Integer pDefault = 0;
    final long period = 1000;

    private Handler handler = new Handler();
    MqttHelper mqttHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_feeder);

        context = getContext();
        apiInterface = ApiLocalClient.getClient().create(ApiInterface.class);
        sharedPrefManager = new SharedPrefManager(this);

        //        Set WIFI to enabled
        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        Log.d("infowifineee", "= " + wifiInfo.toString());

        //set wifi
         connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
         mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        mqttHelper = new MqttHelper(getApplicationContext());

        db = DatabaseHandler.getInstance(context);

        auth = FirebaseAuth.getInstance();
        Log.d("autaaaaaah", "= " + auth.getTenantId());

//      Auth Listener
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(HomeFeederActivity.this, LoginFirebaseActivity.class));
                    finish();
                }
            }
        };

        txtProgress = (TextView) findViewById(R.id.txtProgress);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        alarmRepository = new AlarmRepository();
        adapterListAlarm = new AdapterListAlarm(getApplicationContext());
        alarmViewModel = new AlarmViewModel(getApplication());

        mTextView   = findViewById(R.id.txt);
        txt_nama    = findViewById(R.id.txt_nama);
        ly_timer    = findViewById(R.id.ly_timer);
        ly_wifi     = findViewById(R.id.ly_wifi);
        ly_img22    = findViewById(R.id.ly_img22);
        rv_time_alarm = findViewById(R.id.rv_time_alarm);
        btn_beri_pakan = findViewById(R.id.btn_beri_pakan);

        //ImageViewButton
        img_notif   = findViewById(R.id.img_notif);
        img_setting = findViewById(R.id.img_setting);
        img_delete_alarm = findViewById(R.id.img_delete_alarm);

        img_delete_alarm.setOnClickListener(this);
        img_setting.setOnClickListener(this);
        img_notif.setOnClickListener(this);

        ly_img22.setOnClickListener(this);
        ly_wifi.setOnClickListener(this);
        ly_timer.setOnClickListener(this);

        btn_beri_pakan.setOnClickListener(this);

        txt_nama.setText("Haloo ," + sharedPrefManager.getSpNama());

//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                // do your task here
//                Call<StatusPakan> status_pakan = apiInterface.statusPakan();
//                status_pakan.enqueue(new Callback<StatusPakan>() {
//                    @Override
//                    public void onResponse(Call<StatusPakan> call, Response<StatusPakan> response) {
//                        String status = String.valueOf(response.body().getPersen());
//                        Log.d("statusss", "=" + response.body().getStatus().toString());
////                if (status.equals("HIGH")){
////                    pDefault = 100;
////                } else if (status == "MEDIUM"){
////                    pDefault = 75;
////                } else if (status == "LOW"){
////                    pDefault = 35;
////                }
//                        pDefault = response.body().getPersen();
//
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                while (pStatus <= pDefault) {
//                                    handler.post(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            progressBar.setProgress(pStatus);
//                                            progressBar.setMax(10);
//                                            txtProgress.setText(pStatus + "");
//                                        }
//                                    });
//                                    try {
//                                        Thread.sleep(200);
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                    }
//                                    pStatus++;
//                                }
//                            }
//                        }).start();
//                    }
//
//                    @Override
//                    public void onFailure(Call<StatusPakan> call, Throwable t) {
//
//                    }
//                });
//            }
//        }, 0, period);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String tmp = "";
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);
                tmp += key + ": " + value + "\n\n";
            }
            mTextView.setText(tmp);
        }

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    token = task.getException().getMessage();
                    Log.w("FCM TOKEN Failed", task.getException());
                } else {
                    token = task.getResult().getToken();
//                    AUTH_KEY = "key="+token;
//                    Log.d("token_auth", AUTH_KEY);
                    Log.i("FCM TOKEN", token);
                }
            }
        });

        rv_time_alarm.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_time_alarm.setAdapter(adapterListAlarm);

        alarmViewModel = ViewModelProviders.of(this).get(AlarmViewModel .class);
        alarmViewModel.init();
        alarmViewModel.getArtikelLiveData().observe(this, new Observer<ArrayList<AlarmModel>>() {
            @Override
            public void onChanged(ArrayList<AlarmModel> alarms) {
                adapterListAlarm.setArtikels(alarms);
//                Log.d("alarmmmmaaa", "= " + alarms.toString());

                for (AlarmModel alarmModel : alarms){
                    Log.d("timel", alarmModel.getTime());
                }
            }
        });

        reloadAlarm();

        try {
            getIntervalTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        alarmViewModel.getAlarm(context);
    }

    public void reloadAlarm() {
        alarmViewModel.getAlarm(context);
        alarmRepository.getAlarmLocal(context);
//        Log.d("moddelll", alarmViewModel.toString());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ly_timer:
                startActivity(new Intent(HomeFeederActivity.this, TimerActivity.class));
                break;

            case R.id.ly_wifi:
                startActivity(new Intent(HomeFeederActivity.this, MqttTesActivity.class));
                break;

            case R.id.ly_img22:
                startActivity(new Intent(HomeFeederActivity.this, ScanBroadcastActivity.class));
                break;

            case R.id.btn_beri_pakan:
                Log.d("wifi", mWifi.toString());

                if (mWifi.isConnected()){

                    OfflineFeeder();
                } else {
                    OnlineFeeder();
                }
                break;

            case R.id.img_setting:
                startActivity(new Intent(this, SettingActivity.class));
                break;

            case R.id.img_notif:
                startActivity(new Intent(this, NotificationFeederActivity.class));
                break;

            case R.id.img_delete_alarm:
                AlertDialog.Builder b =  new  AlertDialog.Builder(this)
                        .setTitle("Anda yakin mau menghapus semua alarm ?");
                        b.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        // do something...
                                        db.deleteModel();
                                        Toast.makeText(context, "Data Alarm berhasil di hapus", Toast.LENGTH_LONG).show();
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
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    public void showToken(View view) {
        mTextView.setText(token);
    }

    public void subscribe(View view) {
        FirebaseMessaging.getInstance().subscribeToTopic("news");
//        mTextView.setText(R.string.btn_subscribe_news);
    }

    public void unsubscribe(View view) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("news");
//        mTextView.setText(R.string.btn_unsubscribe_news);
    }

    public void sendToken(View view) {
        sendWithOtherThread("token");
    }

    public void sendTokens(View view) {
        sendWithOtherThread("tokens");
    }

    public void sendTopic(View view) {
        sendWithOtherThread("topic");
    }

    private void sendWithOtherThread(final String type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                pushNotification(type);
            }
        }).start();
    }

    private void pushNotification(String type) {
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        JSONObject jData = new JSONObject();
        try {
            jNotification.put("title", "Google I/O 2016");
            jNotification.put("body", "Firebase Cloud Messaging (App)");
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");
            jNotification.put("click_action", "OPEN_ACTIVITY_1");
            jNotification.put("icon", "ic_notification");

            jData.put("picture", "https://miro.medium.com/max/1400/1*QyVPcBbT_jENl8TGblk52w.png");

            switch(type) {
                case "tokens":
                    JSONArray ja = new JSONArray();
                    ja.put("c5pBXXsuCN0:APA91bH8nLMt084KpzMrmSWRS2SnKZudyNjtFVxLRG7VFEFk_RgOm-Q5EQr_oOcLbVcCjFH6vIXIyWhST1jdhR8WMatujccY5uy1TE0hkppW_TSnSBiUsH_tRReutEgsmIMmq8fexTmL");
                    ja.put(token);
                    jPayload.put("registration_ids", ja);
                    break;
                case "topic":
                    jPayload.put("to", "/topics/news");
                    break;
                case "condition":
                    jPayload.put("condition", "'sport' in topics || 'news' in topics");
                    break;
                default:
                    jPayload.put("to", token);
            }

            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);
            jPayload.put("data", jData);

            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            Log.d("AUTH_KEY", AUTH_KEY);

            conn.setRequestProperty("Authorization", AUTH_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Send FCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jPayload.toString().getBytes());

            // Read FCM response.
            InputStream inputStream = conn.getInputStream();
            final String resp = convertStreamToString(inputStream);

            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                @Override
                public void run() {
                    mTextView.setText(resp);
                }
            });
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }

//    On Start
    @Override
    protected void onStart(){
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop(){
        super.onStop();
        if (authStateListener != null){
            auth.removeAuthStateListener(authStateListener);
        }
    }

    public void getIntervalTime() throws ParseException {
        Date date1, date2;
        Integer days, hours, min;
        String timeNow, timeAlarm;

        timeAlarm = "08:00";

        Calendar c = Calendar.getInstance();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm");

        timeNow = simpleDateFormat.format(c.getTime());

        date1 = simpleDateFormat.parse(timeAlarm);
        date2 = simpleDateFormat.parse(timeNow);

        long difference = date1.getTime() - date2.getTime();
        days = (int) (difference / (1000*60*60*24));
        hours = (int) ((difference - (1000*60*60*24*days)) / (1000*60*60));
        min = (int) (difference - (1000*60*60*24*days) - (1000*60*60*hours)) / (1000*60);
        hours = (hours < 0 ? -hours : hours);

        Log.d("timeiii", hours.toString() + ":" + min.toString());
        Log.d("dfifference", String.valueOf(difference));
    }

    public void OnlineFeeder(){
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeFeederActivity.this);
        builder.setTitle("Jumlah");
        View viewInflated = getLayoutInflater().inflate(R.layout.text_input, null);
        // Set up the input
        final EditText input = (EditText) viewInflated.findViewById(R.id.input);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        builder.setView(viewInflated);

        // Set up the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                m_Text = input.getText().toString();

                String json = "{\"count\":"+ m_Text +", \"secret_key\":" + sharedPrefManager.getSpSecretKey() +"}";
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
                        client.connect(null, new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken mqttToken) {
                                Log.i("OnlineLog", "Client connected");

                                MqttMessage mqttMessage = new MqttMessage();
                                mqttMessage.setPayload(json.getBytes());
                                mqttMessage.setQos(2);
                                mqttMessage.setRetained(false);
                                try {
                                    client.publish(sharedPrefManager.getSpIdDevice() + "/control/beri_pakan", mqttMessage);
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
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                    Log.d("MyAppmmm", obj.toString());
                    Log.d("phonetypevalue", obj.getString("phonetype"));

                } catch (Throwable tx) {
                    Log.e("MyApp", "Could not parse malformed JSON: \"" + json + "\""
                    );
                }
                Toast.makeText(HomeFeederActivity.this, "Memberi pakan", Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

        Integer p = 1;



        String a = "";
    }

    public void OfflineFeeder(){
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeFeederActivity.this);
        builder.setTitle("Jumlah");
        View viewInflated = getLayoutInflater().inflate(R.layout.text_input, null);
        // Set up the input
        final EditText input = (EditText) viewInflated.findViewById(R.id.input);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        builder.setView(viewInflated);

// Set up the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        m_Text = input.getText().toString();

                        Call<String> beriPakan = apiInterface.beriPakan(
                                m_Text
                        );

                        Toast.makeText(HomeFeederActivity.this, "Memberi pakan", Toast.LENGTH_LONG).show();
                        beriPakan.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {

                            }
                        });
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
    }

}