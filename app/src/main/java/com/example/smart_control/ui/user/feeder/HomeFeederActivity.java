package com.example.smart_control.ui.user.feeder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.app.ProgressDialog;
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
import com.example.smart_control.handler.DatabaseNotif;
import com.example.smart_control.model.AlarmModel;
import com.example.smart_control.model.Notification;
import com.example.smart_control.model.StatusPakan;
import com.example.smart_control.mqtt.MqttActivity;
import com.example.smart_control.mqtt.MqttHelper;
import com.example.smart_control.mqtt.MqttTesActivity;
import com.example.smart_control.mqtt.WifiConnectActivity;
import com.example.smart_control.network.ApiInterface;
import com.example.smart_control.network.ApiLocalClient;
import com.example.smart_control.receiver.WifiActivity;
import com.example.smart_control.repository.AlarmRepository;
import com.example.smart_control.services.CekConnectionService;
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
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
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
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.Connection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Url;

import static com.example.smart_control.Myapp.getContext;

public class HomeFeederActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private static String AUTH_KEY = "key=AAAAPZwfg1o:APA91bFyoAPcGEvSiGjIlw7upmN7C0tvliqdatgybhwRND6MLlcgxVncaRNQLyrAd-7pSYDHZQoM1ofQlWceCYbCmXhTH6GIIUluNwU7n26QrJ0-47bhRUvEP0foHmr0lKGx9sU94BwQ";

    LinearLayout ly_timer, ly_wifi, ly_img22;
    TextView mTextView, txt_nama, txt_status_device, txt_status_pakan, txt_time;
    Button btn_beri_pakan;
    ImageView img_setting, img_notif, img_delete_alarm;
    RecyclerView rv_time_alarm;
    SwipeRefreshLayout ly_refresh;

    AdapterListAlarm adapterListAlarm;
    AlarmRepository alarmRepository;
    AlarmViewModel alarmViewModel;

    ApiInterface apiInterface;
    SharedPrefManager sharedPrefManager;
    DatabaseHandler db;
    DatabaseNotif dbNotif;
    WifiManager wifiManager;

    ConnectivityManager connManager;
    NetworkInfo mWifi;

    private String token;
    String m_Text;
    String ConnectionStatus = "";
    Context context;
    ProgressDialog mDialog;

    private TextView txtProgress;
    private ProgressBar progressBar;

    private int pStatus = 0;
    private Integer pDefault = 0;
    final long period = 3000;

    private Handler handler = new Handler();
//    Handler mHandler = new Handler();
    boolean isRunning = true;
    MqttHelper mqttHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_feeder);

        context = getContext();
        apiInterface = ApiLocalClient.getClient().create(ApiInterface.class);
        sharedPrefManager = new SharedPrefManager(context);
        mDialog = new ProgressDialog(this);

        //        Set WIFI to enabled
        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        Log.d("infowifineee", "= " + wifiInfo.toString());

        //set wifi
         connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
         mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        mqttHelper = new MqttHelper(getApplicationContext());

        db = DatabaseHandler.getInstance(context);
        dbNotif = DatabaseNotif.getInstance(context);

        Log.d("dbnnnn", dbNotif.getAllRecord().toString());

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
        txt_status_device = findViewById(R.id.txt_status_device);
        txt_status_pakan = findViewById(R.id.txt_status_pakan);
        txt_time = findViewById(R.id.txt_time);

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

        //Cek Connection
