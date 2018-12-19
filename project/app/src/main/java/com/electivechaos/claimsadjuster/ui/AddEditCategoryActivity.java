package com.electivechaos.claimsadjuster.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.adapters.CustomMenuAdapter;
import com.electivechaos.claimsadjuster.asynctasks.DBPropertyDetailsListTsk;
import com.electivechaos.claimsadjuster.database.CategoryListDBHelper;
import com.electivechaos.claimsadjuster.interfaces.AsyncTaskStatusCallback;
import com.electivechaos.claimsadjuster.pojo.Category;
import com.electivechaos.claimsadjuster.pojo.CoveragePOJO;
import com.electivechaos.claimsadjuster.utils.CommonUtils;

import java.util.ArrayList;

/**
 * Created by barkhasikka on 25/04/18.
 */

public class AddEditCategoryActivity extends AppCompatActivity {
    public static final int ADD_COVERAGE_REQUEST_CODE = 2000;
    private static final int UNIQUE_CONSTRAINT_FAIL_ERROR_CODE = -111;
    private int categoryID = -1;
    private EditText categoryName;
    private EditText categoryDescription;
    private View addEditCategoryParentLayout;
    private CategoryListDBHelper categoryListDBHelper;
    private TextView selectCoverageType;
    private String coverageType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_category);

        categoryListDBHelper = CategoryListDBHelper.getInstance(this);
        categoryName = findViewById(R.id.editTextCategoryName);
        categoryDescription = findViewById(R.id.editTextCategoryDescription);
        selectCoverageType = findViewById(R.id.selectCoverageType);
        addEditCategoryParentLayout = findViewById(R.id.addEditCategoryParentLayout);
        if (getIntent().getExtras() != null) {
            Bundle dataFromActivity = getIntent().getExtras().getBundle("categoryDetails");
            String intentCategoryTitle = dataFromActivity.getString("categoryName", "");
            String intentCategoryDescription = dataFromActivity.getString("categoryDescription", "");
            String intentCategoryCoverageType = dataFromActivity.getString("categoryCoverageType", "");
            categoryID = dataFromActivity.getInt("categoryID");

            Button updateCategoryButton = findViewById(R.id.updateCategory);
            updateCategoryButton.setText(R.string.update_category);


            categoryName.setText(intentCategoryTitle);

            categoryDescription.setText(intentCategoryDescription);
            if (!intentCategoryCoverageType.isEmpty()) {
                selectCoverageType.setText(intentCategoryCoverageType);
            }
            categoryDescription.setText(intentCategoryDescription);
        }

        if (savedInstanceState != null) {
            coverageType = savedInstanceState.getString("coverageType");
            if (coverageType != null && !coverageType.isEmpty()) {
                selectCoverageType.setText(coverageType);
            }
        }

        final Button updateCategoryButton = findViewById(R.id.updateCategory);
        updateCategoryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String categoryNameString = categoryName.getText().toString().trim();


                String categoryDescriptionString = categoryDescription.getText().toString().trim();
                String categoryCoverageType = selectCoverageType.getText().toString().trim();

                if (categoryCoverageType.equals(getString(R.string.select_coverage_type))) {
                    categoryCoverageType = "";
                }

                if (categoryNameString.isEmpty()) {
                    CommonUtils.hideKeyboard(AddEditCategoryActivity.this);
                    CommonUtils.showSnackbarMessage("Please enter category name.", true, true, addEditCategoryParentLayout, AddEditCategoryActivity.this);
                    return;
                } else if (categoryDescriptionString.isEmpty()) {
                    CommonUtils.hideKeyboard(AddEditCategoryActivity.this);
                    CommonUtils.showSnackbarMessage("Please enter category description.", true, true, addEditCategoryParentLayout, AddEditCategoryActivity.this);
                    return;
                } else {
                    Bundle data = new Bundle();
                    int catId;
                    if (categoryID == -1) {
                        Category categoryPOJO = new Category();
                        categoryPOJO.setCategoryName(categoryNameString);
                        categoryPOJO.setCategoryDescription(categoryDescriptionString);
                        categoryPOJO.setCoverageType(categoryCoverageType);

                        catId = Integer.parseInt(String.valueOf(categoryListDBHelper.addCategory(categoryPOJO)));

                        if (catId == UNIQUE_CONSTRAINT_FAIL_ERROR_CODE) {
                            Toast.makeText(AddEditCategoryActivity.this, "Category type with same name already exists.", Toast.LENGTH_SHORT).show();
                        } else {

                            categoryPOJO.setCategoryId(catId);
                            if (categoryID == -1) {
                                data.putInt("categoryId", catId);
                                data.putString("categoryName", categoryNameString);
                                data.putString("categoryDescription", categoryDescriptionString);
                                data.putString("categoryCoverageType", categoryCoverageType);
                                Intent intent = new Intent();
                                intent.putExtra("categoryDetails", data);
                                setResult(Activity.RESULT_OK, intent);
                                finish();
                            }
                        }
                    } else {
                        Category categoryPOJO = new Category();
                        categoryPOJO.setCategoryId(categoryID);
                        categoryPOJO.setCategoryName(categoryNameString);
                        categoryPOJO.setCategoryDescription(categoryDescriptionString);
                        categoryPOJO.setCoverageType(categoryCoverageType);
                        catId = categoryListDBHelper.updateCategory(categoryPOJO);

                        if (catId == UNIQUE_CONSTRAINT_FAIL_ERROR_CODE) {
                            Toast.makeText(AddEditCategoryActivity.this, "Category type with same name already exists.", Toast.LENGTH_SHORT).show();

                        } else {
                            categoryPOJO.setCategoryId(categoryID);
                            Intent intent = new Intent();
                            data.putString("categoryName", categoryNameString);
                            data.putString("categoryDescription", categoryDescriptionString);
                            data.putString("categoryCoverageType", categoryCoverageType);
                            data.putInt("categoryID", categoryID);
                            intent.putExtra("categoryDetails", data);
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        }
                    }
                }
            }
        });

        selectCoverageType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DBPropertyDetailsListTsk(categoryListDBHelper, "coverage_type", new AsyncTaskStatusCallback() {
                    @Override
                    public void onPostExecute(Object object, String type) {

                        final ArrayList<CoveragePOJO> coveragePOJOS = (ArrayList<CoveragePOJO>) object;

                        final AlertDialog.Builder ad = new AlertDialog.Builder(AddEditCategoryActivity.this);

                        ad.setCancelable(true);
                        ad.setPositiveButton("Add New", new DialogInterface.OnClickListener() {


                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(AddEditCategoryActivity.this, AddEditCoverageActivity.class);
                                startActivityForResult(intent, ADD_COVERAGE_REQUEST_CODE);
                            }
                        });
                        ad.setTitle("Coverage Type");
                        if (coveragePOJOS.size() == 0) {
                            ad.setMessage("No coverage types found.");
                        }
                        CustomMenuAdapter adapter = new CustomMenuAdapter(coveragePOJOS, selectCoverageType.getText().toString(), "coverage_type");
                        ad.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int position) {

                                selectCoverageType.setText(coveragePOJOS.get(position).getName());
                                coverageType = coveragePOJOS.get(position).getName();
                                dialogInterface.dismiss();

                            }
                        });

                        ad.show();
                        CommonUtils.unlockOrientation(AddEditCategoryActivity.this);
                    }

                    @Override
                    public void onPreExecute() {
                        CommonUtils.lockOrientation(AddEditCategoryActivity.this);
                    }

                    @Override
                    public void onProgress(int progress) {

                    }
                }).execute();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == ADD_COVERAGE_REQUEST_CODE) {
                selectCoverageType.setText(data.getBundleExtra("coverageDetails").getString("name"));
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("coverageType", coverageType);
    }
}
