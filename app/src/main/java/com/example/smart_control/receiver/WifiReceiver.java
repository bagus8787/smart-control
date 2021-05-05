package com.example.smart_control.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.smart_control.R;
import com.example.smart_control.ui.user.feeder.HomeFeederActivity;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.List;

public class WifiReceiver extends BroadcastReceiver {
    WifiManager wifiManager;
    StringBuilder sb;
    ListView wifiDeviceList;
    SearchableSpinner spinner_wifi;

    public WifiReceiver(WifiManager wifiManager, ListView wifiDeviceList, SearchableSpinner spinner_wifi) {
        this.wifiManager = wifiManager;
        this.wifiDeviceList = wifiDeviceList;
        this.spinner_wifi = spinner_wifi;
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
            sb = new StringBuilder();
            List<ScanResult> wifiList = wifiManager.getScanResults();
            ArrayList<String> deviceList = new ArrayList<>();
            for (ScanResult scanResult : wifiList) {
                sb.append("\n").append(scanResult.SSID).append(" - ").append(scanResult.capabilities);
                deviceList.add(scanResult.SSID);
            }

//            Toast.makeText(context, sb, Toast.LENGTH_SHORT).show();
//            ListWifi
            ArrayAdapter arrayAdapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, deviceList.toArray());
            wifiDeviceList.setAdapter(arrayAdapter);

//            Spinner wifi
            ArrayAdapter spinerAdapter = new ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, deviceList.toArray());
            spinerAdapter.setDropDownViewResource(R.layout.list_item_wifi);
            spinner_wifi.setTitle("Pilih WiFi");
            spinner_wifi.setAdapter(spinerAdapter);
        }
    }
}
