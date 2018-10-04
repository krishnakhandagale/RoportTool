package com.electivechaos.claimsadjuster.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.database.CategoryListDBHelper;
import com.electivechaos.claimsadjuster.pojo.RoofSystemPOJO;
import com.electivechaos.claimsadjuster.utils.CommonUtils;

public class RoofSystemActivity extends AppCompatActivity {

    private CategoryListDBHelper categoryListDBHelper;
    private static final int UNIQUE_CONSTRAINT_FAIL_ERROR_CODE = -111;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        categoryListDBHelper = CategoryListDBHelper.getInstance(this);

        setContentView(R.layout.activity_roof_system);
        Button addRoofSystemBtn = findViewById(R.id.addRoofSystem);
        final EditText roofName = findViewById(R.id.roofName);
        final View parentLayoutForMessages = findViewById(R.id.parentLayout);


        addRoofSystemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (roofName.getText().toString().isEmpty()) {
                    CommonUtils.showSnackbarMessage(getString(R.string.please_enter_name), true, true, parentLayoutForMessages, RoofSystemActivity.this);
                } else {
                    RoofSystemPOJO roofSystemPOJO = new RoofSystemPOJO();
                    roofSystemPOJO.setName(roofName.getText().toString());

                    long roofId = categoryListDBHelper.addRoofSystem(roofSystemPOJO);
                    if (roofId == UNIQUE_CONSTRAINT_FAIL_ERROR_CODE) {
                        Toast.makeText(RoofSystemActivity.this, "Roof System with same name already exists.", Toast.LENGTH_SHORT).show();
                    } else {
                        Bundle data = new Bundle();
                        data.putString("roofSystemName", roofName.getText().toString());
                        Intent intent = new Intent();
                        intent.putExtra("roofSystemDetails", data);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                }
            }
        });
    }

}
