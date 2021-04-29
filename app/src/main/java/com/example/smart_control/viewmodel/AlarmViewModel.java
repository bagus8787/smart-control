package com.example.smart_control.viewmodel;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.smart_control.model.AlarmModel;
import com.example.smart_control.repository.AlarmRepository;

import java.util.ArrayList;

public class AlarmViewModel extends AndroidViewModel {
    private AlarmRepository alarmRepository;
    private LiveData<ArrayList<AlarmModel>> artikelLiveData;

    public AlarmViewModel(@NonNull Application application) {
        super(application);
    }

    public void init() {
        alarmRepository = new AlarmRepository();
        artikelLiveData = alarmRepository.getArtikelResponseLiveData();
    }

    public void getAlarm(Context context) {
        alarmRepository.getAlarmLocal(context);
        Log.d("alarmmmmmmmm", alarmRepository.toString());
    }

    public LiveData<ArrayList<AlarmModel>> getArtikelLiveData() {
//        Log.d("live_data", String.valueOf(artikelLiveData));
        return artikelLiveData;
    }
}
