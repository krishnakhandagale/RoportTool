package com.electivechaos.claimsadjuster.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bumptech.glide.Glide;
import com.electivechaos.claimsadjuster.Constants;
import com.electivechaos.claimsadjuster.PermissionUtilities;
import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.pojo.Image;
import com.electivechaos.claimsadjuster.pojo.ImageDetailsPOJO;
import com.electivechaos.claimsadjuster.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Set;

import static com.electivechaos.claimsadjuster.ui.ImagePickerActivity.options;

public class SettingsActivity extends AppCompatActivity {

    private View parentLayoutForMessages;
    private  static final int SELECT_FILE_IMAGE_LOGO_REQUEST_CODE= 401;

    private ImageView companyNameLogo, companyNameLogoRemove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        parentLayoutForMessages = findViewById(R.id.parentLayoutForMessages);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");

        RadioGroup radioQuality = findViewById(R.id.radioQuality);
        final RadioButton radioLow, radioMedium, radioHigh;

        radioLow = findViewById(R.id.radioLow);
        radioHigh = findViewById(R.id.radioHigh);
        radioMedium = findViewById(R.id.radioMedium);

        RadioGroup radioGoogleMap = findViewById(R.id.radioMap);
        final RadioButton radioMapRoad, radioMapSatellite, radioMapNone ;

        radioMapRoad = findViewById(R.id.radioRoadMap);
        radioMapSatellite = findViewById(R.id.radioSatelliteMap);
        radioMapNone = findViewById(R.id.radioMapNone);

        final EditText reportByEditText = findViewById(R.id.reportBy);
        final ImageButton editReportByBtn = findViewById(R.id.editBtn);
        final ImageButton doneReportByBtn = findViewById(R.id.doneBtn);

        final EditText reportTitleEditText = findViewById(R.id.reportTitle);
        final ImageButton editReportTitleBtn = findViewById(R.id.editReportTitle);
        final ImageButton doneReportTitleBtn = findViewById(R.id.doneReportTitle);

        final EditText reportDescriptionEditText = findViewById(R.id.reportDescription);
        final ImageButton editReportDescriptionBtn = findViewById(R.id.editReportDescription);
        final ImageButton doneReportDescriptionBtn = findViewById(R.id.doneReportDescription);

        final EditText companyNameEditText = findViewById(R.id.companyName);
        final ImageButton editCompanyNameBtn = findViewById(R.id.editCompanyName);
        final ImageButton doneCompanyNameBtn = findViewById(R.id.doneCompanyName);


        final Button uploadButton = findViewById(R.id.uploadButton);
        companyNameLogo = findViewById(R.id.companyNameLogo);
        companyNameLogoRemove = findViewById(R.id.companyNameLogoRemove);

        if(!CommonUtils.getReportByField(this).isEmpty()){
            reportByEditText.setText(CommonUtils.getReportByField(this));
        }

        if(!CommonUtils.getReportTitle(this).isEmpty()){
            reportTitleEditText.setText(CommonUtils.getReportTitle(this));
        }

        if(!CommonUtils.getReportDescription(this).isEmpty()){
            reportDescriptionEditText.setText(CommonUtils.getReportDescription(this));
        }

        if(!CommonUtils.getCompanyName(this).isEmpty()){
            companyNameEditText.setText(CommonUtils.getCompanyName(this));
        }

        if(!CommonUtils.getImageLogoUrl(this).isEmpty()){
            Glide.with(this)
                    .load("file://" +CommonUtils.getImageLogoUrl(this))
                    .thumbnail(0.1f)
                    .apply(options)
                    .into(companyNameLogo);
            companyNameLogoRemove.setVisibility(View.VISIBLE);
        }else {
            companyNameLogoRemove.setVisibility(View.INVISIBLE);
        }

        if(CommonUtils.getReportQuality(SettingsActivity.this).equals(Constants.REPORT_QUALITY_LOW)){
            radioLow.setChecked(true);
        }else if(CommonUtils.getReportQuality(SettingsActivity.this).equals(Constants.REPORT_QUALITY_HIGH)){
            radioHigh.setChecked(true);
        }else {
            radioMedium.setChecked(true);
        }

