package com.electivechaos.claimsadjuster.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.database.CategoryListDBHelper;
import com.electivechaos.claimsadjuster.pojo.PerilPOJO;
import com.electivechaos.claimsadjuster.utils.CommonUtils;

public class AddEditPerilActivity extends AppCompatActivity {
    private static final int UNIQUE_CONSTRAINT_FAIL_ERROR_CODE = -100;
    private String perilTitle = null;
    private String perilDescription = null;
    private int perilID = -1;
    private EditText name;
    private EditText description;
    private Button updateCategoryButton;
    private View addEditPerilParentLayout;
    private CategoryListDBHelper categoryListDBHelper;
    private int perilId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_peril);

        categoryListDBHelper = CategoryListDBHelper.getInstance(this);

        name = findViewById(R.id.name);
        description = findViewById(R.id.description);
        updateCategoryButton = findViewById(R.id.updatePeril);

        addEditPerilParentLayout = findViewById(R.id.addEditPerilParentLayout);


        if (getIntent().getExtras() != null) {
            Bundle dataFromActivity = getIntent().getBundleExtra("perilDetails");
            perilTitle = dataFromActivity.get("name").toString();
            perilDescription = dataFromActivity.get("description").toString();
            perilID = Integer.parseInt(dataFromActivity.get("id").toString());


            updateCategoryButton.setText("Update Peril");

            name.setText(perilTitle.toString());

            description.setText(perilDescription.toString());
        }

        updateCategoryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                name = findViewById(R.id.name);
                String nameString = name.getText().toString().trim();

                description = findViewById(R.id.description);
                String descriptionString = description.getText().toString().trim();

                Bundle data = new Bundle();


                if (nameString.isEmpty()) {
                    CommonUtils.hideKeyboard(AddEditPerilActivity.this);
                    CommonUtils.showSnackbarMessage("Please enter name.", true, true, addEditPerilParentLayout, AddEditPerilActivity.this);
                    return;
                } else if (descriptionString.isEmpty()) {
                    CommonUtils.hideKeyboard(AddEditPerilActivity.this);
                    CommonUtils.showSnackbarMessage("Please enter description.", true, true, addEditPerilParentLayout, AddEditPerilActivity.this);
                    return;
                } else {
                    if (perilID == -1) {
                        PerilPOJO perilPOJO = new PerilPOJO();
                        perilPOJO.setName(nameString);
                        perilPOJO.setDescription(descriptionString);

                        perilId = Integer.parseInt(String.valueOf(categoryListDBHelper.addPeril(perilPOJO)));
                        perilPOJO.setID(perilId);
                        if (perilId == UNIQUE_CONSTRAINT_FAIL_ERROR_CODE) {
                            Toast.makeText(AddEditPerilActivity.this, "Peril type with same name already exists.", Toast.LENGTH_SHORT).show();
                        } else {
                            data.putString("name", nameString);
                            data.putString("description", descriptionString);
                            Intent intent = new Intent();
                            intent.putExtra("perilDetails", data);
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        }

                    } else {
                        PerilPOJO perilPOJO = new PerilPOJO();
                        perilPOJO.setName(nameString);
                        perilPOJO.setDescription(descriptionString);
                        perilPOJO.setID(perilID);

                        int resultId = categoryListDBHelper.updatePeril(perilPOJO);
                        if (resultId == UNIQUE_CONSTRAINT_FAIL_ERROR_CODE) {
                            Toast.makeText(AddEditPerilActivity.this, "Peril type with same name already exists.", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent();
                            data.putString("name", nameString);
                            data.putString("description", descriptionString);
                            data.putInt("id", perilID);
                            intent.putExtra("perilDetails", data);
                            setResult(2, intent);
                            finish();
                        }

                    }
                }
            }
        });
    }
}
