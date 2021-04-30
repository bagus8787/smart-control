package com.example.smart_control.ui.user.feeder;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smart_control.R;

import java.util.Locale;

public class ScanWifiDeviceActivity extends AppCompatActivity {

    ImageView img_wifi;
    AnimationDrawable wifiAnimation;

    private static final long START_TIME_IN_MILLIS = 120000;

    private TextView txt_countdown;

    CountDownTimer countDownTimer;
    boolean mTimerRunning;
    long mTimeLeftInMillis = START_TIME_IN_MILLIS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_wifi_device);

        img_wifi    = findViewById(R.id.img_wifi);
        img_wifi.setBackgroundResource(R.drawable.animation_wifi);
        wifiAnimation = (AnimationDrawable) img_wifi.getBackground();

        txt_countdown = findViewById(R.id.txt_countdown);

        if (mTimerRunning){
            pauseTimer();
        } else {
            startTimer();
        }

        updateCountDownText();
    }

    private void startTimer(){
        countDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                Toast.makeText(ScanWifiDeviceActivity.this, "Scan", Toast.LENGTH_LONG).show();
            }
        }.start();

        mTimerRunning = true;
    }

    private void pauseTimer(){
        countDownTimer.cancel();
        Toast.makeText(ScanWifiDeviceActivity.this, "Scan", Toast.LENGTH_LONG).show();
    }

    private void updateCountDownText(){
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds);
        txt_countdown.setText(timeLeftFormatted);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        wifiAnimation.start();
    }
}