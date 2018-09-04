package com.electivechaos.claimsadjuster.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.utils.CommonUtils;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");

        RadioGroup radioQuality = findViewById(R.id.radioQuality);
        final RadioButton radioLow, radioMedium, radioHigh;

        radioLow = findViewById(R.id.radioLow);
        radioMedium = findViewById(R.id.radioMedium);
        radioHigh = findViewById(R.id.radioHigh);

           if(CommonUtils.getReportQuality(SettingsActivity.this).equals("low")){
               radioLow.setChecked(true);
           }else if(CommonUtils.getReportQuality(SettingsActivity.this).equals("high")){
               radioHigh.setChecked(true);
           }else {
               radioMedium.setChecked(true);
           }

        radioQuality.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(radioLow.isChecked()){
                    CommonUtils.setReportQuality("low",SettingsActivity.this);
                }else if(radioHigh.isChecked()){
                    CommonUtils.setReportQuality("high",SettingsActivity.this);
                }else {
                    CommonUtils.setReportQuality("medium",SettingsActivity.this);
                }
            }
        });



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
