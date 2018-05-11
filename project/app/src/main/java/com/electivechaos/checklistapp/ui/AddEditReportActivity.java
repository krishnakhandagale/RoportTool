package com.electivechaos.checklistapp.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.electivechaos.checklistapp.R;
import com.electivechaos.checklistapp.adapters.CustomCategoryPopUpAdapter;
import com.electivechaos.checklistapp.adapters.DrawerMenuListAdapter;
import com.electivechaos.checklistapp.database.CategoryListDBHelper;
import com.electivechaos.checklistapp.fragments.AddEditReportSelectedImagesFragment;
import com.electivechaos.checklistapp.fragments.CauseOfLossFragment;
import com.electivechaos.checklistapp.fragments.ClaimDetailsFragment;
import com.electivechaos.checklistapp.fragments.PointOfOriginFragment;
import com.electivechaos.checklistapp.interfaces.AddEditLabelInterface;
import com.electivechaos.checklistapp.interfaces.ClaimDetailsDataInterface;
import com.electivechaos.checklistapp.interfaces.LossLocationDataInterface;
import com.electivechaos.checklistapp.interfaces.NextButtonClickListener;
import com.electivechaos.checklistapp.interfaces.SelectedImagesDataInterface;
import com.electivechaos.checklistapp.pojo.Category;
import com.electivechaos.checklistapp.pojo.ImageDetailsPOJO;
import com.electivechaos.checklistapp.pojo.Label;
import com.electivechaos.checklistapp.pojo.ReportPOJO;
import com.electivechaos.checklistapp.utils.CommonUtils;
import com.itextpdf.text.LargeElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AddEditReportActivity extends AppCompatActivity implements DrawerMenuListAdapter.OnLabelAddClickListener, AddEditLabelInterface, ClaimDetailsDataInterface, LossLocationDataInterface,SelectedImagesDataInterface,NextButtonClickListener {
    private DrawerLayout mDrawerLayout;
    private ExpandableListView mExpandableListView;
    private DrawerMenuListAdapter drawerMenuListAdapter;
    private String tabName;

    HashMap<String, List<Label>> childMenuItems = new HashMap<>();
    List<Label> inspectionChildMenu = new ArrayList<>();
    ArrayList<String> parentMenuItems;

    private int selectedFragmentPosition = 0;

    private ArrayList<Category> categories = null;
    static CategoryListDBHelper categoryListDBHelper;

    private ReportPOJO reportPOJO = new ReportPOJO();
    private View progressBarLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        progressBarLayout = findViewById(R.id.progressBarLayout);
        categoryListDBHelper = CategoryListDBHelper.getInstance(this);

        selectedFragmentPosition=0;

        FragmentManager transactionManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, new ClaimDetailsFragment());
        fragmentTransaction.commit();
        tabName = "ClaimDetailsFragment";

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        getSupportActionBar().setTitle("Claim Details");

        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);


        mExpandableListView = findViewById(R.id.slider_menu);

        parentMenuItems = new ArrayList<>();

        parentMenuItems.add("Claim Details");
        parentMenuItems.add("Cause Of Loss");
        parentMenuItems.add("Point Of Origin");
        parentMenuItems.add("Inspection");


        inspectionChildMenu = (List<Label>) reportPOJO.getLabelArrayList().clone();
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
                    FragmentManager transactionManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();

                    ClaimDetailsFragment claimDetailsFragment = new ClaimDetailsFragment();
                    Bundle claimDetailsData = new Bundle();
                    claimDetailsData.putString("reportTitle", reportPOJO.getReportTitle());
                    claimDetailsData.putString("reportDescription", reportPOJO.getReportDescription());
                    claimDetailsData.putString("claimNumber", reportPOJO.getClaimNumber());
                    claimDetailsData.putString("claimNumber", reportPOJO.getClaimNumber());
                    claimDetailsData.putString("clientName", reportPOJO.getClientName());

                    claimDetailsData.putString("locationLat", reportPOJO.getLocationLat());
                    claimDetailsData.putString("locationLong", reportPOJO.getLocationLong());

                    claimDetailsFragment.setArguments(claimDetailsData);
                    fragmentTransaction.replace(R.id.content_frame, claimDetailsFragment);
                    fragmentTransaction.commit();
                    tabName = "ClaimDetailsFragment";


                    selectedFragmentPosition = 0;
                    getSupportActionBar().setTitle("Claim Details");


                } else if (parentMenuItems.get(groupPosition).equals("Cause Of Loss")) {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    FragmentManager transactionManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
                    fragmentTransaction.replace(R.id.content_frame, new CauseOfLossFragment());
                    fragmentTransaction.commit();
                    tabName = "CauseOfLossFragment";

                    selectedFragmentPosition = 1;
                    getSupportActionBar().setTitle("Cause Of Loss");

                } else if (parentMenuItems.get(groupPosition).equals("Point Of Origin")) {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    FragmentManager transactionManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
                    fragmentTransaction.replace(R.id.content_frame, new PointOfOriginFragment());
                    fragmentTransaction.commit();
                    tabName = "PointOfOriginFragment";

                    selectedFragmentPosition = 2;

                    getSupportActionBar().setTitle("Point Of Origin");
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
                selectedFragmentPosition = childPosition + 3;
                tabName = "AddEditReportSelectedImagesFragment";
                getSupportActionBar().setTitle(reportPOJO.getLabelArrayList().get(childPosition).getName());
                return false;
            }
        });


    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

        }
        return super.onOptionsItemSelected(item);
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
    public void onLabelAddClick(int position) {

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
                                long id = 0;
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
        selectedFragmentPosition = labelList.size() -1;
        getSupportActionBar().setTitle(label.getName());
    }


    @Override
    public void setReportTitle(String reportTitle) {
        reportPOJO.setReportTitle(reportTitle);
    }

    @Override
    public void setReportDescription(String reportDescription) {
        reportPOJO.setReportDescription(reportDescription);
    }

    @Override
    public void setReportClientName(String reportClientName) {
        reportPOJO.setClientName(reportClientName);
    }

    @Override
    public void setReportClaimNumber(String reportClaimNumber) {
        reportPOJO.setClaimNumber(reportClaimNumber);
    }


    @Override
    public void setLocationLat(String locationLat) {
        reportPOJO.setLocationLat(locationLat);
    }

    @Override
    public void setLocationLong(String locationLong) {
        reportPOJO.setLocationLong(locationLong);
    }

    @Override
    public void setSelectedElevationImages(ArrayList<ImageDetailsPOJO> elevationImagesList,int labelPosition) {
        reportPOJO.getLabelArrayList().get(labelPosition).setSelectedElevationImages(elevationImagesList);
    }

    @Override
    public void setSelectedImages(ArrayList<ImageDetailsPOJO> imagesList , int labelPosition) {
        reportPOJO.getLabelArrayList().get(labelPosition).setSelectedImages(imagesList);

    }

    @Override
    public void onNextButtonClick() {

        selectedFragmentPosition = selectedFragmentPosition + 1;
        if(selectedFragmentPosition == 1) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
            FragmentManager transactionManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_frame,new CauseOfLossFragment());
            fragmentTransaction.commit();
            tabName="CauseOfLossFragment";

            getSupportActionBar().setTitle("Cause Of Loss");
        }
        else if(selectedFragmentPosition == 2) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
            FragmentManager transactionManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_frame,new PointOfOriginFragment());
            fragmentTransaction.commit();
            tabName="PointOfOriginFragment";

            getSupportActionBar().setTitle("Point Of Origin");

        }else if(selectedFragmentPosition > 2){

            ArrayList<Label> labelArrayList =  reportPOJO.getLabelArrayList();
            if( labelArrayList!= null && labelArrayList.size() > selectedFragmentPosition - 3 && labelArrayList.get(selectedFragmentPosition - 3) != null){
                FragmentManager transactionManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
                AddEditReportSelectedImagesFragment addEditReportSelectedImagesFragment = AddEditReportSelectedImagesFragment.initFragment(labelArrayList.get(selectedFragmentPosition - 3).getSelectedImages(),labelArrayList.get(selectedFragmentPosition - 3).getSelectedElevationImages(), selectedFragmentPosition - 3);
                fragmentTransaction.replace(R.id.content_frame, addEditReportSelectedImagesFragment);
                fragmentTransaction.commit();
                tabName = "AddEditReportSelectedImagesFragment";

                getSupportActionBar().setTitle(labelArrayList.get(selectedFragmentPosition - 3).getName());
            }else{
                selectedFragmentPosition = selectedFragmentPosition -1;
            }


        }

    }




    // Task for label addition
    class DatabaseTaskHelper extends AsyncTask <String,Void,Long>{


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
        protected Long doInBackground(String... strings) {

            long id = categoryListDBHelper.addLabel(label);
            return id;
        }

        @Override
        protected void onPostExecute(Long result) {
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


}