//        cek_connection();

        ly_refresh = findViewById(R.id.ly_refresh);
        // Mengeset properti warna yang berputar pada SwipeRefreshLayout
        ly_refresh.setColorSchemeResources(R.color.colorWhite,R.color.colorPrimary);
        ly_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Handler untuk menjalankan jeda selama 5 detik
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        // Berhenti berputar/refreshing
                        ly_refresh.setRefreshing(false);
                        // fungsi-fungsi lain yang dijalankan saat refresh berhenti
                        cek_connection();
                    }
                }, 5000);
            }
        });

        cek_connection();

        Log.d("qppqpqpqpq", "= " + ConnectionStatus);
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
                Log.d("netttt", String.valueOf(isInternetAvailable()));

                if (isInternetAvailable() == false){
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
                pushNotification(type, token, "Google I/O 2016", "Firebase Cloud Messaging (App)");
            }
        }).start();
    }

    private void pushNotification(String type, String tokens, String title, String body) {
        Log.d("toesnss", tokens);
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        JSONObject jData = new JSONObject();
        try {
            jNotification.put("title", title);
            jNotification.put("body", body);
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");
            jNotification.put("click_action", "OPEN_ACTIVITY_1");
            jNotification.put("icon", "ic_notification");
//            jData.put("picture", "https://miro.medium.com/max/1400/1*QyVPcBbT_jENl8TGblk52w.png");

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
                    jPayload.put("to", tokens);
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

            SaveToDbNotif("a",title, body);

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

    public void cek_connection(){
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                Log.d("connectccc", String.valueOf(isInternetAvailable()));
                if (isInternetAvailable() == false){
                    OfflineStatusPakan();
//                    mDialog.dismiss();
                } else {
                    OnlineStatusPakan();
//                    mDialog.dismiss();
                }
                handler.postDelayed(this, 1000);
            }
        };
        runnable.run();
    }

    public void OnlineFeeder(){
        Log.d("secret_keyssss", sharedPrefManager.getSpSecretKey());
        String json = "{\"count\":"+1+",\"secret_key\":"+sharedPrefManager.getSpSecretKey()+"}";
        String user = "";

        mDialog.setMessage("Sedang memberi pakan. Mohon tunggu sebentar");
        mDialog.setIndeterminate(true);

        try {
            mDialog.show();

            Log.d("mqttttsss", String.valueOf(mqttHelper.mqttAndroidClient.isConnected()));
            if (mqttHelper.mqttAndroidClient.isConnected() == false){
                mqttHelper.mqttAndroidClient.connect();
                mDialog.dismiss();
                Toast.makeText(context, "Server disconnect", Toast.LENGTH_LONG).show();
                return;
            } else {
                MqttMessage message = new MqttMessage();
                message.setPayload(json.getBytes());
                message.setQos(0);
                mqttHelper.mqttAndroidClient.publish(sharedPrefManager.getSpIdDevice() + "/control/beri_pakan", message,null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.i("OnlineLog", "Message published= " + message.toString());
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
                mDialog.dismiss();
            }

        } catch (MqttException e) {
            Log.e("TAG", e.toString());
            e.printStackTrace();
        }

        Toast.makeText(HomeFeederActivity.this, "Memberi pakan", Toast.LENGTH_LONG).show();

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                token = task.getResult().getToken();
                Log.d("FCM_TOKEN", "AAAA =" + token);

                pushNotification("", token ,"Pemberian pakan kucing berhasil ✔" ,"Tenang lur kucing e gak keluwen");

            }
        });

    }

    public void OfflineFeeder(){
        Call<String> beriPakan = apiInterface.beriPakan(
                "1"
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
//        AlertDialog.Builder builder = new AlertDialog.Builder(HomeFeederActivity.this);
//        builder.setTitle("Jumlah");
//        View viewInflated = getLayoutInflater().inflate(R.layout.text_input, null);
//        // Set up the input
//        final EditText input = (EditText) viewInflated.findViewById(R.id.input);
//        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
//        builder.setView(viewInflated);
//
//// Set up the buttons
//        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//            @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//
//                        m_Text = input.getText().toString();
//
//                        Call<String> beriPakan = apiInterface.beriPakan(
//                                m_Text
//                        );
//                        Toast.makeText(HomeFeederActivity.this, "Memberi pakan", Toast.LENGTH_LONG).show();
//                        beriPakan.enqueue(new Callback<String>() {
//                            @Override
//                            public void onResponse(Call<String> call, Response<String> response) {
//                            }
//
//                            @Override
//                            public void onFailure(Call<String> call, Throwable t) {
//
//                            }
//                        });
//                    }
//                });
//                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//
//                builder.show();
    }

    public void OfflineStatusPakan(){
        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        txt_time.setText(currentTime.toString());

        String status = "Device : Tersambung (Offline)";
        txt_status_device.setText(status);

        Call<StatusPakan> status_pakan = apiInterface.statusPakan();
        status_pakan.enqueue(new Callback<StatusPakan>() {
            @Override
            public void onResponse(Call<StatusPakan> call, Response<StatusPakan> response) {
                String status = String.valueOf(response.body().getPersen());
                Log.d("statusss", "=" + response.body().getPersen().toString() + "||" + response.body().getLevel().toString());

                if (response.body().getLevel().equals("HIGH")){
                    txt_status_pakan.setText("Penuh");
                } else if (response.body().getLevel().equals("MEDIUM")){
                    txt_status_pakan.setText("Medium");
                } else if (response.body().getLevel().equals("LOW")){
                    txt_status_pakan.setText("Sedikit");
                }

                if (response.body().getPersen() == 10){
                    pDefault = 100;
                } else if (response.body().getPersen() == 9){
                    pDefault = 90;
                } else if (response.body().getPersen() == 8){
                    pDefault = 80;
                } else if (response.body().getPersen() == 7){
                    pDefault = 70;
                } else if (response.body().getPersen() == 6){
                    pDefault = 60;
                } else if (response.body().getPersen() == 5) {
                    pDefault = 50;
                } else if (response.body().getPersen() == 4){
                    pDefault = 40;
                } else if (response.body().getPersen() == 3){
                    pDefault = 30;
                } else if (response.body().getPersen() == 2){
                    pDefault = 20;
                } else if (response.body().getPersen() == 1){
                    pDefault = 10;
                } else if (response.body().getPersen() == 11) {
                    pDefault = 100;
                }
//                            pDefault = response.body().getPersen();
                txtProgress.setText(pDefault + "%");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (pStatus <= pDefault) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(pDefault);
                                }
                            });
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            pStatus++;
                        }
                    }
                }).start();
            }

            @Override
            public void onFailure(Call<StatusPakan> call, Throwable t) {

            }
        });
    }

    public void OnlineStatusPakan(){
        mqttHelper.mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                if (b=true){
                    String status = "Device : Tersambung (Online)";
//                    txt_status_device.setText(status);
                    Log.w("DebugStatus","Connected= " + b);
                } else {
                    String status = "Device : Tidak tersambung";
//                    txt_status_device.setText(status);
                    Log.w("DebugStatus","Connected= " + b);
                }
            }

            @Override
            public void connectionLost(Throwable throwable) {
                Log.w("DebugStatus","Connected= " + throwable);
                if (throwable.toString() != "Connection lost (32109) - java.io.EOFException"){
                    String status = "Device : Tersambung (Online)";
                    txt_status_device.setText(status);
                } else {
                    String status = "Device : Tidak tersambung";
                    txt_status_device.setText(status);
                }
            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.w("Debugss",mqttMessage.toString());
                Log.w("topicss", topic.toString());

                Log.d("federraaaar", sharedPrefManager.getSpIdDevice());

                Log.d("federraaaarssss", topic.toString() + "|| " + mqttMessage.toString());

                String feeder_topic_jarak = sharedPrefManager.getSpIdDevice()+"/feeder/status";
                Log.d("federrr", feeder_topic_jarak);

                String status = "Device : Tersambung (Online)";
                txt_status_device.setText(status);
                switch (topic.toString()){
                    case "USW1000001/feeder/persen":
                        pDefault = Integer.parseInt(String.valueOf(mqttMessage));
                        Log.d("jaakkkk", "= " +pDefault.toString());
                        if (pDefault == 10){
                            pDefault = 100;
                        } else if (pDefault == 9){
                            pDefault = 90;
                        } else if (pDefault == 8){
                            pDefault = 80;
                        } else if (pDefault == 7){
                            pDefault = 70;
                        } else if (pDefault == 6){
                            pDefault = 60;
                        } else if (pDefault == 5) {
                            pDefault = 50;
                        } else if (pDefault == 4){
                            pDefault = 40;
                        } else if (pDefault == 3){
                            pDefault = 30;
                        } else if (pDefault == 2){
                            pDefault = 20;
                        } else if (pDefault == 1){
                            pDefault = 10;
                        } else if (pDefault == 11) {
                            pDefault = 100;
                        }

                        txtProgress.setText(pDefault + "%");

                        Log.d("pddddd", mqttMessage.toString());
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (pStatus <= pDefault) {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setProgress(pStatus);
                                        }
                                    });
                                    try {
                                        Thread.sleep(200);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    pStatus++;
                                }
                            }
                        }).start();

                        break;

                    case "USW1000001/feeder/pakan":
                        Log.d("pddddd", mqttMessage.toString());
                        if (mqttMessage.toString().equals("HIGH")){
                            txt_status_pakan.setText("Penuh");
                        } else if (mqttMessage.toString().equals("MEDIUM")){
                            txt_status_pakan.setText("Medium");
                        } else if (mqttMessage.toString().equals("LOW")){
                            txt_status_pakan.setText("Sedikit");
                        } else if (mqttMessage.toString().equals("EMPTY")){
                            txt_status_pakan.setText("Habis");
                        }
