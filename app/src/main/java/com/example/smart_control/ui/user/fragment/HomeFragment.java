package com.example.smart_control.ui.user.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;

import com.example.smart_control.Myapp;
import com.example.smart_control.R;
import com.example.smart_control.adapter.AdapterArtikelList;
import com.example.smart_control.adapter.GridArtikelAdapter;
import com.example.smart_control.base.layout.ExpandableHeightGridView;
import com.example.smart_control.model.Artikel;
import com.example.smart_control.network.ApiClient;
import com.example.smart_control.network.ApiInterface;
import com.example.smart_control.network.response.ArtikelResponse;
import com.example.smart_control.repository.ArtikelRepository;
import com.example.smart_control.ui.user.activity.AddKategoriDeviceActivity;
import com.example.smart_control.utils.SharedPrefManager;
import com.example.smart_control.viewmodel.ArtikelViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private AdapterArtikelList adapterArtikelList;
    private GridArtikelAdapter gridArtikelAdapter;

    public SharedPrefManager sharedPrefManager;
    public ApiInterface apiInterface;

    public ArtikelRepository artikelRepository;
    public ArtikelViewModel artikelViewModel;
    public List<Artikel> list;

    public ArrayList<Artikel> artikelArrayList;

    ExpandableHeightGridView gridView;
    SwipeRefreshLayout swipeRefreshLayout;

    ImageView img_add_device;
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home,
                container, false);

        artikelRepository = new ArtikelRepository();

        swipeRefreshLayout = rootView.findViewById(R.id.ly_refresh);

        gridView = rootView.findViewById(R.id.grid_view);
        gridView.setExpanded(true);

        img_add_device = rootView.findViewById(R.id.img_add_device);
        img_add_device.setOnClickListener(this);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                reloadArtikelList();
            }
        });
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapterArtikelList = new AdapterArtikelList(getContext());

        list = new ArrayList<>();

        sharedPrefManager = new SharedPrefManager(Myapp.getContext());
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        artikelViewModel = ViewModelProviders.of(this).get(ArtikelViewModel.class);
        artikelViewModel.init();
        artikelViewModel.getArtikelLiveData().observe(this, new Observer<ArrayList<Artikel>>() {
            @Override
            public void onChanged(ArrayList<Artikel> artikels) {
                adapterArtikelList.setArtikels(artikels);

                gridArtikelAdapter = new GridArtikelAdapter(getContext(), R.layout.grid_list, artikels);
                gridView.setAdapter(gridArtikelAdapter);
//                adapterArtikelList = new AdapterArtikelList(getContext(), artikels);

                Log.d("artikelssss", artikels.toString());


            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        reloadArtikelList();

    }

    @Override
    public void onResume(){
        super.onResume();
        artikelViewModel.getArtikel();
    }

    public void reloadArtikelList() {
        artikelViewModel.getArtikel();
        artikelRepository.getArtikel();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_add_device:
                startActivity(new Intent(getActivity(), AddKategoriDeviceActivity.class));
                break;


        }
    }
}