package com.example.smart_control.ui.user.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.smart_control.R;

public class DetailDevicesActivity extends AppCompatActivity {
    ImageView mImageView;
    TextView mResultView;
    View mColorView;

    Bitmap bitmap;
    String id, time, count, time_now;
    EditText ip_time_before, ip_count;
    TimePicker datePicker1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_devices);

        id  = getIntent().getStringExtra("IT_ID");
        time  = getIntent().getStringExtra("IT_TIME");
        count  = getIntent().getStringExtra("IT_COUNT");

        ip_time_before = findViewById(R.id.ip_time_before);
        ip_count = findViewById(R.id.ip_count);
        datePicker1 = findViewById(R.id.datePicker1);
        datePicker1.setIs24HourView(true);

        ip_time_before.setText(time);
        ip_count.setText(count);

        datePicker1.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                time_now = String.format("%02d:%02d",hourOfDay, minute);

                setTime(time_now);
            }

        });

        mImageView = findViewById(R.id.imageView);
        mResultView = findViewById(R.id.result);
        mColorView = findViewById(R.id.colorView);

        mImageView.setDrawingCacheEnabled(true);
        mImageView.buildDrawingCache(true);

        mImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE){
                    bitmap = mImageView.getDrawingCache();

                    int pixel = bitmap.getPixel((int) event.getX(), (int)event.getY());

                    int r   = Color.red(pixel);
                    int g   = Color.green(pixel);
                    int b   = Color.blue(pixel);

                    String hex = "#" + Integer.toHexString(pixel);

                    mColorView.setBackgroundColor(Color.rgb(r,g,b));

                    mResultView.setText("RGB: "+ r +", " +g +", "+b
                            + "\nHEX: " + hex);

                }
                return true;
            }
        });
    }

    private void setTime(String time) {
        time_now = time;
        Log.d("timessskkk", "= " + time_now);
    }
}