//                        txt_status_pakan.setText(mqttMessage.toString());
                        break;

                    case "USW1000001/feeder/time":
//                                    textView1.setText(mqttMessage.toString());
//                        pDefault = Integer.parseInt(String.valueOf(mqttMessage));
                        Log.d("pddddd", mqttMessage.toString());

                        String dtStart = mqttMessage.toString();
                        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                        try {
                            Date time_mqtt = format.parse(dtStart);
                            Log.d("rtccc", time_mqtt.toString());

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        txt_time.setText(mqttMessage.toString());

                        break;

                    default:
                        Log.d("Error","Error ocquired");
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }

    public void SaveToDbNotif(String timee, String title, String body){
        DatabaseNotif db_notif;

        Notification db_model = new Notification();
        db_model.setTitle(title);
        db_model.setBody(body);

        db_notif = DatabaseNotif.getInstance(this);
//        db_notif = new DatabaseNotif(HomeFeederActivity.this);
        db_notif.addRecord(db_model);
    }

    @Override
    public void onBackPressed(){
//        finish();
        new AlertDialog.Builder(this)
                .setMessage("Apakah anda mau keluar dari Aplikasi Feeder ?")
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        HomeFeederActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("Tidak", null)
                .show();
    }

    public boolean isInternetAvailable() {
//        try {
//            int timeoutMs = 0;
//            Socket sock = new Socket();
//            SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 43);
//
//            sock.connect(sockaddr, timeoutMs);
//            sock.close();
//
//            return true;
//        } catch (IOException e) { return false; }

//        Runtime runtime = Runtime.getRuntime();
//        try {
//            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
//            int     exitValue = ipProcess.waitFor();
//            return (exitValue == 0);
//        }
//        catch (IOException e)          { e.printStackTrace(); }
//        catch (InterruptedException e) { e.printStackTrace(); }
//        return false;

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                // TODO Auto-generated method stub
//                while (isRunning) {
//                    try {
//                        Thread.sleep(10000);
//                        handler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    int timeoutMs = 0;
//                                    Socket sock = new Socket();
//                                    SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 43);
//
//                                    sock.connect(sockaddr, timeoutMs);
//                                    sock.close();
//
////                                    return true;
//                                    ConnectionStatus = "connect";
//                                } catch (IOException e) {
//                                    ConnectionStatus = "disconnect";
//
////                                    return false;
//                                }
//                            }
//                        });
//                    } catch (Exception e) {
//                        // TODO: handle exception
//                    }
//                }
//            }
//        }).start();

        try {
            InetAddress address = InetAddress.getByName("www.google.com");
            return !address.equals("");
        } catch (UnknownHostException e) {
            // Log error
        }

        return false;
    }

}