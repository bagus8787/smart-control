package com.example.smart_control.ui.user.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.smart_control.MainActivity;
import com.example.smart_control.Myapp;
import com.example.smart_control.R;
import com.example.smart_control.ui.user.fragment.HomeFragment;
import com.example.smart_control.ui.user.fragment.PesanFragment;
import com.example.smart_control.ui.user.fragment.ProfilFragment;
import com.example.smart_control.ui.user.fragment.SmartFragment;
import com.example.smart_control.utils.SharedPrefManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    SharedPrefManager sharedPrefManager;
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sharedPrefManager = new SharedPrefManager(Myapp.getContext());

        //Load Fragment
        loadFragment(new HomeFragment());

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        loadFragment(new HomeFragment());
                        return true;

                    case R.id.navigation_smart:
                        loadFragment(new SmartFragment());
                        return true;

                    case R.id.navigation_acount:
                        loadFragment(new ProfilFragment());
                        return true;

                    default:
                        return false;
                }
            }
        });
    }


    private void loadFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction;
        fragmentTransaction = getSupportFragmentManager().beginTransaction()
                .replace(R.id.layout_container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
//        finish();
        new AlertDialog.Builder(this)
                .setMessage("Apakah anda mau keluar dari Aplikasi Smart Control?")
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        HomeActivity.super.onBackPressed();
                        sharedPrefManager.saveSPString(SharedPrefManager.NAME_ARTIKEL, "artikel");
                    }
                })
                .setNegativeButton("Tidak", null)
                .show();
    }
}