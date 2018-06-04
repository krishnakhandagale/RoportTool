package com.electivechaos.claimsadjuster.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.database.CategoryListDBHelper;
import com.electivechaos.claimsadjuster.pojo.Category;
import com.electivechaos.claimsadjuster.utils.CommonUtils;

/**
 * Created by barkhasikka on 25/04/18.
 */

public class AddEditCategoryActivity extends AppCompatActivity{
    private  String intentCategoryTitle = null;
    private  String intentCategoryDescription = null;
    private  int categoryID = -1;

    private  EditText categoryName;
    private EditText categoryDescription;
    private View addEditCategoryParentLayout;
    private CategoryListDBHelper categoryListDBHelper;
    private int catId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_category);

        categoryListDBHelper = CategoryListDBHelper.getInstance(this);
        categoryName = findViewById(R.id.editTextCategoryName);
        categoryDescription = findViewById(R.id.editTextCategoryDescription);
        addEditCategoryParentLayout = findViewById(R.id.addEditCategoryParentLayout);

        if(getIntent().getExtras()!= null){
            Bundle dataFromActivity = getIntent().getExtras().getBundle("categoryDetails");
            intentCategoryTitle = dataFromActivity.get("categoryName").toString();
            intentCategoryDescription = dataFromActivity.get("categoryDescription").toString();
            categoryID = Integer.parseInt(dataFromActivity.get("categoryID").toString());

            Button updateCategoryButton = findViewById(R.id.updateCategory);
            updateCategoryButton.setText(R.string.update_category);


            categoryName.setText(intentCategoryTitle.toString());

            categoryDescription.setText(intentCategoryDescription.toString());
        }

        final Button updateCategoryButton = findViewById(R.id.updateCategory);
        updateCategoryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String categoryNameString = categoryName.getText().toString().trim();


                String categoryDescriptionString = categoryDescription.getText().toString().trim();

                if(categoryNameString.isEmpty()){
                    CommonUtils.hideKeyboard(AddEditCategoryActivity.this);
                    CommonUtils.showSnackbarMessage("Please enter category name.", true, true,addEditCategoryParentLayout,AddEditCategoryActivity.this);
                    return;
                }else if(categoryDescriptionString.isEmpty()){
                    CommonUtils.hideKeyboard(AddEditCategoryActivity.this);
                    CommonUtils.showSnackbarMessage("Please enter category description.", true, true,addEditCategoryParentLayout,AddEditCategoryActivity.this);
                    return;
                }else {
                    if (categoryID == -1) {
                        Category categoryPOJO = new Category();
                        categoryPOJO.setCategoryName(categoryNameString);
                        categoryPOJO.setCategoryDescription(categoryDescriptionString);
                        catId = Integer.parseInt(String.valueOf(categoryListDBHelper.addCategory(categoryPOJO)));
                        categoryPOJO.setCategoryId((int) catId);

                    } else {
                        Category categoryPOJO = new Category();
                        categoryPOJO.setCategoryName(categoryNameString);
                        categoryPOJO.setCategoryDescription(categoryDescriptionString);
                        categoryPOJO.setCategoryId(categoryID);
                        categoryListDBHelper.updateCategory(categoryPOJO);

                    }
                }

                Bundle data = new Bundle();
                if(categoryID == -1) {
                    data.putInt("categoryId",catId);
                    data.putString("categoryName", categoryNameString);
                    data.putString("categoryDescription", categoryDescriptionString);
                    Intent intent = new Intent();
                    intent.putExtra("categoryDetails", data);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }else {
                    Intent intent = new Intent();
                    data.putString("categoryName", categoryNameString);
                    data.putString("categoryDescription", categoryDescriptionString);
                    data.putInt("categoryID", categoryID);
                    intent.putExtra("categoryDetails", data);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }

            }
        });
    }
}
