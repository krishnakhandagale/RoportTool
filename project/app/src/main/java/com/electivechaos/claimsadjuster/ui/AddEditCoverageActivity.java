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
import com.electivechaos.claimsadjuster.pojo.CoveragePOJO;
import com.electivechaos.claimsadjuster.utils.CommonUtils;

public class AddEditCoverageActivity extends AppCompatActivity{
    private  String coverageTitle = null;
    private  String coverageDescription = null;
    private  int coverageID = -1;

    private EditText name;
    private  EditText description;
    private Button updateCoverageButton;
    private View addEditCoverageParentLayout;

    private static final int UNIQUE_CONSTRAINT_FAIL_ERROR_CODE = -100;
    private CategoryListDBHelper categoryListDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_coverage);

        categoryListDBHelper = CategoryListDBHelper.getInstance(this);

        name = findViewById(R.id.name);
        description = findViewById(R.id.description);
        updateCoverageButton = findViewById(R.id.updateCoverage);

        addEditCoverageParentLayout = findViewById(R.id.addEditCoverageParentLayout);


        if(getIntent().getExtras()!= null){
            Bundle dataFromActivity = getIntent().getBundleExtra("coverageDetails");
            coverageTitle = dataFromActivity.get("name").toString();
            coverageDescription = dataFromActivity.get("description").toString();
            coverageID = Integer.parseInt(dataFromActivity.get("id").toString());


            updateCoverageButton.setText("Update Coverage");

            name.setText(coverageTitle.toString());

            description.setText(coverageDescription.toString());
        }

        updateCoverageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                name = findViewById(R.id.name);
                String nameString = name.getText().toString().trim();

                description = findViewById(R.id.description);
                String descriptionString = description.getText().toString().trim();

                Bundle data = new Bundle();


                if(nameString.isEmpty()){
                    CommonUtils.hideKeyboard(AddEditCoverageActivity.this);
                    CommonUtils.showSnackbarMessage("Please enter name.", true,true, addEditCoverageParentLayout,AddEditCoverageActivity.this);
                }else if(descriptionString.isEmpty()){
                    CommonUtils.hideKeyboard(AddEditCoverageActivity.this);
                    CommonUtils.showSnackbarMessage("Please enter description.", true,true, addEditCoverageParentLayout, AddEditCoverageActivity.this);
                }else {
                    if(coverageID == -1) {
                        CoveragePOJO coveragePOJO = new CoveragePOJO();
                        coveragePOJO.setName(nameString);
                        coveragePOJO.setDescription(descriptionString);

                        int resultId = Integer.parseInt(String.valueOf(categoryListDBHelper.addCoverage(coveragePOJO)));

                        if (resultId == UNIQUE_CONSTRAINT_FAIL_ERROR_CODE) {
                            Toast.makeText(AddEditCoverageActivity.this, "Coverage type with same name already exists.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            data.putString("name", nameString);
                            data.putString("description", descriptionString);
                            data.putInt("id", resultId);
                            Intent intent = new Intent();
                            intent.putExtra("coverageDetails", data);
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        }

                    }else {
                        CoveragePOJO coveragePOJO = new CoveragePOJO();
                        coveragePOJO.setName(nameString);
                        coveragePOJO.setDescription(descriptionString);
                        coveragePOJO.setID(coverageID);

                        int resultId = categoryListDBHelper.updateCoverage(coveragePOJO);
                        if (resultId == UNIQUE_CONSTRAINT_FAIL_ERROR_CODE) {
                            Toast.makeText(AddEditCoverageActivity.this, "Coverage type with same name already exists.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Intent intent = new Intent();
                            data.putString("name", nameString);
                            data.putString("description", descriptionString);
                            data.putInt("id", coverageID);
                            intent.putExtra("coverageDetails", data);
                            setResult(2, intent);
                            finish();
                        }

                    }
                }
            }
        });
    }
}
