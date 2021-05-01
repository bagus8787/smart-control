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
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.smart_control.model.AlarmModel;
import com.example.smart_control.model.StatusPakan;
import com.example.smart_control.network.ApiInterface;
import com.example.smart_control.network.ApiLocalClient;
import com.example.smart_control.receiver.WifiActivity;
import com.example.smart_control.repository.AlarmRepository;
import com.example.smart_control.ui.loginFirebase.LoginFirebaseActivity;
import com.example.smart_control.ui.user.activity.NotificationActivity;
import com.example.smart_control.utils.SharedPrefManager;
import com.example.smart_control.viewmodel.AlarmViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.smart_control.Myapp.getContext;

public class HomeFeederActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private static String AUTH_KEY = "key=AAAAPZwfg1o:APA91bFyoAPcGEvSiGjIlw7upmN7C0tvliqdatgybhwRND6MLlcgxVncaRNQLyrAd-7pSYDHZQoM1ofQlWceCYbCmXhTH6GIIUluNwU7n26QrJ0-47bhRUvEP0foHmr0lKGx9sU94BwQ";

//    private static String AUTH_KEY;
    LinearLayout ly_timer, ly_wifi, ly_img22;
    TextView mTextView, txt_nama;
    Button btn_beri_pakan;
    ImageView img_setting, img_notif;
    RecyclerView rv_time_alarm;

    AdapterListAlarm adapterListAlarm;
    AlarmRepository alarmRepository;
    AlarmViewModel alarmViewModel;

    ApiInterface apiInterface;
    SharedPrefManager sharedPrefManager;

    private String token;
    String m_Text;
    Context context;

    private TextView txtProgress;
    private ProgressBar progressBar;

    private int pStatus = 0;
    private int pDefault = 0;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_feeder);

        context = getContext();
        apiInterface = ApiLocalClient.getClient().create(ApiInterface.class);
        sharedPrefManager = new SharedPrefManager(this);

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

        Call<StatusPakan> status_pakan = apiInterface.statusPakan();
        status_pakan.enqueue(new Callback<StatusPakan>() {
            @Override
            public void onResponse(Call<StatusPakan> call, Response<StatusPakan> response) {
                String status = response.body().getStatus();
                Log.d("statusss", "=" + response.body().getStatus().toString());

                if (status.equals("HIGH")){
                    pDefault = 100;
                } else if (status == "MEDIUM"){
                    pDefault = 75;
                } else if (status == "LOW"){
                    pDefault = 35;
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (pStatus <= pDefault) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(pStatus);
                                    txtProgress.setText(pStatus + " %");
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
                Log.d("alarmmmmaaa", "= " + alarms.toString());

                for (AlarmModel alarmModel : alarms){
                    Log.d("timel", alarmModel.getTime());
                }
            }
        });

        reloadAlarm();

    }

    @Override
    public void onResume(){
        super.onResume();
        alarmViewModel.getAlarm(context);
    }

    public void reloadAlarm() {
        alarmViewModel.getAlarm(context);
        alarmRepository.getAlarmLocal(context);

        Log.d("moddelll", alarmViewModel.toString());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ly_timer:
                startActivity(new Intent(HomeFeederActivity.this, TimerActivity.class));
                break;

            case R.id.ly_wifi:
                startActivity(new Intent(HomeFeederActivity.this, WifiActivity.class));
                break;

            case R.id.ly_img22:
                startActivity(new Intent(HomeFeederActivity.this, ScanBroadcastActivity.class));
                break;

            case R.id.btn_beri_pakan:

                AlertDialog.Builder builder = new AlertDialog.Builder(HomeFeederActivity.this);
                builder.setTitle("Jumlah");
                // I'm using fragment here so I'm using getView() to provide ViewGroup
                // but you can provide here any other instance of ViewGroup from your Fragment / Activity
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

                break;

            case R.id.img_setting:
                startActivity(new Intent(this, SettingActivity.class));
                break;

            case R.id.img_notif:
                startActivity(new Intent(this, NotificationFeederActivity.class));
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
}