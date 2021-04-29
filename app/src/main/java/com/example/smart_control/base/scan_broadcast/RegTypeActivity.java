package com.example.smart_control.base.scan_broadcast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.smart_control.Myapp;
import com.example.smart_control.R;
import com.example.smart_control.base.scan_broadcast.fragment.ServiceBrowserFragment;
import com.github.druk.rx2dnssd.BonjourService;

public class RegTypeActivity extends AppCompatActivity implements ServiceBrowserFragment.ServiceListener {

    private static final String KEY_REG_TYPE = "com.druk.servicebrowser.ui.RegTypeActivity.KEY_DOMAIN";
    private static final String KEY_DOMAIN = "com.druk.servicebrowser.ui.RegTypeActivity.KEY_REG_TYPE";

    public static Intent createIntent(Context context, String regType, String domain) {
        return new Intent(context, RegTypeActivity.class).
                putExtra(RegTypeActivity.KEY_DOMAIN, domain).
                putExtra(RegTypeActivity.KEY_REG_TYPE, regType);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_type);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        if (getIntent() != null && getIntent().hasExtra(KEY_DOMAIN) && getIntent().hasExtra(KEY_REG_TYPE)) {
            String regType = getIntent().getStringExtra(KEY_REG_TYPE);
            String domain = getIntent().getStringExtra(KEY_DOMAIN);
            String description = Myapp.getRegTypeManager(this).getRegTypeDescription(regType);
            if (description != null) {
                setTitle(description);
            } else {
                setTitle(regType);
            }
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.content, ServiceBrowserFragment.newInstance(domain, regType)).commit();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onServiceWasSelected(String domain, String regType, BonjourService service) {
        ServiceActivity.startActivity(this, service);
    }
}
