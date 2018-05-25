package com.electivechaos.claimsadjuster.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.electivechaos.claimsadjuster.BaseActivity;
import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.pojo.ImageDetailsPOJO;

/**
 * Created by krishna on 11/10/17.
 */

public class SingleImageDetailsActivity extends BaseActivity {
    private ImageDetailsPOJO imageDetails;
    private int position;
    private boolean isEdit = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_image_details_layout);
        ImageView imgView = findViewById(R.id.imageView);
        final EditText title = findViewById(R.id.clickedImageTitle);
        final EditText description = findViewById(R.id.clickedImageDescription);
        final CheckedTextView isDamageTextView = findViewById(R.id.isDamageTextView);
        final CheckedTextView isOverviewTextView = findViewById(R.id.isOverviewTextView);



        imageDetails = getIntent().getExtras().getParcelable("image_details");
        isEdit = getIntent().getExtras().getBoolean("isEdit", false);
        position = getIntent().getExtras().getInt("position", -1);


        if(imageDetails != null && isEdit == true ){
            title.setText(imageDetails.getTitle());
            description.setText(imageDetails.getDescription());

            isDamageTextView.setChecked(imageDetails.isDamage());
            if(imageDetails.isDamage()) {
                isDamageTextView.setBackground(ContextCompat.getDrawable(this,R.drawable.shape_chip_drawable_active));
            }else {
                isDamageTextView.setBackground(ContextCompat.getDrawable(this,R.drawable.shape_chip_drawable_gray));
            }


            isOverviewTextView.setChecked(imageDetails.isOverview());
            if(imageDetails.isOverview()) {
                isOverviewTextView.setBackground(ContextCompat.getDrawable(this,R.drawable.shape_chip_drawable_active));
            }else {
                isOverviewTextView.setBackground(ContextCompat.getDrawable(this,R.drawable.shape_chip_drawable_gray));
            }
        }

        isDamageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isOverviewTextView.isChecked()){
                    isOverviewTextView.setChecked(false);
                    isOverviewTextView.setBackground(ContextCompat.getDrawable(SingleImageDetailsActivity.this,R.drawable.shape_chip_drawable_gray));
                    imageDetails.setOverview(false);
                }
                if(((CheckedTextView)v).isChecked()){
                    ((CheckedTextView)v).setChecked(false);
                    imageDetails.setIsDamage(false);
                    v.setBackground(ContextCompat.getDrawable(SingleImageDetailsActivity.this,R.drawable.shape_chip_drawable_gray));
                }else{
                    ((CheckedTextView)v).setChecked(true);
                    imageDetails.setIsDamage(true);
                    v.setBackground(ContextCompat.getDrawable(SingleImageDetailsActivity.this,R.drawable.shape_chip_drawable_active));
                }
            }
        });

        isOverviewTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isDamageTextView.isChecked()){
                    isDamageTextView.setChecked(false);
                    isDamageTextView.setBackground(ContextCompat.getDrawable(SingleImageDetailsActivity.this,R.drawable.shape_chip_drawable_gray));
                    imageDetails.setDamage(false);
                }
                if(((CheckedTextView)v).isChecked()){
                    ((CheckedTextView)v).setChecked(false);
                    imageDetails.setOverview(false);
                    v.setBackground(ContextCompat.getDrawable(SingleImageDetailsActivity.this,R.drawable.shape_chip_drawable_gray));
                }else{
                    ((CheckedTextView)v).setChecked(true);
                    imageDetails.setOverview(true);
                    v.setBackground(ContextCompat.getDrawable(SingleImageDetailsActivity.this,R.drawable.shape_chip_drawable_active));
                }
            }
        });


        ImageButton imageButton = findViewById(R.id.submitImageDetails);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageDetailsPOJO shareImageDetails = new ImageDetailsPOJO();
                shareImageDetails.setTitle(title.getText().toString());
                shareImageDetails.setDescription(description.getText().toString());
                shareImageDetails.setImageUrl(imageDetails.getImageUrl());
                shareImageDetails.setIsDamage(imageDetails.isDamage());
                shareImageDetails.setOverview(imageDetails.isOverview());
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

    }
}
