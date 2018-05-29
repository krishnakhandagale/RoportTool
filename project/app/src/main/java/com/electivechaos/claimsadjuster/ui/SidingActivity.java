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

public class SidingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_siding);
        Button addSidingBtn = findViewById(R.id.addSiding);
        final EditText sidingName = findViewById(R.id.sidingName);
        final View parentLayoutForMessages = findViewById(R.id.parentLayout);

        addSidingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sidingName.getText().toString().isEmpty())
                {
                    CommonUtils.showSnackbarMessage(getString(R.string.please_enter_name), true, true, parentLayoutForMessages, SidingActivity.this);
                }else {
                    Bundle data = new Bundle();
                    data.putString("sidingName", sidingName.getText().toString());
                    Intent intent = new Intent();
                    intent.putExtra("sidingDetails", data);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        });
    }
}
