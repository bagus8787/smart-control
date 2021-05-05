package com.example.smart_control.mqtt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smart_control.R;
import com.example.smart_control.receiver.WifiReceiver;
import com.example.smart_control.utils.SharedPrefManager;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MqttTesActivity extends AppCompatActivity {
    String LOGTAG ="mqttttttt";
    MqttHelper mqttHelper;
    SharedPrefManager sharedPrefManager;

    Context context;
    TextView dataReceived;
    TextView textView,textView1,textView2,olcum;
    SearchableSpinner spinner_wifi;

//    Scan Wifi
    private ListView wifiList;
    private WifiManager wifiManager;
    private final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 1;
    WifiReceiver receiverWifi;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mqtt_tes);

        context = getApplicationContext();
        sharedPrefManager = new SharedPrefManager(this);

        textView = (TextView) findViewById(R.id.textView);
        textView1 = (TextView) findViewById(R.id.textView2);
        textView2 = (TextView) findViewById(R.id.textView3);
        olcum = (TextView) findViewById(R.id.textView5);

        dataReceived = (TextView) findViewById(R.id.dataReceived);
//        chart = (LineChart) findViewById(R.id.chart);
//        mChart = new ChartHelper(chart);

        spinner_wifi = findViewById(R.id.spinner_wifi);

        spinner_wifi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorBlack));

                String nama = parent.getItemAtPosition(position).toString();

                Toast.makeText(MqttTesActivity.this, nama, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        wifiList = findViewById(R.id.wifiList);
        Button buttonScan = findViewById(R.id.scanBtn);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(getApplicationContext(), "Turning WiFi ON...", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }
        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MqttTesActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            MqttTesActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
                } else {
                    wifiManager.startScan();
                }
            }
        });

//        startMqtt();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        receiverWifi = new WifiReceiver(wifiManager, wifiList, spinner_wifi);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(receiverWifi, intentFilter);
        getWifi();
    }

    private void getWifi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Toast.makeText(MqttTesActivity.this, "version> = marshmallow", Toast.LENGTH_SHORT).show();
            if (ContextCompat.checkSelfPermission(MqttTesActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MqttTesActivity.this, "location turned off", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(MqttTesActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
            } else {
                Toast.makeText(MqttTesActivity.this, "location turned on", Toast.LENGTH_SHORT).show();
                wifiManager.startScan();
            }
        } else {
            Toast.makeText(MqttTesActivity.this, "scanning", Toast.LENGTH_SHORT).show();
            wifiManager.startScan();
        }
    }

//    public void ListWifi(){
//
//        Call<ProvinsiResponse> provinsiResponseCall = apiInterface.getProv();
//        provinsiResponseCall.enqueue(new Callback<ProvinsiResponse>() {
//            @Override
//            public void onResponse(Call<ProvinsiResponse> call, Response<ProvinsiResponse> response) {
//                provinsiArrayAdapter = new ArrayAdapter<Provinsi>(Register2Activity.this, android.R.layout.simple_spinner_dropdown_item, response.body().getProvinsis());
//                provinsiArrayAdapter.setDropDownViewResource(R.layout.list_item_region);
//                searchableSpinnerProv.setTitle("Pilih Provinsi");
//                searchableSpinnerProv.setAdapter(provinsiArrayAdapter);
//            }
//
//            @Override
//            public void onFailure(Call<ProvinsiResponse> call, Throwable t) {
//
//            }
//        });
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MqttTesActivity.this, "permission granted", Toast.LENGTH_SHORT).show();
                wifiManager.startScan();
            } else {
                Toast.makeText(MqttTesActivity.this, "permission not granted", Toast.LENGTH_SHORT).show();
                return;
            }
            break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiverWifi);
    }

    private void startMqtt(){
        mqttHelper = new MqttHelper(getApplicationContext());
        mqttHelper.mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                Log.w("Debug","Connected");
            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.w("Debugss",mqttMessage.toString());
                Log.w("topicss", topic.toString());

                switch (topic.toString()){
                    case "USW1000001/feeder/time":
                        Log.w("mqttmessage", mqttMessage.toString());
                        textView.setText(mqttMessage.toString());
                        break;
                    case "USW1000001/feeder/jarak":
                        textView1.setText(mqttMessage.toString());
                        break;
                    case "USW1000001/feeder/status":
                        Log.w("mqttstatus", mqttMessage.toString());

                        textView2.setText(mqttMessage.toString());
                        break;
                    case "sensor/text3":
                        olcum.setText(mqttMessage.toString());
                        break;
                    case "sensor/olcum":
                        olcum.setText(mqttMessage.toString());
                        olcum.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                        break;

                    default:
                        Log.d("Error","Error ocquired");
                }

                //         dataReceived.setText(mqttMessage.toString());
//                mChart.addEntry(Float.valueOf(mqttMessage.toString()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });

        publish();
    }

    public void publish(){

        Integer p = 1;

        String json = "{\"count\":"+ p.toString() +", \"secret_key\":" + sharedPrefManager.getSpSecretKey() +"}";
        String user = "";

        try {
            JSONObject obj = new JSONObject(json);
            MqttHelper mqttHelper;
            mqttHelper = new MqttHelper(context);
            mqttHelper.serverUri.toString();

            MemoryPersistence memPer = new MemoryPersistence();
            final MqttAndroidClient client = new MqttAndroidClient(
                    context, mqttHelper.serverUri.toString(), user, memPer);
            Log.d("clientt", client.toString());
            try {
                client.connect(null, new IMqttActionListener() {

                    @Override
                    public void onSuccess(IMqttToken mqttToken) {
                        Log.i(LOGTAG, "Client connected");

                        MqttMessage mqttMessage = new MqttMessage();
                        mqttMessage.setPayload(json.getBytes());
                        mqttMessage.setQos(2);
                        mqttMessage.setRetained(false);

                        try {
                            client.publish(sharedPrefManager.getSpIdDevice() + "/control/beri_pakan", mqttMessage);
                            Log.i(LOGTAG, "Message published");

                        } catch (MqttPersistenceException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();

                        } catch (MqttException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(IMqttToken arg0, Throwable arg1) {
                        // TODO Auto-generated method stub
                        Log.i(LOGTAG, "Client connection failed: "+arg1.getMessage());

                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
            Log.d("MyAppmmm", obj.toString());
            Log.d("phonetypevalue", obj.getString("phonetype"));

        } catch (Throwable tx) {
            Log.e("MyApp", "Could not parse malformed JSON: \"" + json + "\""
            );
        }

        String a = "";


    }
}