package com.electivechaos.claimsadjuster.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;

import com.electivechaos.claimsadjuster.ImageHelper;
import com.electivechaos.claimsadjuster.PermissionUtilities;
import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.SingleMediaScanner;
import com.electivechaos.claimsadjuster.adapters.CustomCategoryPopUpAdapter;
import com.electivechaos.claimsadjuster.adapters.DrawerMenuListAdapter;
import com.electivechaos.claimsadjuster.asynctasks.DBSelectedImagesTask;
import com.electivechaos.claimsadjuster.asynctasks.DBUpdateFilePath;
import com.electivechaos.claimsadjuster.asynctasks.DBUpdateTaskOnTextChanged;
import com.electivechaos.claimsadjuster.asynctasks.DatabaseSaveReportTask;
import com.electivechaos.claimsadjuster.database.CategoryListDBHelper;
import com.electivechaos.claimsadjuster.fragments.AddEditReportSelectedImagesFragment;
import com.electivechaos.claimsadjuster.fragments.PerilFragment;
import com.electivechaos.claimsadjuster.fragments.ClaimDetailsFragment;
import com.electivechaos.claimsadjuster.fragments.PointOfOriginFragment;
import com.electivechaos.claimsadjuster.fragments.PropertyDetailsFragment;
import com.electivechaos.claimsadjuster.interfaces.AddEditLabelInterface;
import com.electivechaos.claimsadjuster.interfaces.ClaimDetailsDataInterface;
import com.electivechaos.claimsadjuster.interfaces.LossLocationDataInterface;
import com.electivechaos.claimsadjuster.interfaces.NextButtonClickListener;
import com.electivechaos.claimsadjuster.interfaces.OnGenerateReportClickListener;
import com.electivechaos.claimsadjuster.interfaces.OnSaveReportClickListener;
import com.electivechaos.claimsadjuster.interfaces.PreferenceDialogCallback;
import com.electivechaos.claimsadjuster.interfaces.SelectedImagesDataInterface;
import com.electivechaos.claimsadjuster.listeners.OnMediaScannerListener;
import com.electivechaos.claimsadjuster.pojo.Category;
import com.electivechaos.claimsadjuster.pojo.ImageDetailsPOJO;
import com.electivechaos.claimsadjuster.pojo.Label;
import com.electivechaos.claimsadjuster.pojo.ReportPOJO;
import com.electivechaos.claimsadjuster.utils.CommonUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AddEditReportActivity extends AppCompatActivity implements DrawerMenuListAdapter.OnLabelAddClickListener, AddEditLabelInterface, ClaimDetailsDataInterface, LossLocationDataInterface,SelectedImagesDataInterface,NextButtonClickListener,OnSaveReportClickListener, OnGenerateReportClickListener, PreferenceDialogCallback {
    private DrawerLayout mDrawerLayout;
    private DrawerMenuListAdapter drawerMenuListAdapter;
    private String tabName;

    private HashMap<String, List<Label>> childMenuItems = new HashMap<>();
    private ArrayList<String> parentMenuItems;

    private int selectedFragmentPosition = 0;

    private ArrayList<Category> categories = null;
    static CategoryListDBHelper categoryListDBHelper;

    private ReportPOJO reportPOJO ;
    private View progressBarLayout;

    private  Toolbar toolbar;
    private MenuItem actionBarEditBtn;
    private ActionBar activityActionBar;
    private View parentLayoutForMessages;

    private  Bitmap googleMapSnapshotBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_edit_report_activity_layout);

        parentLayoutForMessages = findViewById(R.id.parentLayoutForMessages);
        progressBarLayout = findViewById(R.id.progressBarLayout);
        categoryListDBHelper = CategoryListDBHelper.getInstance(this);

        if(getIntent().getExtras() != null){
            reportPOJO = categoryListDBHelper.getReportItem(getIntent().getExtras().getString("reportId"));

        }else{
            reportPOJO = new ReportPOJO();
            Date currentDate = new Date();
            reportPOJO.setId(String.valueOf(currentDate.getTime()));

            DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
            String reportSavedDate = dateFormat.format(currentDate.getTime());
            setCreatedDate(reportSavedDate);
            onReportSave(false);
        }

        selectedFragmentPosition = 0;

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        activityActionBar = getSupportActionBar();
        activityActionBar.setTitle("Claim Details");


        activityActionBar.setDisplayHomeAsUpEnabled(true);
        activityActionBar.setHomeAsUpIndicator(R.drawable.ic_menu);


        ExpandableListView mExpandableListView = findViewById(R.id.slider_menu);


        putClaimDetailsFragment();
        parentMenuItems = new ArrayList<>();

        parentMenuItems.add("Claim Details");
        parentMenuItems.add("Property Details");
        parentMenuItems.add("Peril");
        parentMenuItems.add("Point Of Origin");
        parentMenuItems.add("Inspection");


        List<Label> inspectionChildMenu = (List<Label>) reportPOJO.getLabelArrayList().clone();
        childMenuItems.put("Inspection", inspectionChildMenu);


        mDrawerLayout = findViewById(R.id.drawer_layout);

        if (parentMenuItems != null && parentMenuItems.size() > 0) {
            drawerMenuListAdapter = new DrawerMenuListAdapter(this, parentMenuItems, childMenuItems);
            mExpandableListView.setAdapter(drawerMenuListAdapter);
            mExpandableListView.setIndicatorBounds(0, 20);
        }
        mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                if (parentMenuItems.get(groupPosition).equals("Claim Details")) {

                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    putClaimDetailsFragment();
                    actionBarEditBtn.setVisible(false);


                } else if (parentMenuItems.get(groupPosition).equals("Property Details")) {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    FragmentManager transactionManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
                    fragmentTransaction.replace(R.id.content_frame, new PropertyDetailsFragment());
                    fragmentTransaction.commit();
                    tabName = "PropertyDetailsFragment";

                    selectedFragmentPosition = 1;
                    activityActionBar.setTitle("Property Details");
                    actionBarEditBtn.setVisible(false);

                }
                else if (parentMenuItems.get(groupPosition).equals("Peril")) {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    FragmentManager transactionManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
                    fragmentTransaction.replace(R.id.content_frame, new PerilFragment());
                    fragmentTransaction.commit();
                    tabName = "PerilFragment";

                    selectedFragmentPosition = 2;
                    activityActionBar.setTitle("Peril");
                    actionBarEditBtn.setVisible(false);

                } else if (parentMenuItems.get(groupPosition).equals("Point Of Origin")) {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    FragmentManager transactionManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
                    fragmentTransaction.replace(R.id.content_frame, new PointOfOriginFragment());
                    fragmentTransaction.commit();
                    tabName = "PointOfOriginFragment";

                    selectedFragmentPosition = 3;

                    activityActionBar.setTitle("Point Of Origin");
                    actionBarEditBtn.setVisible(false);
                }
                return false;
            }
        });


        mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                mDrawerLayout.closeDrawer(Gravity.LEFT);
                FragmentManager transactionManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
                AddEditReportSelectedImagesFragment addEditReportSelectedImagesFragment = AddEditReportSelectedImagesFragment.initFragment(reportPOJO.getLabelArrayList().get(childPosition).getSelectedImages(), reportPOJO.getLabelArrayList().get(childPosition).getSelectedElevationImages(), childPosition);
                fragmentTransaction.replace(R.id.content_frame, addEditReportSelectedImagesFragment);
                fragmentTransaction.commit();
                selectedFragmentPosition = childPosition + 4;
                tabName = "AddEditReportSelectedImagesFragment";
                activityActionBar.setTitle(reportPOJO.getLabelArrayList().get(childPosition).getName());
                actionBarEditBtn.setVisible(true);
                toolbar.setTag(reportPOJO.getLabelArrayList().get(childPosition));
                return false;
            }
        });


    }

    private void putClaimDetailsFragment() {
        FragmentManager transactionManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();

        ClaimDetailsFragment claimDetailsFragment = new ClaimDetailsFragment();
        Bundle claimDetailsData = new Bundle();

        claimDetailsData.putString("reportTitle", reportPOJO.getReportTitle());
        claimDetailsData.putString("reportDescription", reportPOJO.getReportDescription());
        claimDetailsData.putString("claimNumber", reportPOJO.getClaimNumber());
        claimDetailsData.putString("clientName", reportPOJO.getClientName());
        claimDetailsData.putString("createdDate", reportPOJO.getCreatedDate());
        claimDetailsData.putString("locationLat", reportPOJO.getLocationLat());
        claimDetailsData.putString("locationLong", reportPOJO.getLocationLong());
        claimDetailsData.putString("addressLine", reportPOJO.getAddressLine());

        claimDetailsFragment.setArguments(claimDetailsData);
        fragmentTransaction.replace(R.id.content_frame, claimDetailsFragment);
        fragmentTransaction.commit();
        tabName = "ClaimDetailsFragment";


        selectedFragmentPosition = 0;
        activityActionBar.setTitle("Claim Details");
    }


    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                CommonUtils.hideKeyboard(AddEditReportActivity.this);
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.edit:

                View view = getLayoutInflater().inflate(R.layout.edit_label_actionbar_layout, null);
                final EditText editLabel=view.findViewById(R.id.editText);

                editLabel.setText(activityActionBar.getTitle());
                editLabel.setFocusableInTouchMode(true);
                editLabel.setFocusable(true);
                editLabel.requestFocus();
                final Label label=(Label)toolbar.getTag();

                view.findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activityActionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP);
                        activityActionBar.setDisplayShowTitleEnabled(true);
                        activityActionBar.setDisplayShowCustomEnabled(false);
                        item.setVisible(true);
                    }
                });

                view.findViewById(R.id.doneBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //database call for edit label and notify data set changed for expandable list view.
                        label.setName(editLabel.getText().toString());
                        categoryListDBHelper.updateLabel(label);
                        activityActionBar.setTitle(editLabel.getText().toString());
                        drawerMenuListAdapter.notifyDataSetChanged();

                        activityActionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP);
                        activityActionBar.setDisplayShowTitleEnabled(true);
                        activityActionBar.setDisplayShowCustomEnabled(false);
                        item.setVisible(true);
                        CommonUtils.hideKeyboard(AddEditReportActivity.this);
                    }
                });

                activityActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
                activityActionBar.setDisplayShowCustomEnabled(true);



                Toolbar.LayoutParams layout = new Toolbar.LayoutParams(Toolbar.LayoutParams.FILL_PARENT, Toolbar.LayoutParams.FILL_PARENT);
                activityActionBar.setCustomView(view, layout);
                item.setVisible(false);
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putString("tabName",tabName);


    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(AddEditReportActivity.this);
        builder.setTitle("Go Back")
                .setMessage("Pressing back will take you to previous screen, are you sure wanna go back ?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                      AddEditReportActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
        Button negativeButton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setTextColor(ContextCompat.getColor(AddEditReportActivity.this,R.color.colorPrimaryDark));
        Button positiveButton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setTextColor(ContextCompat.getColor(AddEditReportActivity.this,R.color.colorPrimaryDark));
    }

    @Override
    public void onLabelAddClick() {

        mDrawerLayout.closeDrawer(Gravity.LEFT);

        try {
            categories = new DatabaseTaskCategoryList(AddEditReportActivity.this).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        final CustomCategoryPopUpAdapter adapter = new CustomCategoryPopUpAdapter(this, categories);
                final android.app.AlertDialog.Builder ad = new android.app.AlertDialog.Builder(AddEditReportActivity.this);
                ad.setCancelable(true);
                ad.setTitle("Select Category");

                ad.setSingleChoiceItems(adapter, -1,  new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialogInterface, int pos) {

                                final Label label = new Label();
                                label.setCategoryID(categories.get(pos).getCategoryId());
                                label.setName(categories.get(pos).getCategoryName());
                                label.setReportId(reportPOJO.getId());
                                String id = "";
                                try {
                                    id = new DatabaseTaskHelper(AddEditReportActivity.this,label).execute().get();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }

                                label.setId(id);

                                onLabelAdded(label);
                                dialogInterface.dismiss();
                            }
                        });

                ad.show();

    }


    public void onLabelAdded(Label label) {

        List<Label> labelList =  childMenuItems.get("Inspection");
        labelList.add(label);
        drawerMenuListAdapter.notifyDataSetChanged();
        reportPOJO.getLabelArrayList().add(label);


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        AddEditReportSelectedImagesFragment addEditReportSelectedImagesFragment = AddEditReportSelectedImagesFragment.initFragment(new ArrayList<ImageDetailsPOJO>(),new ArrayList<ImageDetailsPOJO>(),labelList.size()-1);
        fragmentTransaction.replace(R.id.content_frame, addEditReportSelectedImagesFragment);
        fragmentTransaction.commit();
        selectedFragmentPosition = (labelList.size() -1) + 4;
        activityActionBar.setTitle(label.getName());
        actionBarEditBtn.setVisible(true);
        toolbar.setTag(label);
        activityActionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP);
        activityActionBar.setDisplayShowTitleEnabled(true);
        activityActionBar.setDisplayShowCustomEnabled(false);

    }

    @Override
    public void onLabelDeleted(int position) {
        childMenuItems.get("Inspection").remove(position);
        drawerMenuListAdapter.notifyDataSetChanged();
        reportPOJO.getLabelArrayList().remove(position);

        if(reportPOJO.getLabelArrayList().size() > 0 ){
            selectedFragmentPosition = position > 0 ? position - 1 + 4 : position + 4;
            ArrayList<Label> labelArrayList =  reportPOJO.getLabelArrayList();
            if( labelArrayList!= null && labelArrayList.size() > selectedFragmentPosition - 4 && labelArrayList.get(selectedFragmentPosition - 4) != null) {
                FragmentManager transactionManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
                AddEditReportSelectedImagesFragment addEditReportSelectedImagesFragment = AddEditReportSelectedImagesFragment.initFragment(labelArrayList.get(selectedFragmentPosition - 4).getSelectedImages(), labelArrayList.get(selectedFragmentPosition - 4).getSelectedElevationImages(), selectedFragmentPosition - 4);
                fragmentTransaction.replace(R.id.content_frame, addEditReportSelectedImagesFragment);
                fragmentTransaction.commit();
                tabName = "AddEditReportSelectedImagesFragment";

                activityActionBar.setTitle(labelArrayList.get(selectedFragmentPosition - 4).getName());
                actionBarEditBtn.setVisible(true);
                toolbar.setTag(labelArrayList.get(selectedFragmentPosition - 4));
            }
        }else{
            mDrawerLayout.closeDrawer(Gravity.LEFT);
            putClaimDetailsFragment();
            actionBarEditBtn.setVisible(false);

        }


    }


    @Override
    public void setReportTitle(String reportTitle) {

        new DBUpdateTaskOnTextChanged(AddEditReportActivity.this, progressBarLayout,reportTitle,reportPOJO.getId(),false,categoryListDBHelper,"title").execute();
        reportPOJO.setReportTitle(reportTitle);
    }

    @Override
    public void setReportDescription(String reportDescription) {

        reportPOJO.setReportDescription(reportDescription);
        new DBUpdateTaskOnTextChanged(AddEditReportActivity.this, progressBarLayout,reportDescription,reportPOJO.getId(),false,categoryListDBHelper,"description").execute();
    }

    @Override
    public void setReportClientName(String reportClientName) {
        new DBUpdateTaskOnTextChanged(AddEditReportActivity.this, progressBarLayout,reportClientName,reportPOJO.getId(),false,categoryListDBHelper,"clientName").execute();
        reportPOJO.setClientName(reportClientName);
    }

    @Override
    public void setReportClaimNumber(String reportClaimNumber) {
        new DBUpdateTaskOnTextChanged(AddEditReportActivity.this, progressBarLayout, reportClaimNumber,reportPOJO.getId(),false,categoryListDBHelper,"claimNumber").execute();
        reportPOJO.setClaimNumber(reportClaimNumber);
    }

    @Override
    public void setCreatedDate(String createdDate) {
        reportPOJO.setCreatedDate(createdDate);
    }


    @Override
    public void setLocationLat(String locationLat) {
        reportPOJO.setLocationLat(locationLat);
        new DBUpdateTaskOnTextChanged(AddEditReportActivity.this, progressBarLayout, locationLat,reportPOJO.getId(),false,categoryListDBHelper,"latitude").execute();

    }

    @Override
    public void setLocationLong(String locationLong) {
        reportPOJO.setLocationLong(locationLong);
        new DBUpdateTaskOnTextChanged(AddEditReportActivity.this, progressBarLayout, locationLong,reportPOJO.getId(),false,categoryListDBHelper,"longitude").execute();
    }

    @Override
    public void setAddressLine(String addressLine) {
        reportPOJO.setAddressLine(addressLine);
        new DBUpdateTaskOnTextChanged(AddEditReportActivity.this, progressBarLayout, addressLine,reportPOJO.getId(),false,categoryListDBHelper,"addressLine").execute();

    }

    @Override
    public void setMapSnapshot(final Bitmap bitmap) {
        googleMapSnapshotBitmap =  bitmap;
        boolean result = PermissionUtilities.checkPermission(AddEditReportActivity.this,null,PermissionUtilities.MY_APP_SAVE_SNAPSHOT_PERMISSIONS);

        if(result){
            File destination = new File(Environment.getExternalStorageDirectory(), reportPOJO.getId() + ".png");
            ImageHelper.grantAppPermission(this, getIntent(), Uri.fromFile(destination));
            try {
                final FileOutputStream fileOutputStream = new FileOutputStream(destination,false);

                new SingleMediaScanner(this, destination, new OnMediaScannerListener(){

                    @Override
                    public void onMediaScanComplete(String path, Uri uri) {
                        if(path != null){
                            ImageHelper.revokeAppPermission(AddEditReportActivity.this, uri);
                            googleMapSnapshotBitmap.compress(Bitmap.CompressFormat.PNG, 90, fileOutputStream);
                            reportPOJO.setGoogleMapSnapShotFilePath(path);
                            categoryListDBHelper.updateLocationSnapshot(path,reportPOJO.getId());
                        }

                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }


    }


    @Override
    public void setSelectedElevationImages(ArrayList<ImageDetailsPOJO> elevationImagesList,int labelPosition) {

        reportPOJO.getLabelArrayList().get(labelPosition).setSelectedElevationImages(elevationImagesList);


        new DBSelectedImagesTask(AddEditReportActivity.this, progressBarLayout, reportPOJO.getLabelArrayList().get(labelPosition),false,categoryListDBHelper,"elevationImages").execute();

    }

    @Override
    public void setSelectedImages(ArrayList<ImageDetailsPOJO> imagesList , int labelPosition) {

        reportPOJO.getLabelArrayList().get(labelPosition).setSelectedImages(imagesList);


        new DBSelectedImagesTask(AddEditReportActivity.this, progressBarLayout, reportPOJO.getLabelArrayList().get(labelPosition),false,categoryListDBHelper,"selectedImages").execute();

    }

    @Override
    public void onNextButtonClick() {

        selectedFragmentPosition = selectedFragmentPosition + 1;
         if(selectedFragmentPosition == 1) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
            FragmentManager transactionManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_frame,new PropertyDetailsFragment());
            fragmentTransaction.commit();
            tabName="PropertyDetailsFragment";

            activityActionBar.setTitle("Property Details");
            actionBarEditBtn.setVisible(false);

        }
        else if(selectedFragmentPosition == 2) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
            FragmentManager transactionManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_frame,new PerilFragment());
            fragmentTransaction.commit();
            tabName="PerilFragment";

            activityActionBar.setTitle("Peril");
            actionBarEditBtn.setVisible(false);

        }
        else if(selectedFragmentPosition == 3) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
            FragmentManager transactionManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_frame,new PointOfOriginFragment());
            fragmentTransaction.commit();
            tabName="PointOfOriginFragment";

            activityActionBar.setTitle("Point Of Origin");
            actionBarEditBtn.setVisible(false);

        }else if(selectedFragmentPosition > 3){

            ArrayList<Label> labelArrayList =  reportPOJO.getLabelArrayList();
            if( labelArrayList!= null && labelArrayList.size() > selectedFragmentPosition - 4 && labelArrayList.get(selectedFragmentPosition - 4) != null){
                FragmentManager transactionManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
                AddEditReportSelectedImagesFragment addEditReportSelectedImagesFragment = AddEditReportSelectedImagesFragment.initFragment(labelArrayList.get(selectedFragmentPosition - 4).getSelectedImages(),labelArrayList.get(selectedFragmentPosition - 4).getSelectedElevationImages(), selectedFragmentPosition - 4);
                fragmentTransaction.replace(R.id.content_frame, addEditReportSelectedImagesFragment);
                fragmentTransaction.commit();
                tabName = "AddEditReportSelectedImagesFragment";

                activityActionBar.setTitle(labelArrayList.get(selectedFragmentPosition - 4).getName());
                actionBarEditBtn.setVisible(true);
                toolbar.setTag(labelArrayList.get(selectedFragmentPosition - 4));
            }else{
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                putClaimDetailsFragment();
                actionBarEditBtn.setVisible(false);
            }


        }

    }

    public  boolean validateData(){
        if(reportPOJO.getReportTitle().trim().isEmpty()){
            CommonUtils.showSnackbarMessage(getString(R.string.please_enter_title), true, true,parentLayoutForMessages, AddEditReportActivity.this);

            return false;
        }else if(reportPOJO.getReportDescription().trim().isEmpty()){
            CommonUtils.showSnackbarMessage(getString(R.string.enter_description_message), true, true, parentLayoutForMessages, AddEditReportActivity.this);
            return false;
        }else if(reportPOJO.getClientName().trim().isEmpty()){
            CommonUtils.showSnackbarMessage(getString(R.string.enter_client_name_message), true, true, parentLayoutForMessages, AddEditReportActivity.this);
            return false;
        }else if(reportPOJO.getClaimNumber().trim().isEmpty()){
            CommonUtils.showSnackbarMessage(getString(R.string.enter_claim_number_message), true, true, parentLayoutForMessages, AddEditReportActivity.this);
            return false;
        }else if(reportPOJO.getAddressLine().trim().isEmpty()){
            CommonUtils.showSnackbarMessage(getString(R.string.please_add_address_message), true, true, parentLayoutForMessages, AddEditReportActivity.this);
            return false;
        }
        return true;
    }

    @Override
    public void onReportSave(boolean isProgressBar) {

        //Is from user action then validate
        boolean isValid;
        if(isProgressBar) {
            isValid = validateData();

            if(isValid){
                new DatabaseSaveReportTask(AddEditReportActivity.this, progressBarLayout, reportPOJO,false,categoryListDBHelper).execute();
            }
        }else {
            new DatabaseSaveReportTask(AddEditReportActivity.this, progressBarLayout, reportPOJO,false,categoryListDBHelper).execute();
        }


    }

    @Override
    public void onReportGenerateClicked() {
        boolean isValid = validateData();
        if(isValid){
            showReportPreferenceDialog(this);
        }
    }

    @Override
    public void onTwoImagesPerPage() {
        boolean result = PermissionUtilities.checkPermission(AddEditReportActivity.this,null,PermissionUtilities.MY_APP_GENERATE_REPORT_PERMISSIONS_TWO);

        Intent i  = new Intent(AddEditReportActivity.this, CustomisationActivity.class);
        i.putExtra("reportDetails", reportPOJO);
        startActivity(i);
//        if(result)
//            new DBUpdateFilePath(AddEditReportActivity.this,findViewById(R.id.progressBarLayout), reportPOJO, true, categoryListDBHelper).execute(2);
    }

    @Override
    public void onFourImagesPerPage() {
        boolean result = PermissionUtilities.checkPermission(AddEditReportActivity.this,null,PermissionUtilities.MY_APP_GENERATE_REPORT_PERMISSIONS_FOUR);
//        if(result)
//            new DBUpdateFilePath(AddEditReportActivity.this,findViewById(R.id.progressBarLayout), reportPOJO, true, categoryListDBHelper).execute(4);
    }


    // Task for label addition
    class DatabaseTaskHelper extends AsyncTask <String,Void,String>{


        private Label label;
        private Context context;


        public  DatabaseTaskHelper(Context context,Label label) {
            this.context=context;
            this.label=label;

        }

        @Override
        protected void onPreExecute() {
            CommonUtils.lockOrientation((Activity) context);
            if(progressBarLayout != null){
                progressBarLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected String doInBackground(String... strings) {

            return categoryListDBHelper.addLabel(label);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(progressBarLayout != null){
                progressBarLayout.setVisibility(View.GONE);
            }
            CommonUtils.unlockOrientation((Activity)context);

        }
    }

    // Task for getting  cat list
    class DatabaseTaskCategoryList extends AsyncTask <String,Void,ArrayList<Category>>{

        private Context context;


        public  DatabaseTaskCategoryList(Context context) {
            this.context=context;

        }

        @Override
        protected void onPreExecute() {
            CommonUtils.lockOrientation((Activity) context);
            if(progressBarLayout != null){
                progressBarLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected ArrayList<Category> doInBackground(String... strings) {

          ArrayList<Category> categories= categoryListDBHelper.getCategoryList();
            return categories;
        }

        @Override
        protected void onPostExecute(ArrayList<Category> result) {
            if(progressBarLayout != null){
                progressBarLayout.setVisibility(View.GONE);
            }
            CommonUtils.unlockOrientation((Activity)context);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_action_menu, menu);
        actionBarEditBtn= menu.findItem(R.id.edit);
        actionBarEditBtn.setVisible(false);

        return true;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void showReportPreferenceDialog(final PreferenceDialogCallback callback) {
        final CharSequence[] items = {"2", "4"};
        AlertDialog.Builder builder = new AlertDialog.Builder(AddEditReportActivity.this);

        builder.setTitle("Number of images per page ?");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("2")) {
                    callback.onTwoImagesPerPage();
                } else if (items[item].equals("4")) {
                    callback.onFourImagesPerPage();
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionUtilities.MY_APP_GENERATE_REPORT_PERMISSIONS_FOUR: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onFourImagesPerPage();
                } else {
                    PermissionUtilities.checkPermission(AddEditReportActivity.this, null, PermissionUtilities.MY_APP_GENERATE_REPORT_PERMISSIONS_FOUR);
                }
                break;
            }
            case PermissionUtilities.MY_APP_GENERATE_REPORT_PERMISSIONS_TWO: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onTwoImagesPerPage();
                } else {
                    PermissionUtilities.checkPermission(AddEditReportActivity.this, null, PermissionUtilities.MY_APP_GENERATE_REPORT_PERMISSIONS_TWO);
                }
                break;
            }
            case PermissionUtilities.MY_APP_SAVE_SNAPSHOT_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setMapSnapshot(googleMapSnapshotBitmap);
                } else {
                    PermissionUtilities.checkPermission(AddEditReportActivity.this, null, PermissionUtilities.MY_APP_SAVE_SNAPSHOT_PERMISSIONS);
                }
                break;
            }
        }
    }
}
