package com.example.smart_control.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.smart_control.Myapp;
import com.example.smart_control.model.Artikel;
import com.example.smart_control.network.ApiClient;
import com.example.smart_control.network.ApiInterface;
import com.example.smart_control.network.response.ArtikelResponse;
import com.example.smart_control.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArtikelRepository {
    private MutableLiveData<ArrayList<Artikel>> artikelLiveData;
    private ApiInterface apiInterface;
    private SharedPrefManager sharedPrefManager;

    public ArtikelRepository() {
        sharedPrefManager = new SharedPrefManager(Myapp.getContext());
        artikelLiveData = new MutableLiveData<>();
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
    }

    public void getArtikel(){
        Call<ArtikelResponse> getArtikel = apiInterface.getArtikel(
                sharedPrefManager.getSpAppToken(),
                sharedPrefManager.getSpProdesaToken(),
                sharedPrefManager.getNameArtikel(),
                sharedPrefManager.getSpDesaCode()
        );
        getArtikel.enqueue(new Callback<ArtikelResponse>() {
            @Override
            public void onResponse(Call<ArtikelResponse> call, Response<ArtikelResponse> response) {
                if (response.code() >= 200 && response.code() < 300) {
                    artikelLiveData.postValue(response.body().getArtikelList().getArtikels());
//                    Log.d("isissssisiis", String.valueOf(response.body().getArtikelList().getArtikelUrl() != null));
                    for (Artikel artikel: response.body().getArtikelList().getArtikels()){
//                        artikel.getJudul();
                        Log.d("judul", artikel.getJudul());
                    }

                }
            }

            @Override
            public void onFailure(Call<ArtikelResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public LiveData<ArrayList<Artikel>> getArtikelResponseLiveData() {
        return artikelLiveData;
    }
}
