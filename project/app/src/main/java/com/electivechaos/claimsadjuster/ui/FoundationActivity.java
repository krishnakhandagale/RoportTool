package com.electivechaos.claimsadjuster.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.utils.CommonUtils;

public class FoundationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foundation);
        Button addFoundationBtn = findViewById(R.id.addFoundation);
        final EditText foundationName = findViewById(R.id.foundationName);
        final View parentLayoutForMessages = findViewById(R.id.parentLayout);


        addFoundationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(foundationName.getText().toString().isEmpty())
                {
                    CommonUtils.showSnackbarMessage(getString(R.string.please_enter_name), true, true, parentLayoutForMessages, FoundationActivity.this);
                }else {
                    Bundle data = new Bundle();
                    data.putString("foundationName", foundationName.getText().toString());
                    Intent intent = new Intent();
                    intent.putExtra("foundationDetails", data);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        });
    }
}
