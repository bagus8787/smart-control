package com.example.smart_control.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.smart_control.Myapp;
import com.example.smart_control.handler.DatabaseHandler;
import com.example.smart_control.model.AlarmModel;
import com.example.smart_control.network.ApiClient;
import com.example.smart_control.network.ApiInterface;
import com.example.smart_control.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

public class AlarmRepository {
    private MutableLiveData<ArrayList<AlarmModel>> alarmLiveData;
    private ApiInterface apiInterface;
    private SharedPrefManager sharedPrefManager;
    private DatabaseHandler db;
    private Context context;

    public AlarmRepository() {
        sharedPrefManager = new SharedPrefManager(Myapp.getContext());
        alarmLiveData = new MutableLiveData<>();
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
    }

    public void getAlarmLocal(Context context){
        db = DatabaseHandler.getInstance(context);
        // Get all posts from database
        ArrayList<AlarmModel> posts = db.getAllRecord();
        alarmLiveData.postValue(posts);

        Log.d("post", "=" + posts.toString());

        for (AlarmModel post : posts) {
            // do something
            if (post.getTime() == null){
                Log.d("cek_dbssss", "=" + "KOSONG");
            } else {
                Log.d("cek_dbssss", "=" + post.getTime().toString());
            }
        }
    }

//    public void getArtikel(){
//        Call<ArtikelResponse> getArtikel = apiInterface.getArtikel(
//                sharedPrefManager.getSpAppToken(),
//                sharedPrefManager.getSpProdesaToken(),
//                sharedPrefManager.getNameArtikel(),
//                sharedPrefManager.getSpDesaCode()
//        );
//        getArtikel.enqueue(new Callback<ArtikelResponse>() {
//            @Override
//            public void onResponse(Call<ArtikelResponse> call, Response<ArtikelResponse> response) {
//                if (response.code() >= 200 && response.code() < 300) {
//                    artikelLiveData.postValue(response.body().getArtikelList().getArtikels());
////                    Log.d("isissssisiis", String.valueOf(response.body().getArtikelList().getArtikelUrl() != null));
//                    for (Artikel artikel: response.body().getArtikelList().getArtikels()){
////                        artikel.getJudul();
//                        Log.d("judul", artikel.getJudul());
//                    }
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ArtikelResponse> call, Throwable t) {
//                t.printStackTrace();
//            }
//        });
//    }

    public LiveData<ArrayList<AlarmModel>> getArtikelResponseLiveData() {
        return alarmLiveData;
    }
}
