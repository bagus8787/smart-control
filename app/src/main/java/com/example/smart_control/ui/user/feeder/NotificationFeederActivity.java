package com.example.smart_control.ui.user.feeder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.smart_control.R;
import com.example.smart_control.adapter.AdapterListAlarm;
import com.example.smart_control.adapter.AdapterNotif;
import com.example.smart_control.handler.DatabaseHandler;
import com.example.smart_control.handler.DatabaseNotif;
import com.example.smart_control.model.AlarmModel;
import com.example.smart_control.model.Notification;
import com.example.smart_control.network.ApiClient;
import com.example.smart_control.network.ApiInterface;
import com.example.smart_control.network.ApiLocalClient;
import com.example.smart_control.repository.AlarmRepository;
import com.example.smart_control.repository.NotifRepository;
import com.example.smart_control.viewmodel.AlarmViewModel;
import com.example.smart_control.viewmodel.NotifViewModel;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.smart_control.Myapp.getContext;

public class NotificationFeederActivity extends AppCompatActivity implements View.OnClickListener {
    private DatabaseNotif db;
    private ApiInterface apiInterface;
    Context context;

    NotifRepository notifRepository;
    NotifViewModel notifViewModel;
    AdapterNotif adapterNotif;

    RecyclerView rv_notif_list;
    ImageView img_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_feeder);

        apiInterface = ApiLocalClient.getClient().create(ApiInterface.class);

        notifRepository = new NotifRepository();
        adapterNotif = new AdapterNotif(getApplicationContext());
        notifViewModel = new NotifViewModel(getApplication());
        context = getApplicationContext();

        rv_notif_list = findViewById(R.id.rv_notif_list);

        img_back = findViewById(R.id.img_back);
        img_back.setOnClickListener(this);

        rv_notif_list.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_notif_list.setAdapter(adapterNotif);

        notifViewModel = ViewModelProviders.of(this).get(NotifViewModel.class);
        notifViewModel.init();
        notifViewModel.getArtikelLiveData().observe(this, new Observer<ArrayList<Notification>>() {
            @Override
            public void onChanged(ArrayList<Notification> notif) {
                adapterNotif.setArtikels(notif);
//                Log.d("alarmmmmaaa", "= " + notif.toString());

                for (Notification notifModel : notif){
                    Log.d("timel", notifModel.getTitle());
                }
            }
        });

        reloadNotif();
//        SaveToDb();
        getNotif(getApplicationContext());
    }

    public void getNotif(Context context){
        db = DatabaseNotif.getInstance(context);
        // Get all posts from database
        ArrayList<Notification> posts = db.getAllRecord();
//        alarmLiveData.postValue(posts);
        Log.d("post", "=" + posts.toString());
        for (Notification post : posts) {
            // do something
            if (post.getTitle() == null){
                Log.d("cek_dbtttt", "=" + "KOSONG");
            } else {
                Log.d("cek_dbtttt", "=" + post.getTitle().toString());
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        notifViewModel.getNotifLocal(context);
    }

    public void reloadNotif() {
        notifViewModel.getNotifLocal(context);
        notifRepository.getNotifLocal(context);
//        Log.d("moddelll", alarmViewModel.toString());
    }

    public void SaveToDb(){
        Notification db_model = new Notification();
        db_model.setTitle("adadasd");
        db_model.setBody("casca");
        db = DatabaseNotif.getInstance(this);

        db = new DatabaseNotif(NotificationFeederActivity.this);
        db.addRecord(db_model);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_back:
                startActivity(new Intent(context, HomeFeederActivity.class));
                break;
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}