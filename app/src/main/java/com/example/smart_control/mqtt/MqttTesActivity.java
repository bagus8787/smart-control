package com.example.smart_control.mqtt;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.smart_control.R;
import com.example.smart_control.utils.SharedPrefManager;

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

public class MqttTesActivity extends AppCompatActivity {
    String LOGTAG ="mqttttttt";
    MqttHelper mqttHelper;
    SharedPrefManager sharedPrefManager;


//    ChartHelper mChart;
//    LineChart chart;

    Context context;
    TextView dataReceived;
    TextView textView,textView1,textView2,olcum;

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

        startMqtt();
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