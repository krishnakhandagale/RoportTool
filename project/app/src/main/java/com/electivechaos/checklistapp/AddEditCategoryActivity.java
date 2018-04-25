package com.electivechaos.checklistapp;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.electivechaos.checklistapp.adapters.CategoriesAdapter;
import com.electivechaos.checklistapp.maintabs.CategoryListFragment;

import java.util.ArrayList;

/**
 * Created by barkhasikka on 25/04/18.
 */

public class AddEditCategoryActivity extends Activity{
    private  String categoryTitle = null;
    private  String categoryDescription = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.add_edit_category);

        if(getIntent().getExtras()!= null){
            categoryTitle =  getIntent().getExtras().getString("title");
            categoryDescription =  getIntent().getExtras().getString("description");
        }

        Button updateCategoryButton = findViewById(R.id.updateCategory);
        updateCategoryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText categoryName = (EditText) findViewById(R.id.editTextCategoryName);
                String categoryNameString = categoryName.getText().toString();

                EditText categoryDescription = (EditText) findViewById(R.id.editTextCategoryName);
                String categoryDescriptionString = categoryDescription.getText().toString();

                Bundle data = new Bundle();//create bundle instance
                data.putString("categoryName", categoryNameString);//put string to pass with a key value
                data.putString("categoryDesc", categoryDescriptionString);//put string to pass with a key value
                Intent intent = new Intent();
                intent.putExtra("data", data);
                setResult(Activity.RESULT_OK, intent);
                finish();

            }
        });
    }




}
