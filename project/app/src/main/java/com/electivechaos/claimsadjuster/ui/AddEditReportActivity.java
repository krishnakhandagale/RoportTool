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
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;

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
import com.electivechaos.claimsadjuster.fragments.ClaimDetailsFragment;
import com.electivechaos.claimsadjuster.fragments.PerilListMenuFragment;
import com.electivechaos.claimsadjuster.fragments.PointOfOriginFragment;
import com.electivechaos.claimsadjuster.fragments.PropertyDetailsFragment;
import com.electivechaos.claimsadjuster.fragments.StarterPhotosFragment;
import com.electivechaos.claimsadjuster.interfaces.AddEditLabelInterface;
import com.electivechaos.claimsadjuster.interfaces.AsyncTaskStatusCallback;
import com.electivechaos.claimsadjuster.interfaces.BackButtonClickListener;
import com.electivechaos.claimsadjuster.interfaces.ClaimDetailsDataInterface;
import com.electivechaos.claimsadjuster.interfaces.LossLocationDataInterface;
import com.electivechaos.claimsadjuster.interfaces.NextButtonClickListener;
import com.electivechaos.claimsadjuster.interfaces.OnGenerateReportClickListener;
import com.electivechaos.claimsadjuster.interfaces.OnPerilSelectionListener;
import com.electivechaos.claimsadjuster.interfaces.OnPropertyDetailsClickListener;
import com.electivechaos.claimsadjuster.interfaces.OnSaveReportClickListener;
import com.electivechaos.claimsadjuster.interfaces.OnSetImageFileUriListener;
import com.electivechaos.claimsadjuster.interfaces.SelectedImagesDataInterface;
import com.electivechaos.claimsadjuster.listeners.OnMediaScannerListener;
import com.electivechaos.claimsadjuster.listeners.OnStarterFragmentDataChangeListener;
import com.electivechaos.claimsadjuster.pojo.Category;
import com.electivechaos.claimsadjuster.pojo.ImageDetailsPOJO;
import com.electivechaos.claimsadjuster.pojo.Label;
import com.electivechaos.claimsadjuster.pojo.ParentMenuItem;
import com.electivechaos.claimsadjuster.pojo.PerilPOJO;
import com.electivechaos.claimsadjuster.pojo.PropertyDetailsPOJO;
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
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class AddEditReportActivity extends AppCompatActivity implements DrawerMenuListAdapter.OnLabelAddClickListener, AddEditLabelInterface, ClaimDetailsDataInterface, LossLocationDataInterface,SelectedImagesDataInterface,NextButtonClickListener,BackButtonClickListener,OnSaveReportClickListener, OnGenerateReportClickListener, OnPropertyDetailsClickListener,OnPerilSelectionListener, OnSetImageFileUriListener,OnStarterFragmentDataChangeListener, Observer {

    private DrawerLayout mDrawerLayout;
    private DrawerMenuListAdapter drawerMenuListAdapter;

    private HashMap<String, List<Label>> childMenuItems = new HashMap<>();
    private ArrayList<ParentMenuItem> parentMenuItems;

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

    private  ReportPOJO modifiedReportPojo;
    private static final int SHOWPREFERENCEACTIVITY = 486;
    private static final int ADD_CATEGORY_REQUEST = 10 ;

    private static final int SELECT_FILE = 1;
    private  static final int ADD_IMAGE_DETAILS = 2;
    private static final int SET_CLICKED_IMAGE_DETAILS = 3 ;
    private static final int SET_CLICKED_CAPTURED_DETAILS = 4 ;


    private  static final int REQUEST_CAMERA = 0;
    private  static final int IMAGE_ONE_REQUEST = 100;
    private  static final int IMAGE_TWO_REQUEST = 200;
    private  static final int IMAGE_THREE_REQUEST = 300;
    private  static final int IMAGE_FOUR_REQUEST = 400;

    private  static final int IMAGE_ONE_REQUEST_STARTER = 500;
    private  static final int IMAGE_TWO_REQUEST_STARTER = 600;
    private  static final int IMAGE_THREE_REQUEST_STARTER = 700;
    private  static final int IMAGE_FOUR_REQUEST_STARTER = 800;
    private  static final int HOUSE_NUMBER_REQUEST_STARTER = 900;


    private  static final int SELECT_FILE_IMAGE_ONE_STARTER = 201;
    private  static final int SELECT_FILE_IMAGE_TWO_STARTER = 202;
    private  static final int SELECT_FILE_IMAGE_THREE_STARTER = 203;
    private  static final int SELECT_FILE_IMAGE_FOUR_STARTER = 204;
    private  static final int SELECT_FILE_IMAGE_HOUSE_NUMBER_STARTER = 205;



    private  static final int SELECT_FILE_IMAGE_ONE_OVERVIEW = 301;
    private  static final int SELECT_FILE_IMAGE_TWO_OVERVIEW = 302;
    private  static final int SELECT_FILE_IMAGE_THREE_OVERVIEW = 303;
    private  static final int SELECT_FILE_IMAGE_FOUR_OVERVIEW = 304;


    private static final String CLAIM_DETAILS_FRAGMENT_TAG = "claim_details_fragment" ;
    private static final String PROPERTY_FRAGMENT_TAG = "property_details_fragment" ;
    private static final String PERIL_FRAGMENT_TAG = "peril_fragment" ;
    private static final String POINT_OF_ORIGIN_FRAGMENT_TAG = "point_of_origin_fragment" ;
    private static final String LABEL_FRAGMENT_TAG = "label_fragment" ;

    private String fileUri;

    private  ExpandableListView mExpandableListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_edit_report_activity_layout);

        mExpandableListView = findViewById(R.id.slider_menu);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        activityActionBar = getSupportActionBar();

        activityActionBar.setDisplayHomeAsUpEnabled(true);
        activityActionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        reportPOJO = new ReportPOJO();
        reportPOJO.addObserver(this);

        parentLayoutForMessages = findViewById(R.id.parentLayoutForMessages);
        progressBarLayout = findViewById(R.id.progressBarLayout);
        categoryListDBHelper = CategoryListDBHelper.getInstance(this);
        if(savedInstanceState != null && savedInstanceState.getParcelable("reportPojo") !=null){
            fileUri = savedInstanceState.getString("fileUri");
            reportPOJO = savedInstanceState.getParcelable("reportPojo");
            reportPOJO.addObserver(this);
            setDataToExpandableList();
        }else{
            if(getIntent().getExtras() != null){
                reportPOJO = categoryListDBHelper.getReportItem(getIntent().getExtras().getString("reportId"));
                reportPOJO.addObserver(this);
                setDataToExpandableList();
            }else{

                reportPOJO =new ReportPOJO();
                reportPOJO.addObserver(this);
                setDataToExpandableList();

                Date currentDate = new Date();
                reportPOJO.setId(String.valueOf(currentDate.getTime()));

                DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
                String reportSavedDate = dateFormat.format(currentDate.getTime());
                setCreatedDate(reportSavedDate);

                new DatabaseSaveReportTask(AddEditReportActivity.this, progressBarLayout, reportPOJO, false, categoryListDBHelper, new AsyncTaskStatusCallback() {
                    @Override
                    public void onPostExecute(Object object, String type) {
                        setDataToExpandableList();
                        Label label = categoryListDBHelper.getLabelFromCategoryDetails("Risk Overview");
                        label.setReportId(reportPOJO.getId());
                        String id = "";
                        try {
                            if(!validateLabelName(reportPOJO.getLabelArrayList(),label.getName())){
                                Toast.makeText(AddEditReportActivity.this, R.string.label_validation_msg, Toast.LENGTH_SHORT).show();
                                return;
                            }

                            id = new DatabaseTaskHelper(AddEditReportActivity.this, label).execute().get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }

                        label.setId(id);
                        onDefaultLabelAdded(label);
                    }

                    @Override
                    public void onPreExecute() {

                    }
                    @Override
                    public void onProgress(int progress) {

                    }
                }).execute();


            }

        }




        if(savedInstanceState != null && savedInstanceState.getInt("selectedFragmentPosition") != -1 ){

            selectedFragmentPosition = savedInstanceState.getInt("selectedFragmentPosition");

            if(selectedFragmentPosition == 0){
                putClaimDetailsFragment();
            }else if(selectedFragmentPosition == 1){
                putPropertyDetails();
            }else if(selectedFragmentPosition == 2){
                putPerilDetails();
            }else if(selectedFragmentPosition == 3){
                putPointOfOriginFragment();
            }else if(selectedFragmentPosition  > 3){
                if(selectedFragmentPosition == 4){
                    putDefaultFragment();
                }else {
                    putLabelFragment();
                }
            }
        }else{
            putClaimDetailsFragment();
        }


        mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                if (parentMenuItems.get(groupPosition).getTitle().equals("Claim Details")) {

                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    putClaimDetailsFragment();
                    actionBarEditBtn.setVisible(false);


                } else if (parentMenuItems.get(groupPosition).getTitle().equals("Property Details")) {

                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    putPropertyDetails();
                    actionBarEditBtn.setVisible(false);

                }
                else if (parentMenuItems.get(groupPosition).getTitle().equals("Peril")) {

                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    putPerilDetails();
                    actionBarEditBtn.setVisible(false);

                } else if (parentMenuItems.get(groupPosition).getTitle().equals("Point Of Origin")) {

                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    putPointOfOriginFragment();
                    actionBarEditBtn.setVisible(false);
                }
                return false;
            }
        });


        mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                mDrawerLayout.closeDrawer(Gravity.LEFT);
                selectedFragmentPosition = childPosition + 4;
                if(selectedFragmentPosition == 4){
                    putDefaultFragment();
                }else{
                    putLabelFragment();
                }

                return false;
            }
        });


    }

    private boolean validateLabelName(ArrayList<Label> labelArrayList,String label) {
        boolean isValid = true;
        for(int i=0 ; i<labelArrayList.size();i++){
            if(label.equalsIgnoreCase(labelArrayList.get(i).getName())){
                isValid = false;
                break;
            }
        }
        return isValid;
    }

    private void setDataToExpandableList() {
        parentMenuItems = new ArrayList<>();

        parentMenuItems.add(new ParentMenuItem("Claim Details",false));
        parentMenuItems.add(new ParentMenuItem("Property Details",false));
        parentMenuItems.add(new ParentMenuItem("Peril",false));
        parentMenuItems.add(new ParentMenuItem("Point Of Origin",false));
        parentMenuItems.add(new ParentMenuItem("Inspection",false));


        List<Label> inspectionChildMenu = (List<Label>) reportPOJO.getLabelArrayList().clone();
        childMenuItems.put("Inspection", inspectionChildMenu);

        if (parentMenuItems != null && parentMenuItems.size() > 0) {
            drawerMenuListAdapter = new DrawerMenuListAdapter(this, parentMenuItems, childMenuItems);
            mExpandableListView.setAdapter(drawerMenuListAdapter);
            mExpandableListView.setIndicatorBounds(0, 20);
        }

    }

    private void putClaimDetailsFragment() {
        FragmentManager transactionManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();

        ClaimDetailsFragment claimDetailsFragment = new ClaimDetailsFragment();
        Bundle claimDetailsData = new Bundle();
        claimDetailsData.putString("reportTitle", reportPOJO.getReportTitle());
        claimDetailsData.putString("reportDescription", reportPOJO.getReportDescription());
        claimDetailsData.putString("claimNumber", reportPOJO.getClaimNumber());
        claimDetailsData.putString("reportBy", reportPOJO.getReportBy());
        claimDetailsData.putString("clientName", reportPOJO.getInsuredName());
        claimDetailsData.putString("createdDate", reportPOJO.getCreatedDate());
        claimDetailsData.putString("locationLat", reportPOJO.getLocationLat());
        claimDetailsData.putString("locationLong", reportPOJO.getLocationLong());
        claimDetailsData.putString("addressLine", reportPOJO.getAddressLine());

        claimDetailsFragment.setArguments(claimDetailsData);
        fragmentTransaction.replace(R.id.content_frame, claimDetailsFragment,CLAIM_DETAILS_FRAGMENT_TAG);
        fragmentTransaction.commit();


        selectedFragmentPosition = 0;
        activityActionBar.setTitle("Claim Details");
    }

    private void putPropertyDetails(){
        FragmentManager transactionManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();

        PropertyDetailsFragment propertyDetailsFragment=new PropertyDetailsFragment();
        Bundle propertyDetailsData = new Bundle();

        propertyDetailsData.putParcelable("propertyDetails",reportPOJO.getPropertyDetailsPOJO());
        propertyDetailsFragment.setArguments(propertyDetailsData);

        fragmentTransaction.replace(R.id.content_frame, propertyDetailsFragment,PROPERTY_FRAGMENT_TAG);
        fragmentTransaction.commit();

        selectedFragmentPosition = 1;
        activityActionBar.setTitle("Property Details");
    }
    private void putLabelFragment(){
        ArrayList<Label> labelArrayList =  reportPOJO.getLabelArrayList();
        if( labelArrayList!= null && labelArrayList.size() > selectedFragmentPosition - 4 && labelArrayList.get(selectedFragmentPosition - 4) != null){
            FragmentManager transactionManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
            AddEditReportSelectedImagesFragment addEditReportSelectedImagesFragment = AddEditReportSelectedImagesFragment.initFragment(labelArrayList.get(selectedFragmentPosition - 4).getSelectedImages(),labelArrayList.get(selectedFragmentPosition - 4).getSelectedElevationImages(), selectedFragmentPosition - 4,labelArrayList.get(selectedFragmentPosition - 4), fileUri, labelArrayList.get(selectedFragmentPosition - 4).getCoverageType());
            fragmentTransaction.replace(R.id.content_frame, addEditReportSelectedImagesFragment,LABEL_FRAGMENT_TAG);
            fragmentTransaction.commit();

            activityActionBar.setTitle(labelArrayList.get(selectedFragmentPosition - 4).getName());
            if(actionBarEditBtn != null){
                actionBarEditBtn.setVisible(true);
            }
            toolbar.setTag(labelArrayList.get(selectedFragmentPosition - 4));
        }else{
            mDrawerLayout.closeDrawer(Gravity.LEFT);
            putClaimDetailsFragment();
            if(actionBarEditBtn != null){
                actionBarEditBtn.setVisible(false);
            }
        }
    }

    private void putDefaultFragment(){
        ArrayList<Label> labelArrayList =  reportPOJO.getLabelArrayList();
        if( labelArrayList!= null && labelArrayList.size() > selectedFragmentPosition - 4 && labelArrayList.get(selectedFragmentPosition - 4) != null){
            FragmentManager transactionManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
            StarterPhotosFragment starterPhotosFragment =  new StarterPhotosFragment();
            Bundle bundle = new Bundle();

            bundle.putString("fileUri",fileUri);
            bundle.putInt("position",selectedFragmentPosition - 4);
            bundle.putString("houseNumber",labelArrayList.get(selectedFragmentPosition - 4).getHouseNumber());
            bundle.putSerializable("selectedElevationImagesList",labelArrayList.get(selectedFragmentPosition -4).getSelectedElevationImages());
            starterPhotosFragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.content_frame, starterPhotosFragment,LABEL_FRAGMENT_TAG);
            fragmentTransaction.commit();

            activityActionBar.setTitle(labelArrayList.get(selectedFragmentPosition - 4).getName());
            if(actionBarEditBtn != null){
                actionBarEditBtn.setVisible(false);
            }
            toolbar.setTag(labelArrayList.get(selectedFragmentPosition - 4));
        }else{
            mDrawerLayout.closeDrawer(Gravity.LEFT);
            putClaimDetailsFragment();
            if(actionBarEditBtn != null){
                actionBarEditBtn.setVisible(false);
            }
        }
    }


    private void putPerilDetails(){
        FragmentManager transactionManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();

        PerilListMenuFragment perilListMenuFragment=new PerilListMenuFragment();
        Bundle perilDetailsData = new Bundle();

        perilDetailsData.putParcelable("perilDetails",reportPOJO.getPerilPOJO());
        perilListMenuFragment.setArguments(perilDetailsData);

        fragmentTransaction.replace(R.id.content_frame,perilListMenuFragment,PERIL_FRAGMENT_TAG);
        fragmentTransaction.commit();

        selectedFragmentPosition = 2;
        activityActionBar.setTitle("Peril");
    }

    private void putPointOfOriginFragment(){
        FragmentManager transactionManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();

        fragmentTransaction.replace(R.id.content_frame,new PointOfOriginFragment(),POINT_OF_ORIGIN_FRAGMENT_TAG);
        fragmentTransaction.commit();

        selectedFragmentPosition = 3;
        activityActionBar.setTitle("Point Of Origin");
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
                        if(!validateLabelName(reportPOJO.getLabelArrayList(),editLabel.getText().toString())){
                            Toast.makeText(AddEditReportActivity.this, R.string.label_validation_msg, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        label.setName(editLabel.getText().toString());
                        activityActionBar.setTitle(editLabel.getText().toString());
                        drawerMenuListAdapter.notifyDataSetChanged();

                        categoryListDBHelper.updateLabel(label);

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


        outState.putInt("selectedFragmentPosition",selectedFragmentPosition);
        outState.putParcelable("reportPojo", reportPOJO);
        outState.putString("fileUri",fileUri);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(AddEditReportActivity.this);
        builder.setTitle(R.string.go_back_msg_title)
                .setMessage(R.string.back_from_report_act_msg)
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
        ad.setPositiveButton("Add New", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(AddEditReportActivity.this, AddEditCategoryActivity.class);
                startActivityForResult(intent, ADD_CATEGORY_REQUEST);
            }
        });
        ad.setTitle("Select Category");

        ad.setSingleChoiceItems(adapter, -1,  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int pos) {
                final Label label = new Label();
                label.setCategoryID(categories.get(pos).getCategoryId());
                label.setName(categories.get(pos).getCategoryName());
                label.setReportId(reportPOJO.getId());
                label.setDescription(categories.get(pos).getCategoryDescription());
                label.setCoverageType(categories.get(pos).getCoverageType());
                String id = "";
                try {
                    if(!validateLabelName(reportPOJO.getLabelArrayList(),label.getName())){
                        Toast.makeText(AddEditReportActivity.this, R.string.label_validation_msg, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    id = new DatabaseTaskHelper(AddEditReportActivity.this, label).execute().get();
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

    public  void  onDefaultLabelAdded(Label label){
        List<Label> labelList =  childMenuItems.get("Inspection");
        labelList.add(label);
        drawerMenuListAdapter.notifyDataSetChanged();
        reportPOJO.getLabelArrayList().add(label);
    }
    public void onLabelAdded(Label label) {

        List<Label> labelList =  childMenuItems.get("Inspection");
        labelList.add(label);
        drawerMenuListAdapter.notifyDataSetChanged();
        reportPOJO.getLabelArrayList().add(label);

        selectedFragmentPosition = (labelList.size() -1) + 4;

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        AddEditReportSelectedImagesFragment addEditReportSelectedImagesFragment = AddEditReportSelectedImagesFragment.initFragment(new ArrayList<ImageDetailsPOJO>(),new ArrayList<ImageDetailsPOJO>(),labelList.size()-1,label, fileUri,labelList.get(selectedFragmentPosition - 4).getCoverageType());
        fragmentTransaction.replace(R.id.content_frame, addEditReportSelectedImagesFragment);
        fragmentTransaction.commit();
        activityActionBar.setTitle(label.getName());
        if(actionBarEditBtn != null){
            actionBarEditBtn.setVisible(true);
        }
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

                if((selectedFragmentPosition-4) == 0){
                    putDefaultFragment();
                }else {
                    FragmentManager transactionManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
                    AddEditReportSelectedImagesFragment addEditReportSelectedImagesFragment = AddEditReportSelectedImagesFragment.initFragment(labelArrayList.get(selectedFragmentPosition - 4).getSelectedImages(), labelArrayList.get(selectedFragmentPosition - 4).getSelectedElevationImages(), selectedFragmentPosition - 4, labelArrayList.get(selectedFragmentPosition - 4), fileUri, labelArrayList.get(selectedFragmentPosition - 4).getCoverageType());
                    fragmentTransaction.replace(R.id.content_frame, addEditReportSelectedImagesFragment);
                    fragmentTransaction.commit();
                }

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
        new DBUpdateTaskOnTextChanged(AddEditReportActivity.this, progressBarLayout,reportClientName,reportPOJO.getId(),false,categoryListDBHelper,"client_name").execute();
        reportPOJO.setInsuredName(reportClientName);
    }

    @Override
    public void setReportClaimNumber(String reportClaimNumber) {
        new DBUpdateTaskOnTextChanged(AddEditReportActivity.this, progressBarLayout, reportClaimNumber,reportPOJO.getId(),false,categoryListDBHelper,"claim_number").execute();
        reportPOJO.setClaimNumber(reportClaimNumber);
    }

    @Override
    public void setCreatedDate(String createdDate) {
        reportPOJO.setCreatedDate(createdDate);
    }

    @Override
    public void setReportBy(String reportBy) {
        new DBUpdateTaskOnTextChanged(AddEditReportActivity.this, progressBarLayout, reportBy,reportPOJO.getId(),false,categoryListDBHelper,"report_by").execute();
        reportPOJO.setReportBy(reportBy);
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
        new DBUpdateTaskOnTextChanged(AddEditReportActivity.this, progressBarLayout, addressLine,reportPOJO.getId(),false,categoryListDBHelper,"address_line").execute();

    }

    @Override
    public void setMapSnapshot(final Bitmap bitmap) {

        googleMapSnapshotBitmap =  bitmap;
        boolean result = PermissionUtilities.checkPermission(AddEditReportActivity.this,null,PermissionUtilities.MY_APP_SAVE_SNAPSHOT_PERMISSIONS);

        if(result){
            File destination = new File(Environment.getExternalStorageDirectory(), reportPOJO.getId() + ".png");
            final FileOutputStream fileOutputStream;
            try {
                fileOutputStream = new FileOutputStream(destination,false);
                googleMapSnapshotBitmap.compress(Bitmap.CompressFormat.PNG, 90, fileOutputStream);
                reportPOJO.setGoogleMapSnapShotFilePath(destination.getPath());
                categoryListDBHelper.updateLocationSnapshot(destination.getPath(),reportPOJO.getId());

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            ImageHelper.grantAppPermission(this, getIntent(), Uri.fromFile(destination));


            new SingleMediaScanner(this, destination, new OnMediaScannerListener(){

                @Override
                public void onMediaScanComplete(String path, Uri uri) {
                    if(path != null){
                        ImageHelper.revokeAppPermission(AddEditReportActivity.this, uri);
                    }

                }
            });
        }

    }


    @Override
    public void setSelectedElevationImages(ArrayList<ImageDetailsPOJO> elevationImagesList,int labelPosition) {

        reportPOJO.getLabelArrayList().get(labelPosition).setSelectedElevationImages(elevationImagesList);
        new DBSelectedImagesTask(AddEditReportActivity.this, progressBarLayout, reportPOJO.getLabelArrayList().get(labelPosition),false,categoryListDBHelper,"elevation_images").execute();

    }

    @Override
    public void setSelectedImages(ArrayList<ImageDetailsPOJO> imagesList , int labelPosition) {
        ArrayList<ImageDetailsPOJO> imageDetailsList = reportPOJO.getLabelArrayList().get(labelPosition).getSelectedImages();
        imageDetailsList.addAll(imagesList);
        reportPOJO.getLabelArrayList().get(labelPosition).setSelectedImages(imageDetailsList);
    }

    @Override
    public void setSwapedSelectedImages(ArrayList<ImageDetailsPOJO> imagesList , int labelPosition) {
        reportPOJO.getLabelArrayList().get(labelPosition).setSelectedImages(imagesList);
    }
    @Override
    public void onBackButtonClick() {
        selectedFragmentPosition = selectedFragmentPosition -1;
        if(selectedFragmentPosition >=0){

            if(selectedFragmentPosition == 0) {
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                putClaimDetailsFragment();
                actionBarEditBtn.setVisible(false);

            }

            if(selectedFragmentPosition == 1) {
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                putPropertyDetails();
                actionBarEditBtn.setVisible(false);

            }
            else if(selectedFragmentPosition == 2) {
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                putPerilDetails();
                activityActionBar.setTitle("Peril");
                actionBarEditBtn.setVisible(false);

            }
            else if(selectedFragmentPosition == 3) {
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                putPointOfOriginFragment();
                activityActionBar.setTitle("Point Of Origin");
                actionBarEditBtn.setVisible(false);

            }else if(selectedFragmentPosition > 3){
                if(selectedFragmentPosition == 4){
                    putDefaultFragment();
                }else {
                    putLabelFragment();
                }
            }
        }else{
            selectedFragmentPosition = 4 + reportPOJO.getLabelArrayList().size() -1 ;
            putLabelFragment();

        }
    }

    @Override
    public void onNextButtonClick() {

        selectedFragmentPosition = selectedFragmentPosition + 1;
        if(selectedFragmentPosition == 1) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
            putPropertyDetails();
            actionBarEditBtn.setVisible(false);

        }
        else if(selectedFragmentPosition == 2) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
            putPerilDetails();
            activityActionBar.setTitle("Peril");
            actionBarEditBtn.setVisible(false);

        }
        else if(selectedFragmentPosition == 3) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
            putPointOfOriginFragment();
            activityActionBar.setTitle("Point Of Origin");
            actionBarEditBtn.setVisible(false);

        }else if(selectedFragmentPosition > 3){
            if(selectedFragmentPosition == 4){
                putDefaultFragment();
            }else {
                putLabelFragment();
            }
        }

    }


    public  boolean validateData(){
        boolean validateSquareFootage= Pattern.compile("^[1-9]\\d*(\\.\\d+)?$").matcher(reportPOJO.getPropertyDetailsPOJO().getSquareFootage().trim()).matches();
        if(reportPOJO.getReportTitle().trim().isEmpty()){
            CommonUtils.showSnackbarMessage(getString(R.string.please_enter_title), true, true,parentLayoutForMessages, AddEditReportActivity.this);
            return false;
        }else if(reportPOJO.getReportDescription().trim().isEmpty()){
            CommonUtils.showSnackbarMessage(getString(R.string.enter_description_message), true, true, parentLayoutForMessages, AddEditReportActivity.this);
            return false;
        }else if(reportPOJO.getInsuredName().trim().isEmpty()){
            CommonUtils.showSnackbarMessage(getString(R.string.enter_insurance_name_message), true, true, parentLayoutForMessages, AddEditReportActivity.this);
            return false;
        }else if(reportPOJO.getClaimNumber().trim().isEmpty()){
            CommonUtils.showSnackbarMessage(getString(R.string.enter_claim_number_message), true, true, parentLayoutForMessages, AddEditReportActivity.this);
            return false;
        }else if(reportPOJO.getAddressLine().trim().isEmpty()){
            CommonUtils.showSnackbarMessage(getString(R.string.please_add_address_message), true, true, parentLayoutForMessages, AddEditReportActivity.this);
            return false;
        }
        else if(!reportPOJO.getPropertyDetailsPOJO().getSquareFootage().trim().isEmpty()){
            if(!validateSquareFootage) {
                CommonUtils.showSnackbarMessage(getString(R.string.please_add_valid_square_footage_message), true, true, parentLayoutForMessages, AddEditReportActivity.this);
                return false;
            }
        }


        return true;
    }

    @Override
    public void onReportSave(boolean isProgressBar) {

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
            Intent i  = new Intent(AddEditReportActivity.this, CustomisationActivity.class);
            i.putExtra("reportDetails", reportPOJO);
            AddEditReportActivity.this.startActivityForResult(i,SHOWPREFERENCEACTIVITY);
        }
    }

    public void onTwoImagesPerPage() {
        boolean result = PermissionUtilities.checkPermission(AddEditReportActivity.this,null,PermissionUtilities.MY_APP_GENERATE_REPORT_PERMISSIONS_TWO);

        if(result)
            new DBUpdateFilePath(AddEditReportActivity.this,findViewById(R.id.progressBarLayout), modifiedReportPojo, reportPOJO, true, categoryListDBHelper).execute(2);
    }

    public void onFourImagesPerPage() {
        boolean result = PermissionUtilities.checkPermission(AddEditReportActivity.this,null,PermissionUtilities.MY_APP_GENERATE_REPORT_PERMISSIONS_FOUR);
        if(result)
            new DBUpdateFilePath(AddEditReportActivity.this,findViewById(R.id.progressBarLayout), modifiedReportPojo,reportPOJO, true, categoryListDBHelper).execute(4);
    }

    @Override
    public void setPropertyDate(String propertyDate) {
        reportPOJO.getPropertyDetailsPOJO().setPropertyDate(propertyDate);
        reportPOJO.setPropertyDetailsPOJO( reportPOJO.getPropertyDetailsPOJO());
        new DBUpdateTaskOnTextChanged(AddEditReportActivity.this, progressBarLayout, propertyDate,reportPOJO.getId(),false,categoryListDBHelper,"property_date").execute();

    }

    @Override
    public void setPropertySquareFootage(String squareFootage) {
        reportPOJO.getPropertyDetailsPOJO().setSquareFootage(squareFootage);
        reportPOJO.setPropertyDetailsPOJO( reportPOJO.getPropertyDetailsPOJO());
        new DBUpdateTaskOnTextChanged(AddEditReportActivity.this, progressBarLayout, String.valueOf(squareFootage),reportPOJO.getId(),false,categoryListDBHelper,"square_footage").execute();
    }

    @Override
    public void setPropertyRoofSystem(String roofSystem) {
        reportPOJO.getPropertyDetailsPOJO().setRoofSystem(roofSystem);
        reportPOJO.setPropertyDetailsPOJO( reportPOJO.getPropertyDetailsPOJO());
        new DBUpdateTaskOnTextChanged(AddEditReportActivity.this, progressBarLayout, roofSystem, reportPOJO.getId(),false,categoryListDBHelper,"roof_system").execute();
    }

    @Override
    public void setPropertySiding(String siding) {
        reportPOJO.getPropertyDetailsPOJO().setSiding(siding);
        reportPOJO.setPropertyDetailsPOJO( reportPOJO.getPropertyDetailsPOJO());
        new DBUpdateTaskOnTextChanged(AddEditReportActivity.this, progressBarLayout, siding, reportPOJO.getId(),false,categoryListDBHelper,"siding").execute();
    }

    @Override
    public void setPropertyFoundation(String foundation) {
        reportPOJO.getPropertyDetailsPOJO().setFoundation(foundation);
        reportPOJO.setPropertyDetailsPOJO( reportPOJO.getPropertyDetailsPOJO());
        new DBUpdateTaskOnTextChanged(AddEditReportActivity.this, progressBarLayout, foundation, reportPOJO.getId(),false,categoryListDBHelper,"foundation").execute();

    }

    @Override
    public void setPropertyBuildingType(String buildingType) {
        reportPOJO.getPropertyDetailsPOJO().setBuildingType(buildingType);
        reportPOJO.setPropertyDetailsPOJO( reportPOJO.getPropertyDetailsPOJO());
        new DBUpdateTaskOnTextChanged(AddEditReportActivity.this, progressBarLayout, buildingType, reportPOJO.getId(),false,categoryListDBHelper,"building_type").execute();
    }

    @Override
    public void setPeril(PerilPOJO perilPOJO) {
        reportPOJO.setPerilPOJO(perilPOJO);
        new DBUpdateTaskOnTextChanged(AddEditReportActivity.this, progressBarLayout, perilPOJO.getName(),reportPOJO.getId(),false,categoryListDBHelper,"peril").execute();

    }

    @Override
    public void onSetImageFileUri(String fileUriOfImageCapture) {
        fileUri = fileUriOfImageCapture;
    }

    @Override
    public void onHouseNumberChange(String houseNumber, int labelPosition) {
        reportPOJO.getLabelArrayList().get(labelPosition).setHouseNumber(houseNumber);
        categoryListDBHelper.updateLabel(reportPOJO.getLabelArrayList().get(labelPosition));
    }

    @Override
    public void update(Observable o, Object arg) {
        if(o instanceof ReportPOJO){
            enableDisableCheckList(reportPOJO);
        }
    }

    private void enableDisableCheckList(ReportPOJO reportPOJO) {

        if(!TextUtils.isEmpty(reportPOJO.getReportTitle())
                && !TextUtils.isEmpty(reportPOJO.getReportDescription())
                &&  !TextUtils.isEmpty(reportPOJO.getClaimNumber())
                && !TextUtils.isEmpty(reportPOJO.getInsuredName())
                && !TextUtils.isEmpty(reportPOJO.getAddressLine())){
            parentMenuItems.get(0).setChecked(true);
            drawerMenuListAdapter.notifyDataSetChanged();
        }else{
            parentMenuItems.get(0).setChecked(false);
            drawerMenuListAdapter.notifyDataSetChanged();
        }

        PropertyDetailsPOJO propertyDetailsPOJO = reportPOJO.getPropertyDetailsPOJO();
        if(!TextUtils.isEmpty(propertyDetailsPOJO.getPropertyDate())
                && !TextUtils.equals(propertyDetailsPOJO.getPropertyDate(),"--Select Inspection Date--")
                && !TextUtils.isEmpty(propertyDetailsPOJO.getSquareFootage())
                && !TextUtils.isEmpty(propertyDetailsPOJO.getRoofSystem())
                && !TextUtils.equals(propertyDetailsPOJO.getSquareFootage(),"--Select Roof System--")
                && !TextUtils.isEmpty(propertyDetailsPOJO.getSiding())
                && !TextUtils.equals(propertyDetailsPOJO.getSiding(),"--Select Siding--")
                && !TextUtils.isEmpty(propertyDetailsPOJO.getFoundation())
                && !TextUtils.equals(propertyDetailsPOJO.getSquareFootage(),"--Select Foundation--")
                && !TextUtils.isEmpty(propertyDetailsPOJO.getBuildingType())
                && !TextUtils.equals(propertyDetailsPOJO.getBuildingType(),"--Select Building Type--")){
            parentMenuItems.get(1).setChecked(true);
            drawerMenuListAdapter.notifyDataSetChanged();
        }else{
            parentMenuItems.get(1).setChecked(false);
            drawerMenuListAdapter.notifyDataSetChanged();
        }
        if(!TextUtils.isEmpty(reportPOJO.getPerilPOJO().getName())){
            parentMenuItems.get(2).setChecked(true);
            drawerMenuListAdapter.notifyDataSetChanged();
        }else{
            parentMenuItems.get(2).setChecked(false);
            drawerMenuListAdapter.notifyDataSetChanged();
        }


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
        actionBarEditBtn = menu.findItem(R.id.edit);

        if(selectedFragmentPosition > 4){
            actionBarEditBtn.setVisible(true);
        }else{
            actionBarEditBtn.setVisible(false);
        }
        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        reportPOJO.deleteObserver(this);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) switch (requestCode) {
            case SHOWPREFERENCEACTIVITY:

                ReportPOJO reportPOJOModified = data.getParcelableExtra("modified_report");
                int noOfImages = data.getIntExtra("per_page_images",2);
                modifiedReportPojo =  reportPOJOModified;
                if(noOfImages == 2){
                    onTwoImagesPerPage();
                }else{
                    onFourImagesPerPage();
                }
                break;
            case ADD_CATEGORY_REQUEST:
                Bundle dataFromActivity = data.getExtras().getBundle("categoryDetails");
                if(dataFromActivity!= null) {
                    int categoryId = dataFromActivity.getInt("categoryId");
                    String categoryName = dataFromActivity.get("categoryName").toString();
                    String categoryDescription = dataFromActivity.get("categoryDescription").toString();
                    String categoryCoverageType= dataFromActivity.get("categoryCoverageType").toString();

                    final Label label = new Label();
                    label.setCategoryID(categoryId);
                    label.setName(categoryName);
                    label.setDescription(categoryDescription);
                    label.setReportId(reportPOJO.getId());
                    label.setCoverageType(categoryCoverageType);

                    String labelId = "";
                    try {

                        if(!validateLabelName(reportPOJO.getLabelArrayList(),label.getName())){
                            Toast.makeText(AddEditReportActivity.this, R.string.label_validation_msg, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        labelId = new DatabaseTaskHelper(AddEditReportActivity.this, label).execute().get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                    label.setId(labelId);

                    onLabelAdded(label);
                }
                break;

            case SET_CLICKED_IMAGE_DETAILS:
                ImageDetailsPOJO imageDetailsPOJO  = (ImageDetailsPOJO) data.getExtras().get("selected_images");
                int labelPosition = data.getExtras().getInt("labelPosition");
                ArrayList<ImageDetailsPOJO> imageDetailsPOJOArrayList = (ArrayList<ImageDetailsPOJO>) reportPOJO.getLabelArrayList().get(labelPosition).getSelectedImages().clone();
                ImageDetailsPOJO selectedImage = null;
                if (data.getExtras().getBoolean("isEdit")) {
                    int position = data.getExtras().getInt("position");
                    selectedImage= imageDetailsPOJOArrayList.get(position);
                    selectedImage.setTitle(imageDetailsPOJO.getTitle());
                    selectedImage.setDescription(imageDetailsPOJO.getDescription());
                    selectedImage.setImageUrl(imageDetailsPOJO.getImageUrl());
                    selectedImage.setIsDamage(imageDetailsPOJO.isDamage());
                    selectedImage.setOverview(imageDetailsPOJO.isOverview());
                    selectedImage.setPointOfOrigin(imageDetailsPOJO.isPointOfOrigin());
                    selectedImage.setCoverageTye(imageDetailsPOJO.getCoverageTye());

                }
                if(selectedImage != null) {
                    categoryListDBHelper.updateImageDetails(selectedImage);
                }
                try {
                    FragmentManager fm = getSupportFragmentManager();
                    if(fm.getFragments() != null && fm.getFragments().size() >0){
                        for(int i=0;i<fm.getFragments().size();i++){

                            if(fm.getFragments().get(i) instanceof  AddEditReportSelectedImagesFragment){
                                AddEditReportSelectedImagesFragment fragment = (AddEditReportSelectedImagesFragment) fm.getFragments().get(i);
                                fragment.setDataAndAdapter(imageDetailsPOJOArrayList);
                            }
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case  SET_CLICKED_CAPTURED_DETAILS:
                ImageDetailsPOJO imageDetails = (ImageDetailsPOJO) data.getExtras().get("selected_images");
                categoryListDBHelper.editImageDetails(imageDetails);

                ArrayList<ImageDetailsPOJO> imageDetailsPOJOSList = new ArrayList<>();
                imageDetailsPOJOSList.add(imageDetails);

                try {
                    FragmentManager fm = getSupportFragmentManager();
                    if(fm.getFragments() != null && fm.getFragments().size() >0){
                        for(int i=0;i<fm.getFragments().size();i++){

                            if(fm.getFragments().get(i) instanceof  AddEditReportSelectedImagesFragment){
                                AddEditReportSelectedImagesFragment fragment = (AddEditReportSelectedImagesFragment) fm.getFragments().get(i);
                                fragment.setDataAndAdapter(imageDetailsPOJOSList);
                            }
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case  ADD_IMAGE_DETAILS:
                ArrayList<ImageDetailsPOJO> imagesInformation = (ArrayList<ImageDetailsPOJO>) data.getExtras().getSerializable("selected_images");

                ArrayList<ImageDetailsPOJO> selectedImageList = imagesInformation;

                if(selectedImageList!=null) {
                    for (int i = 0; i < selectedImageList.size(); i++) {
                        categoryListDBHelper.editImageDetails(selectedImageList.get(i));
                    }
                }
                if (selectedImageList == null) {
                    selectedImageList = new ArrayList<>();
                }
                try {
                    FragmentManager fm = getSupportFragmentManager();
                    if(fm.getFragments() != null && fm.getFragments().size() >0){
                        for(int i=0;i<fm.getFragments().size();i++){

                            if(fm.getFragments().get(i) instanceof  AddEditReportSelectedImagesFragment){
                                AddEditReportSelectedImagesFragment fragment = (AddEditReportSelectedImagesFragment) fm.getFragments().get(i);
                                fragment.setDataAndAdapter(selectedImageList);
                            }
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case SELECT_FILE :

                try {
                    FragmentManager fm = getSupportFragmentManager();
                    if(fm.getFragments() != null && fm.getFragments().size() >0){
                        for(int i=0;i<fm.getFragments().size();i++){

                            if(fm.getFragments().get(i) instanceof  AddEditReportSelectedImagesFragment){
                                AddEditReportSelectedImagesFragment fragment = (AddEditReportSelectedImagesFragment) fm.getFragments().get(i);
                                fragment.onSelectImagesFromGallery(data, SELECT_FILE);
                            }
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }


                break;
            case  REQUEST_CAMERA:
                try {
                    FragmentManager fm = getSupportFragmentManager();
                    if(fm.getFragments() != null && fm.getFragments().size() >0){
                        for(int i=0;i<fm.getFragments().size();i++){

                            if(fm.getFragments().get(i) instanceof  AddEditReportSelectedImagesFragment){
                                AddEditReportSelectedImagesFragment fragment = (AddEditReportSelectedImagesFragment) fm.getFragments().get(i);
                                fragment.onImageCapturedResult(data);
                            }
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case IMAGE_ONE_REQUEST:

                try {
                    FragmentManager fm = getSupportFragmentManager();
                    if(fm.getFragments() != null && fm.getFragments().size() >0){
                        for(int i=0;i<fm.getFragments().size();i++){

                            if(fm.getFragments().get(i) instanceof  AddEditReportSelectedImagesFragment){
                                AddEditReportSelectedImagesFragment fragment = (AddEditReportSelectedImagesFragment) fm.getFragments().get(i);
                                fragment.onElevationImageOneCapture(data,requestCode);
                            }
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case IMAGE_TWO_REQUEST:

                try {
                    FragmentManager fm = getSupportFragmentManager();
                    if(fm.getFragments() != null && fm.getFragments().size() >0){
                        for(int i=0;i<fm.getFragments().size();i++){

                            if(fm.getFragments().get(i) instanceof  AddEditReportSelectedImagesFragment){
                                AddEditReportSelectedImagesFragment fragment = (AddEditReportSelectedImagesFragment) fm.getFragments().get(i);
                                fragment.onElevationImageTwoCapture(data,requestCode);
                            }
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case IMAGE_THREE_REQUEST:

                try {
                    FragmentManager fm = getSupportFragmentManager();
                    if(fm.getFragments() != null && fm.getFragments().size() >0){
                        for(int i=0;i<fm.getFragments().size();i++){

                            if(fm.getFragments().get(i) instanceof  AddEditReportSelectedImagesFragment){
                                AddEditReportSelectedImagesFragment fragment = (AddEditReportSelectedImagesFragment) fm.getFragments().get(i);
                                fragment. onElevationImageThreeCapture(data,requestCode);
                            }
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }


                break;

            case IMAGE_FOUR_REQUEST:

                try {
                    FragmentManager fm = getSupportFragmentManager();
                    if(fm.getFragments() != null && fm.getFragments().size() >0){
                        for(int i=0;i<fm.getFragments().size();i++){

                            if(fm.getFragments().get(i) instanceof  AddEditReportSelectedImagesFragment){
                                AddEditReportSelectedImagesFragment fragment = (AddEditReportSelectedImagesFragment) fm.getFragments().get(i);
                                fragment. onElevationImageFourCapture(data,requestCode);
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case IMAGE_ONE_REQUEST_STARTER:

                try {
                    FragmentManager fm = getSupportFragmentManager();
                    if(fm.getFragments() != null && fm.getFragments().size() >0){
                        for(int i=0;i<fm.getFragments().size();i++){

                            if(fm.getFragments().get(i) instanceof  StarterPhotosFragment){
                                StarterPhotosFragment fragment = (StarterPhotosFragment) fm.getFragments().get(i);
                                fragment.onElevationImageOneCapture(data,requestCode);
                            }
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case IMAGE_TWO_REQUEST_STARTER:

                try {
                    FragmentManager fm = getSupportFragmentManager();
                    if(fm.getFragments() != null && fm.getFragments().size() >0){
                        for(int i=0;i<fm.getFragments().size();i++){

                            if(fm.getFragments().get(i) instanceof  StarterPhotosFragment){
                                StarterPhotosFragment fragment = (StarterPhotosFragment) fm.getFragments().get(i);
                                fragment.onElevationImageTwoCapture(data,requestCode);
                            }
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case IMAGE_THREE_REQUEST_STARTER:

                try {
                    FragmentManager fm = getSupportFragmentManager();
                    if(fm.getFragments() != null && fm.getFragments().size() >0){
                        for(int i=0;i<fm.getFragments().size();i++){

                            if(fm.getFragments().get(i) instanceof  StarterPhotosFragment){
                                StarterPhotosFragment fragment = (StarterPhotosFragment) fm.getFragments().get(i);
                                fragment.onElevationImageThreeCapture(data,requestCode);
                            }
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }


                break;

            case IMAGE_FOUR_REQUEST_STARTER:

                try {
                    FragmentManager fm = getSupportFragmentManager();
                    if(fm.getFragments() != null && fm.getFragments().size() >0){
                        for(int i=0;i<fm.getFragments().size();i++){

                            if(fm.getFragments().get(i) instanceof  StarterPhotosFragment){
                                StarterPhotosFragment fragment = (StarterPhotosFragment) fm.getFragments().get(i);
                                fragment.onElevationImageFourCapture(data,requestCode);
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case HOUSE_NUMBER_REQUEST_STARTER :

                try {
                    FragmentManager fm = getSupportFragmentManager();
                    if(fm.getFragments() != null && fm.getFragments().size() >0){
                        for(int i=0;i<fm.getFragments().size();i++){

                            if(fm.getFragments().get(i) instanceof  StarterPhotosFragment){
                                StarterPhotosFragment fragment = (StarterPhotosFragment) fm.getFragments().get(i);
                                fragment.onHouseNumberImageCapture(data,requestCode);
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case SELECT_FILE_IMAGE_ONE_STARTER :
            case SELECT_FILE_IMAGE_TWO_STARTER :
            case SELECT_FILE_IMAGE_THREE_STARTER :
            case SELECT_FILE_IMAGE_FOUR_STARTER :
            case SELECT_FILE_IMAGE_HOUSE_NUMBER_STARTER :

                try {
                    FragmentManager fm = getSupportFragmentManager();
                    if(fm.getFragments() != null && fm.getFragments().size() >0){
                        for(int i=0;i<fm.getFragments().size();i++){

                            if(fm.getFragments().get(i) instanceof  StarterPhotosFragment){
                                StarterPhotosFragment fragment = (StarterPhotosFragment) fm.getFragments().get(i);
                                fragment.onSelectImagesFromGallery(data,requestCode);
                            }
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case SELECT_FILE_IMAGE_ONE_OVERVIEW :
            case SELECT_FILE_IMAGE_TWO_OVERVIEW :
            case SELECT_FILE_IMAGE_THREE_OVERVIEW :
            case SELECT_FILE_IMAGE_FOUR_OVERVIEW :

                try {
                    FragmentManager fm = getSupportFragmentManager();
                    if(fm.getFragments() != null && fm.getFragments().size() >0){
                        for(int i=0;i<fm.getFragments().size();i++){

                            if(fm.getFragments().get(i) instanceof  AddEditReportSelectedImagesFragment){
                                AddEditReportSelectedImagesFragment fragment = (AddEditReportSelectedImagesFragment) fm.getFragments().get(i);
                                fragment.onSelectImagesFromGallery(data,requestCode);
                            }
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }



}
