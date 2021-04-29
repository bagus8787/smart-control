package com.example.smart_control.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.smart_control.model.Artikel;
import com.example.smart_control.repository.ArtikelRepository;

import java.util.ArrayList;
import java.util.List;

public class ArtikelViewModel extends AndroidViewModel {
    private ArtikelRepository artikelRepository;
    private LiveData<ArrayList<Artikel>> artikelLiveData;

    public ArtikelViewModel(@NonNull Application application) {
        super(application);
    }

    public void init() {
        artikelRepository = new ArtikelRepository();
        artikelLiveData = artikelRepository.getArtikelResponseLiveData();
    }

    public void getArtikel() {
        artikelRepository.getArtikel();
    }

    public LiveData<ArrayList<Artikel>> getArtikelLiveData() {
//        Log.d("live_data", String.valueOf(artikelLiveData));
        return artikelLiveData;
    }
}
