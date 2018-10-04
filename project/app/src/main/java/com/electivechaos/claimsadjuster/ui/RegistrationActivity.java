package com.electivechaos.claimsadjuster.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.utils.CommonUtils;

public class RegistrationActivity extends AppCompatActivity {

    private View parentLayoutForMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        parentLayoutForMessages = findViewById(R.id.parentLayoutForMessages);

        final EditText reportByEditText = findViewById(R.id.reportBy);
        final EditText emailIdEditText = findViewById(R.id.emailId);


        Button registerBtn = findViewById(R.id.btnRegister);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 String reportByString = reportByEditText.getText().toString();
                 String emailIdString = emailIdEditText.getText().toString();

                if(validate(reportByString, emailIdString)){
                    CommonUtils.setEmailId(emailIdString, RegistrationActivity.this);
                    CommonUtils.setReportByField(reportByString, RegistrationActivity.this);
                    Intent i = new Intent(RegistrationActivity.this,MainTabsActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });


    }

    private boolean validate(String reportBy, String emailId) {
        if(reportBy.trim().isEmpty()){
            CommonUtils.showSnackbarMessage(getString(R.string.please_enter_report_by), true, true,parentLayoutForMessages, RegistrationActivity.this);
            return false;
        }
        else if(emailId.trim().isEmpty()) {
            CommonUtils.showSnackbarMessage(getString(R.string.please_enter_email_id), true, true,parentLayoutForMessages, RegistrationActivity.this);
            return false;
        }else {
            return true;
        }
    }

}
