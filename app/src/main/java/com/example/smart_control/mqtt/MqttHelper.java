package com.example.smart_control.mqtt;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.smart_control.utils.SharedPrefManager;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttHelper {
    public MqttAndroidClient mqttAndroidClient;

    public String serverUri = "tcp://178.128.217.111:1883";
    public String serverIp = "178.128.217.111";

    String clientId = "";
    final String subscriptionTopic = "USW1000001/#/#";

    final String username = "";
    final String password = "";

    SharedPrefManager sharedPrefManager;

    public MqttHelper(Context context){
        sharedPrefManager = new SharedPrefManager(context);

        clientId = sharedPrefManager.getSpIdDevice();

        Log.d("idddd", clientId.toString());

        mqttAndroidClient = new MqttAndroidClient(context, serverUri, clientId);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                Log.w("mqtt", s);
            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.w("Mqtt", mqttMessage.toString());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
        connect();
    }

    public void setCallback(MqttCallbackExtended callback) {
        mqttAndroidClient.setCallback(callback);
    }

    private void connect(){
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
//        mqttConnectOptions.setUserName(username);
//        mqttConnectOptions.setPassword(password.toCharArray());

        try {

            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    subscribeToTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w("Mqtt", "Failed to connect to: " + serverUri + exception.toString());
                }
            });


        } catch (MqttException ex){
            ex.printStackTrace();
        }
    }


    private void subscribeToTopic() {
        try {
            mqttAndroidClient.subscribe(subscriptionTopic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.w("Mqtt","Subscribed!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w("Mqtt", "Subscribed fail!");
                }
            });

        } catch (MqttException ex) {
            System.err.println("Exceptionst subscribing");
            ex.printStackTrace();
        }
    }

    public void published(Context context, String secret_key){
        Log.d("secret_key", secret_key);

        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload("{count: 4, secret_key: USW1000001-d15b468e-1e87-4b96-9686-603401ea691e}".getBytes());

//        try {
//            mqttAndroidClient.publish("USW1000001/control/beri_pakan", "{count: 4, secret_key: 'USW1000001-d15b468e-1e87-4b96-9686-603401ea691e'}".getBytes(), 0, false);
//            Toast.makeText(context,"Published Messageeee", Toast.LENGTH_SHORT).show();
//        } catch ( MqttException e) {
//            e.printStackTrace();
//        }
//
//        MqttMessage message = new MqttMessage();
//        message.setPayload("{count: 4, secret_key: 'USW1000001-d15b468e-1e87-4b96-9686-603401ea691e'}".getBytes());
//        try {
//            Log.d("mesage", message.toString());
//
//            mqttAndroidClient.publish("USW1000001/control/beri_pakan", message);
//            Toast.makeText(context,"Published Messageeeerrrrrrr", Toast.LENGTH_SHORT).show();
//
//        } catch (MqttException e) {
//            e.printStackTrace();
//        }

//        String topic = "event";
//        String message = "the payload";
//
//        try {
//            mqttAndroidClient.publish(topic, message.getBytes(),0,false);
//            Toast.makeText(context,"Published Message", Toast.LENGTH_SHORT).show();
//        } catch ( MqttException e) {
//            e.printStackTrace();
//        }
    }
}
