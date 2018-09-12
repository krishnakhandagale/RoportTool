package com.electivechaos.claimsadjuster.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.utils.CommonUtils;

public class SettingsActivity extends AppCompatActivity {

    private View parentLayoutForMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        parentLayoutForMessages = findViewById(R.id.parentLayoutForMessages);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");

        RadioGroup radioQuality = findViewById(R.id.radioQuality);
        final RadioButton radioLow, radioMedium, radioHigh;

        radioLow = findViewById(R.id.radioLow);
        radioMedium = findViewById(R.id.radioMedium);
        radioHigh = findViewById(R.id.radioHigh);

        RadioGroup radioGoogleMap = findViewById(R.id.radioMap);
        final RadioButton radioMapActivate, radioMapDeactivate ;

        radioMapActivate = findViewById(R.id.radioMapActivate);
        radioMapDeactivate = findViewById(R.id.radioMapDeactivate);

        final EditText reportByEditText = findViewById(R.id.reportBy);
        final ImageButton editReportByBtn = findViewById(R.id.editBtn);
        final ImageButton doneReportByBtn = findViewById(R.id.doneBtn);

        if(!CommonUtils.getReportByField(this).isEmpty()){
            reportByEditText.setText(CommonUtils.getReportByField(this));
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

        if(CommonUtils.getGoogleMap(SettingsActivity.this).equals("true")){
            radioMapActivate.setChecked(true);
        }else {
            radioMapDeactivate.setChecked(true);
        }

        radioGoogleMap.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(radioMapActivate.isChecked()){
                    CommonUtils.setGoogleMap("true",SettingsActivity.this);
                }else {
                    CommonUtils.setGoogleMap("false",SettingsActivity.this);
                }
            }
        });


        reportByEditText.setEnabled(false);

        editReportByBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportByEditText.setEnabled(true);
                editReportByBtn.setVisibility(View.GONE);
                doneReportByBtn.setVisibility(View.VISIBLE);
            }
        });

        doneReportByBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String reportBy = reportByEditText.getText().toString();
                if(!reportBy.isEmpty()) {
                    reportByEditText.setEnabled(false);
                    doneReportByBtn.setVisibility(View.GONE);
                    editReportByBtn.setVisibility(View.VISIBLE);

                    CommonUtils.setReportByField(reportBy, SettingsActivity.this);
                }else {
                    CommonUtils.showSnackbarMessage(getString(R.string.please_enter_report_by), true, true,parentLayoutForMessages, SettingsActivity.this);
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
