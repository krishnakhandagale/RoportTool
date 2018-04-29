package com.electivechaos.checklistapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.electivechaos.checklistapp.R;
import com.electivechaos.checklistapp.utils.CommonUtils;

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
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_category);
        categoryName = findViewById(R.id.editTextCategoryName);
        categoryDescription = findViewById(R.id.editTextCategoryDescription);
        addEditCategoryParentLayout = findViewById(R.id.addEditCategoryParentLayout);
        if(getIntent().getExtras()!= null){
            Bundle dataFromActivity = getIntent().getBundleExtra(
                    "data");
            intentCategoryTitle = dataFromActivity.get("title").toString();
            intentCategoryDescription = dataFromActivity.get("description").toString();
            categoryID = Integer.parseInt(dataFromActivity.get("id").toString());

            Button updateCategoryButton = findViewById(R.id.updateCategory);
            updateCategoryButton.setText(R.string.update_category);


            categoryName.setText(intentCategoryTitle.toString());

            categoryDescription.setText(intentCategoryDescription.toString());
        }

        Button updateCategoryButton = findViewById(R.id.updateCategory);
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
                }

                Bundle data = new Bundle();//create bundle instance
                if(categoryID == -1) {
                    data.putString("categoryName", categoryNameString);
                    data.putString("categoryDesc", categoryDescriptionString);
                    Intent intent = new Intent();
                    intent.putExtra("data", data);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }else {
                    Intent intent = new Intent();
                    data.putString("categoryName", categoryNameString);
                    data.putString("categoryDesc", categoryDescriptionString);
                    data.putInt("categoryId", categoryID);
                    intent.putExtra("data", data);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }



            }
        });
    }
}
