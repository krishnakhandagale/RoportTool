package com.electivechaos.checklistapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.electivechaos.checklistapp.Pojo.Category;
import com.electivechaos.checklistapp.adapters.CustomCategoryPopUpAdapter;
import com.electivechaos.checklistapp.database.CategoryListDBHelper;

import java.util.ArrayList;

public class AddEditLabelActivity extends AppCompatActivity {
    private ArrayList<Category> categories = null;
    static CategoryListDBHelper mCategoryList;
    private int selectedCategoryPosition = -1;
    String imgCategory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_label);
        final TextView categoryTextView = findViewById(R.id.category_selection);
        if(savedInstanceState != null){
            selectedCategoryPosition = savedInstanceState.getInt("selectedCategoryPosition",-1);
            if(selectedCategoryPosition > -1){
                imgCategory = categories.get(selectedCategoryPosition).getCategoryName();
            }
        }
//fetch from DB
        mCategoryList = new CategoryListDBHelper(this);
        categories = mCategoryList.getCategoryList();
        final CustomCategoryPopUpAdapter adapter = new CustomCategoryPopUpAdapter(this, categories);
        categoryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder ad = new AlertDialog.Builder(AddEditLabelActivity.this);
                ad.setCancelable(true);
                ad.setTitle("Select Category");

                ad.setSingleChoiceItems(adapter, selectedCategoryPosition,  new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int pos) {
                        selectedCategoryPosition =  pos;
                        categoryTextView.setText(categories.get(pos).getCategoryName());
                        dialogInterface.dismiss();
                    }
                });

                ad.show();

            }

        });
    }


}
