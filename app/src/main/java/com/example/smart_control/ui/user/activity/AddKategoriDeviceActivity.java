package com.example.smart_control.ui.user.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;

import com.example.smart_control.R;
import com.example.smart_control.adapter.AdapterKategoriDevice;
import com.example.smart_control.adapter.AdapterGridKategoriDevice;
import com.example.smart_control.adapter.GridArtikelAdapter;
import com.example.smart_control.base.layout.ExpandableHeightGridView;
import com.example.smart_control.model.Artikel;
import com.example.smart_control.network.ApiInterface;
import com.example.smart_control.repository.ArtikelRepository;
import com.example.smart_control.utils.SharedPrefManager;
import com.example.smart_control.viewmodel.ArtikelViewModel;

import java.util.ArrayList;

import static com.example.smart_control.Myapp.getContext;

public class AddKategoriDeviceActivity extends AppCompatActivity {

    AdapterKategoriDevice adapterKategoriDevice;
    AdapterGridKategoriDevice adapterGridKategoriDevice;

    public SharedPrefManager sharedPrefManager;
    public ApiInterface apiInterface;
    Context context;

    ArtikelViewModel artikelViewModel;
    ArtikelRepository artikelRepository;

    RecyclerView recyclerView;
    ExpandableHeightGridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_kategori_device);

        sharedPrefManager = new SharedPrefManager(getContext());

        adapterKategoriDevice = new AdapterKategoriDevice(getContext());
        artikelRepository = new ArtikelRepository();

        recyclerView = findViewById(R.id.rv_kat_dev);

        gridView = findViewById(R.id.gv_device);
        gridView.setExpanded(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapterKategoriDevice);

        artikelViewModel = ViewModelProviders.of(this).get(ArtikelViewModel.class);
        artikelViewModel.init();
        artikelViewModel.getArtikelLiveData().observe(this, new Observer<ArrayList<Artikel>>() {
            @Override
            public void onChanged(ArrayList<Artikel> artikels) {
                adapterKategoriDevice.setKategoriDevice(artikels);

                adapterGridKategoriDevice = new AdapterGridKategoriDevice(getContext(), R.layout.grid_list_device, artikels);
                gridView.setAdapter(adapterGridKategoriDevice);
            }
        });

        reloadArtikel();
    }

    @Override
    public void onResume(){
        super.onResume();
        artikelViewModel.getArtikel();
    }

    public void reloadArtikel() {
        artikelViewModel.getArtikel();
        artikelRepository.getArtikel();
    }
}