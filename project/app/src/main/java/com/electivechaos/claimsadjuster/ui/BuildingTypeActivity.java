package com.electivechaos.claimsadjuster.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.database.CategoryListDBHelper;
import com.electivechaos.claimsadjuster.pojo.BuildingTypePOJO;
import com.electivechaos.claimsadjuster.utils.CommonUtils;

public class BuildingTypeActivity extends AppCompatActivity {

    private static final int UNIQUE_CONSTRAINT_FAIL_ERROR_CODE = -111;
    private CategoryListDBHelper categoryListDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_type);

        categoryListDBHelper = CategoryListDBHelper.getInstance(this);

        Button addBuildingBtn = findViewById(R.id.addBuilding);
        final EditText buildingName = findViewById(R.id.buildingName);
        final View parentLayoutForMessages = findViewById(R.id.parentLayout);


        addBuildingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buildingName.getText().toString().isEmpty()) {
                    CommonUtils.showSnackbarMessage(getString(R.string.please_enter_name), true, true, parentLayoutForMessages, BuildingTypeActivity.this);
                } else {

                    BuildingTypePOJO buildingTypePOJO = new BuildingTypePOJO();
                    buildingTypePOJO.setName(buildingName.getText().toString());

                    long buildingTypeId = categoryListDBHelper.addBuildingType(buildingTypePOJO);
                    if (buildingTypeId == UNIQUE_CONSTRAINT_FAIL_ERROR_CODE) {
                        Toast.makeText(BuildingTypeActivity.this, "Building type with same name already exists.", Toast.LENGTH_SHORT).show();
                    } else {
                        Bundle data = new Bundle();
                        data.putString("buildingName", buildingName.getText().toString());
                        Intent intent = new Intent();
                        intent.putExtra("buildingDetails", data);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                }
            }
        });
    }
}
