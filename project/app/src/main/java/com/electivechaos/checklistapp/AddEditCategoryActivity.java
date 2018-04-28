package com.electivechaos.checklistapp;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.electivechaos.checklistapp.maintabs.CategoryListFragment;

import java.util.ArrayList;

/**
 * Created by barkhasikka on 25/04/18.
 */

public class AddEditCategoryActivity extends AppCompatActivity{
    private  String intentCategoryTitle = null;
    private  String intentCategoryDescription = null;
    private  int categoryID = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.add_edit_category);

        if(getIntent().getExtras()!= null){
            Bundle dataFromActivity = getIntent().getBundleExtra("data");
            intentCategoryTitle = dataFromActivity.get("title").toString();
            intentCategoryDescription = dataFromActivity.get("description").toString();
            categoryID = Integer.parseInt(dataFromActivity.get("id").toString());

            Button updateCategoryButton = findViewById(R.id.updateCategory);
            updateCategoryButton.setText("Update Category");

            EditText categoryName = (EditText) findViewById(R.id.editTextCategoryName);
            categoryName.setText(intentCategoryTitle.toString());

            EditText categoryDescription = (EditText) findViewById(R.id.editTextCategoryDescription);
            categoryDescription.setText(intentCategoryDescription.toString());
        }

        Button updateCategoryButton = findViewById(R.id.updateCategory);
        updateCategoryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText categoryName = (EditText) findViewById(R.id.editTextCategoryName);
                String categoryNameString = categoryName.getText().toString();

                EditText categoryDescription = (EditText) findViewById(R.id.editTextCategoryDescription);
                String categoryDescriptionString = categoryDescription.getText().toString();
                Bundle data = new Bundle();//create bundle instance
                if(categoryID == 0) {
                    Log.d("------------->>", "onClick: THIS IS ADD CATEGORY EVENT");

                    data.putString("categoryName", categoryNameString);//put string to pass with a key value
                    data.putString("categoryDesc", categoryDescriptionString);//put string to pass with a key value
                    Intent intent = new Intent();
                    intent.putExtra("data", data);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }else {
                    Log.d("------------->>", "onClick: THIS IS EDIT EDIT EDIT CATEGORY EVENT");
                    Intent intent = new Intent();
                    data.putString("categoryName", categoryNameString);//put string to pass with a key value
                    data.putString("categoryDesc", categoryDescriptionString);//put string to pass with a key value
                    data.putInt("categoryId", categoryID);//put string to pass with a key value
                    intent.putExtra("data", data);
                    setResult(2, intent);
                    finish();
                }



            }
        });
    }
}
