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

public class BuildingTypeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_type);
        Button addBuildingBtn = findViewById(R.id.addBuilding);
        final EditText buildingName = findViewById(R.id.buildingName);
        final View parentLayoutForMessages = findViewById(R.id.parentLayout);
        

        addBuildingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(buildingName.getText().toString().isEmpty())
                {
                    CommonUtils.showSnackbarMessage(getString(R.string.please_enter_name), true, true, parentLayoutForMessages, BuildingTypeActivity.this);
                }else {
                    Bundle data = new Bundle();
                    data.putString("buildingName", buildingName.getText().toString());
                    Intent intent = new Intent();
                    intent.putExtra("buildingDetails", data);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        });
    }
}
