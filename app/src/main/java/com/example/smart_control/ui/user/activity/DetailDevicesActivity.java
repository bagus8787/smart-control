package com.example.smart_control.ui.user.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smart_control.R;

public class DetailDevicesActivity extends AppCompatActivity {
    ImageView mImageView;
    TextView mResultView;
    View mColorView;

    Bitmap bitmap;
    String id, time, count;
    EditText ip_time, ip_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_devices);

        id  = getIntent().getStringExtra("IT_ID");
        time  = getIntent().getStringExtra("IT_TIME");
        count  = getIntent().getStringExtra("IT_COUNT");

        ip_time = findViewById(R.id.ip_time);
        ip_count = findViewById(R.id.ip_count);

        ip_time.setText(time);
        ip_count.setText(count);

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
}