        radioQuality.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(radioLow.isChecked()){
                    CommonUtils.setReportQuality(Constants.REPORT_QUALITY_LOW,SettingsActivity.this);
                }else if(radioHigh.isChecked()){
                    CommonUtils.setReportQuality(Constants.REPORT_QUALITY_HIGH,SettingsActivity.this);
                }else {
                    CommonUtils.setReportQuality(Constants.REPORT_QUALITY_MEDIUM,SettingsActivity.this);
                }
            }
        });

        if(CommonUtils.getGoogleMap(SettingsActivity.this).equals(Constants.MAP_TYPE_ID_ROADMAP)){
            radioMapRoad.setChecked(true);
        }else if(CommonUtils.getGoogleMap(SettingsActivity.this).equals(Constants.MAP_TYPE_ID_SATELLITE)){
            radioMapSatellite.setChecked(true);
        }else {
            radioMapNone.setChecked(true);
        }

        radioGoogleMap.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(radioMapNone.isChecked()){
                    CommonUtils.setGoogleMap("none",SettingsActivity.this);
                }else if(radioMapSatellite.isChecked()) {
                    CommonUtils.setGoogleMap(Constants.MAP_TYPE_ID_SATELLITE,SettingsActivity.this);
                }else {
                    CommonUtils.setGoogleMap(Constants.MAP_TYPE_ID_ROADMAP,SettingsActivity.this);
                }
            }
        });


        reportByEditText.setEnabled(false);

        editReportByBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportByEditText.setEnabled(true);
                editReportByBtn.setVisibility(View.GONE);
                doneReportByBtn.setVisibility(View.VISIBLE);
            }
        });

        doneReportByBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String reportBy = reportByEditText.getText().toString();
                if(!reportBy.isEmpty()) {
                    reportByEditText.setEnabled(false);
                    doneReportByBtn.setVisibility(View.GONE);
                    editReportByBtn.setVisibility(View.VISIBLE);

                    CommonUtils.setReportByField(reportBy, SettingsActivity.this);
                }else {
                    CommonUtils.showSnackbarMessage(getString(R.string.please_enter_report_by), true, true,parentLayoutForMessages, SettingsActivity.this);
                }
            }
        });


        reportTitleEditText.setEnabled(false);

        editReportTitleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportTitleEditText.setEnabled(true);
                editReportTitleBtn.setVisibility(View.GONE);
                doneReportTitleBtn.setVisibility(View.VISIBLE);
            }
        });

        doneReportTitleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String reportTitle = reportTitleEditText.getText().toString();
                if(!reportTitle.isEmpty()) {
                    reportTitleEditText.setEnabled(false);
                    doneReportTitleBtn.setVisibility(View.GONE);
                    editReportTitleBtn.setVisibility(View.VISIBLE);

                    CommonUtils.setReportTitle(reportTitle, SettingsActivity.this);
                }else {
                    CommonUtils.showSnackbarMessage(getString(R.string.please_enter_title), true, true,parentLayoutForMessages, SettingsActivity.this);
                }
            }
        });


        reportDescriptionEditText.setEnabled(false);

        editReportDescriptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportDescriptionEditText.setEnabled(true);
                editReportDescriptionBtn.setVisibility(View.GONE);
                doneReportDescriptionBtn.setVisibility(View.VISIBLE);
            }
        });

        doneReportDescriptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String reportDescription = reportDescriptionEditText.getText().toString();
                if(!reportDescription.isEmpty()) {
                    reportDescriptionEditText.setEnabled(false);
                    doneReportDescriptionBtn.setVisibility(View.GONE);
                    editReportDescriptionBtn.setVisibility(View.VISIBLE);

                    CommonUtils.setReportDescription(reportDescription, SettingsActivity.this);
                }else {
                    CommonUtils.showSnackbarMessage(getString(R.string.please_enter_report_description), true, true,parentLayoutForMessages, SettingsActivity.this);
                }
            }
        });


        companyNameEditText.setEnabled(false);

        editCompanyNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                companyNameEditText.setEnabled(true);
                editCompanyNameBtn.setVisibility(View.GONE);
                doneCompanyNameBtn.setVisibility(View.VISIBLE);
            }
        });

        doneCompanyNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String companyName = companyNameEditText.getText().toString();
                if(!companyName.isEmpty()) {
                    companyNameEditText.setEnabled(false);
                    doneCompanyNameBtn.setVisibility(View.GONE);
                    editCompanyNameBtn.setVisibility(View.VISIBLE);

                    CommonUtils.setCompanyName(companyName, SettingsActivity.this);
                }else {
                    CommonUtils.showSnackbarMessage(getString(R.string.please_enter_company_name), true, true,parentLayoutForMessages, SettingsActivity.this);
                }
            }
        });



        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result = PermissionUtilities.checkPermissionImageUpload(SettingsActivity.this, SettingsActivity.this, PermissionUtilities.MY_APP_TAKE_RIGHT_PHOTO_PERMISSIONS_IMAGE_LOGO);
                if (result){
                     galleryIntent(SELECT_FILE_IMAGE_LOGO_REQUEST_CODE);
                }

            }
        });


        companyNameLogoRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                companyNameLogo.setImageDrawable(null);
                companyNameLogoRemove.setVisibility(View.INVISIBLE);
                CommonUtils.setImageLogoUrl("",SettingsActivity.this);
            }
        });




    }

    private void galleryIntent(int requestCode) {
        Intent intent = new Intent(SettingsActivity.this, ImagePickerActivity.class);
        intent.putExtra("number_of_images_allowed",1 );
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, requestCode);

    }

    public void onSelectImagesFromGallery(Intent data, final int requestId) {
        if (data != null) {
            onSelectFromGalleryResult(data, requestId);
        } else {
            Snackbar snackbar = Snackbar
                    .make(parentLayoutForMessages, "Something went wrong.Please try again.", Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("RETRY", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    galleryIntent(requestId);
                    v.setVisibility(View.GONE);
                }
            });
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.color_error));
            snackbar.show();
        }
    }


    private void onSelectFromGalleryResult(Intent data, int requestId) {
        ArrayList<com.electivechaos.claimsadjuster.pojo.Image> selectedImages = data.getParcelableArrayListExtra("ImageUrls");
        Glide.with(this)
                    .load("file://" +selectedImages.get(0).getPath())
                    .thumbnail(0.1f)
                    .apply(options)
                    .into(companyNameLogo);

        CommonUtils.setImageLogoUrl(selectedImages.get(0).getPath(),this);
        companyNameLogoRemove.setVisibility(View.VISIBLE);
        }



        @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if(requestCode == SELECT_FILE_IMAGE_LOGO_REQUEST_CODE ){
                this.onSelectImagesFromGallery(data,requestCode);
            }
        }
    }

}
