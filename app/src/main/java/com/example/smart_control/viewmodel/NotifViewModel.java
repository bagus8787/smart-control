package com.example.smart_control.viewmodel;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.smart_control.model.AlarmModel;
import com.example.smart_control.model.Notification;
import com.example.smart_control.repository.AlarmRepository;
import com.example.smart_control.repository.NotifRepository;

import java.util.ArrayList;

public class NotifViewModel extends AndroidViewModel {
    private NotifRepository alarmRepository;
    private LiveData<ArrayList<Notification>> artikelLiveData;

    public NotifViewModel(@NonNull Application application) {
        super(application);
    }

    public void init() {
        alarmRepository = new NotifRepository();
        artikelLiveData = alarmRepository.getArtikelResponseLiveData();
    }

    public void getNotifLocal(Context context) {
        alarmRepository.getNotifLocal(context);
        Log.d("notiffffff", alarmRepository.toString());
    }

    public LiveData<ArrayList<Notification>> getArtikelLiveData() {
//        Log.d("live_data", String.valueOf(artikelLiveData));
        return artikelLiveData;
    }
}
