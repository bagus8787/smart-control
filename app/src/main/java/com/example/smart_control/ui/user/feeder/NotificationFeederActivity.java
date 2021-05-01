package com.example.smart_control.ui.user.feeder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.example.smart_control.R;
import com.example.smart_control.handler.DatabaseHandler;
import com.example.smart_control.handler.DatabaseNotif;
import com.example.smart_control.model.AlarmModel;
import com.example.smart_control.model.Notification;
import com.example.smart_control.network.ApiClient;
import com.example.smart_control.network.ApiInterface;
import com.example.smart_control.network.ApiLocalClient;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationFeederActivity extends AppCompatActivity {
    private DatabaseNotif db;
    private ApiInterface apiInterface;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_feeder);

        apiInterface = ApiLocalClient.getClient().create(ApiInterface.class);

        SaveToDb();

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

    public void SaveToDb(){
//        Log.d("timeek" , "= " + timee);

//        Call<String> addAlarm = apiInterface.addAlarm(
//                timee,
//                Integer.parseInt(txt_count.getText().toString()));
//        addAlarm.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(Call<String> call, Response<String> response) {
//
//                Log.d("respon_Add" , "=" + response.body().toString());
//            }
//
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
//
//            }
//        });

        Notification db_model = new Notification();
        db_model.setTitle("adadasd");
        db_model.setBody("casca");
        db = DatabaseNotif.getInstance(this);

        db = new DatabaseNotif(NotificationFeederActivity.this);
        db.addRecord(db_model);

    }
}