package com.electivechaos.checklistapp.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.electivechaos.checklistapp.R;
import com.electivechaos.checklistapp.fragments.AddEditReportSelectedImagesFragment;
import com.electivechaos.checklistapp.pojo.Category;
import com.electivechaos.checklistapp.pojo.Label;
import com.electivechaos.checklistapp.adapters.CustomCategoryPopUpAdapter;
import com.electivechaos.checklistapp.database.CategoryListDBHelper;

import java.util.ArrayList;

public class AddEditLabelActivity extends AppCompatActivity {
    private ArrayList<Category> categories = null;
    static CategoryListDBHelper mCategoryList;
    private int selectedCategoryPosition = -1;
    int selectedCategoryID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_label);
        final TextView categoryTextView = findViewById(R.id.category_selection);
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
                        selectedCategoryID = categories.get(pos).getCategoryId();
                        dialogInterface.dismiss();
                    }
                });

                ad.show();

            }

        });

        Button addInspectionView = findViewById(R.id.addLabel);
        addInspectionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Label label = new Label();
                label.setCategoryID(selectedCategoryID);
                final TextView labelName = findViewById(R.id.name);
                final TextView labelDescription = findViewById(R.id.description);
                label.setName(labelName.toString());
                label.setDescription(labelDescription.toString());
                mCategoryList.addLabel(label);

                android.support.v4.app.FragmentManager transactionManager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_frame, new AddEditReportSelectedImagesFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }


}
