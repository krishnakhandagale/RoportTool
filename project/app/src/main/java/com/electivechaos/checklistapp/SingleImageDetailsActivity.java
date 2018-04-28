package com.electivechaos.checklistapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.electivechaos.checklistapp.pojo.Category;
import com.electivechaos.checklistapp.pojo.ImageDetailsPOJO;
import com.electivechaos.checklistapp.adapters.CustomCategoryPopUpAdapter;
import com.electivechaos.checklistapp.database.ReportsListDBHelper;


import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by krishna on 11/10/17.
 */

public class SingleImageDetailsActivity extends BaseActivity {
    private ImageDetailsPOJO imageDetails;
    private int position;
    private  int selectedCategoryPosition = -1;
    private boolean isEdit = false;
    ArrayList<Category> options;
    protected int getCategoryIndex(ArrayList<Category>options, String cat){

        for(int i=0; i<options.size();i++){
            if(options.get(i).getCategoryName().equals(cat)){
                return  i;
            }
        }
        return  -1;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_image_details_layout);
        ReportsListDBHelper dbHelper = new ReportsListDBHelper(SingleImageDetailsActivity.this);
        ImageView imgView = findViewById(R.id.imageView);
        final EditText title = findViewById(R.id.clicked_image_title);
        final EditText description = findViewById(R.id.clicked_image_description);
        final TextView category=findViewById(R.id.category_selection);


        imageDetails = (ImageDetailsPOJO) getIntent().getExtras().getSerializable("image_details");
        isEdit = getIntent().getExtras().getBoolean("isEdit", false);
        position = getIntent().getExtras().getInt("position", -1);
        options = dbHelper.getCategoryList();
        Collections.sort(options, new CustomCategoryComparator());

        if(imageDetails != null && isEdit == true ){
            title.setText(imageDetails.getTitle());
            description.setText(imageDetails.getDescription());
            if(imageDetails.getCategory() != null && !imageDetails.getCategory().isEmpty()){
                selectedCategoryPosition =  getCategoryIndex(options,imageDetails.getCategory());
                category.setText(imageDetails.getCategory());
            }
        }




        if(savedInstanceState != null ){
            selectedCategoryPosition = savedInstanceState.getInt("selectedCategoryPosition", -1);
            if(selectedCategoryPosition != -1){
                category.setText(options.get(selectedCategoryPosition).getCategoryName());

            }
        }

        if(category.getText().toString().equals("Select Category")){
            description.setEnabled(false);
        }

        final CustomCategoryPopUpAdapter adapter = new CustomCategoryPopUpAdapter(SingleImageDetailsActivity.this,options);

        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder ad = new AlertDialog.Builder(SingleImageDetailsActivity.this);
                ad.setCancelable(true);
                ad.setTitle("Select Category");

                ad.setSingleChoiceItems(adapter, selectedCategoryPosition,  new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        selectedCategoryPosition =  position;
                        category.setText(options.get(position).getCategoryName());
                        description.setEnabled(true);
                        dialogInterface.dismiss();

                    }
                });

                ad.show();

            }
        });



        ImageButton imageButton = findViewById(R.id.submitImageDetails);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String cat = category.getText().toString();
                if(cat.equals("Select Category".trim())){
                    cat = "";
                }
                ImageDetailsPOJO shareImageDetails = new ImageDetailsPOJO();
                shareImageDetails.setTitle(title.getText().toString());
                shareImageDetails.setDescription(description.getText().toString());
                shareImageDetails.setImageUrl(imageDetails.getImageUrl());
                shareImageDetails.setCategory(cat);
                Intent intent = new Intent();
                intent.putExtra("image_entered_details", shareImageDetails);
                intent.putExtra("isEdit", isEdit);
                intent.putExtra("position",position);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        Glide.with(this).load("file://"+imageDetails.getImageUrl()).into(imgView);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selectedCategoryPosition", selectedCategoryPosition);
    }
}
