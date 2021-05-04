package com.example.smart_control.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.smart_control.Myapp;
import com.example.smart_control.handler.DatabaseNotif;
import com.example.smart_control.model.Notification;
import com.example.smart_control.network.ApiClient;
import com.example.smart_control.network.ApiInterface;
import com.example.smart_control.utils.SharedPrefManager;

import java.util.ArrayList;

public class NotifRepository {
    private MutableLiveData<ArrayList<Notification>> notifLiveData;
    private ApiInterface apiInterface;
    private SharedPrefManager sharedPrefManager;
    private DatabaseNotif db;
    private Context context;

    public NotifRepository() {
        sharedPrefManager = new SharedPrefManager(Myapp.getContext());
        notifLiveData = new MutableLiveData<>();
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

    }

    public void getNotifLocal(Context context){
        db = DatabaseNotif.getInstance(context);
        // Get all posts from database
        ArrayList<Notification> posts = db.getAllRecord();
        notifLiveData.postValue(posts);

        Log.d("post", "=" + posts.toString());

        for (Notification post : posts) {
            // do something
            if (post.getTitle() == null){
                Log.d("cek_dbssss", "=" + "KOSONG");
            } else {
                Log.d("cek_dbssss", "=" + post.getTitle().toString());
            }
        }
    }

    public LiveData<ArrayList<Notification>> getArtikelResponseLiveData() {
        return notifLiveData;
    }
}
