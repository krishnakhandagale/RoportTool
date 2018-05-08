package com.electivechaos.checklistapp.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.electivechaos.checklistapp.R;
import com.electivechaos.checklistapp.adapters.DrawerMenuListAdapter;
import com.electivechaos.checklistapp.fragments.AddEditLabelFragment;
import com.electivechaos.checklistapp.fragments.AddEditReportSelectedImagesFragment;
import com.electivechaos.checklistapp.fragments.CauseOfLossFragment;
import com.electivechaos.checklistapp.fragments.ClaimDetailsFragment;
import com.electivechaos.checklistapp.fragments.PointOfOriginFragment;
import com.electivechaos.checklistapp.interfaces.ClaimDetailsDataInterface;
import com.electivechaos.checklistapp.interfaces.LossLocationDataInterface;
import com.electivechaos.checklistapp.interfaces.SelectedImagesDataInterface;
import com.electivechaos.checklistapp.pojo.ImageDetailsPOJO;
import com.electivechaos.checklistapp.pojo.Label;
import com.electivechaos.checklistapp.pojo.ReportPOJO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddEditReportActivity extends AppCompatActivity implements  DrawerMenuListAdapter.MyItemClickListener, AddEditLabelFragment.AddEditLabelInterface, ClaimDetailsDataInterface, LossLocationDataInterface,SelectedImagesDataInterface{
    private DrawerLayout mDrawerLayout;
    private FloatingActionButton saveReportBtn;
    private ExpandableListView mExpandableListView;
    private DrawerMenuListAdapter drawerMenuListAdapter;
    private String tabName;

    HashMap<String,List<Label>> childMenuItems = new HashMap<>();
    List<Label> inspectionChildMenu = new ArrayList<>();
    ArrayList<String> parentMenuItems;

    int selectedFragmentPosition;


    ReportPOJO reportPOJO = new ReportPOJO();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        FragmentManager transactionManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame,new ClaimDetailsFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        tabName="ClaimDetailsFragment";

        selectedFragmentPosition=0;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        getSupportActionBar().setTitle("Claim Details");

        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);


        mExpandableListView = findViewById(R.id.slider_menu);
        saveReportBtn = findViewById(R.id.saveReport);

        saveReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(selectedFragmentPosition==0) {
                    mDrawerLayout.closeDrawers();
                    FragmentManager transactionManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
                    fragmentTransaction.replace(R.id.content_frame,new CauseOfLossFragment());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    tabName="CauseOfLossFragment";

                    selectedFragmentPosition=1;
                    getSupportActionBar().setTitle("Cause Of Loss");
                }
                else if(selectedFragmentPosition==1) {
                    mDrawerLayout.closeDrawers();
                    FragmentManager transactionManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
                    fragmentTransaction.replace(R.id.content_frame,new PointOfOriginFragment());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    tabName="PointOfOriginFragment";

                    selectedFragmentPosition=2;
                    getSupportActionBar().setTitle("Point Of Origin");

                }
                else {

                }
                Toast.makeText(AddEditReportActivity.this,"My next",Toast.LENGTH_SHORT).show();

                Log.d("reportPOJO",reportPOJO.toString());
            }
        });
        parentMenuItems = new ArrayList<>();

        parentMenuItems.add("Claim Details");
        parentMenuItems.add("Cause Of Loss");
        parentMenuItems.add("Point Of Origin");
        parentMenuItems.add("Inspection");


        inspectionChildMenu = (List<Label>) reportPOJO.getLabelArrayList().clone();
        childMenuItems.put("Inspection", inspectionChildMenu);


        mDrawerLayout = findViewById(R.id.drawer_layout);

        if(parentMenuItems != null && parentMenuItems.size() > 0){
            drawerMenuListAdapter = new DrawerMenuListAdapter(this,parentMenuItems, childMenuItems);
            mExpandableListView.setAdapter(drawerMenuListAdapter);
            mExpandableListView.setIndicatorBounds(0, 20);
        }
        mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                if(parentMenuItems.get(groupPosition).equals("Claim Details")){

                    mDrawerLayout.closeDrawers();
                    FragmentManager transactionManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();

                    ClaimDetailsFragment claimDetailsFragment = new ClaimDetailsFragment();
                    Bundle claimDetailsData = new Bundle();
                    claimDetailsData.putString("reportTitle",reportPOJO.getReportTitle());
                    claimDetailsData.putString("reportDescription",reportPOJO.getReportDescription());
                    claimDetailsData.putString("claimNumber",reportPOJO.getClaimNumber());
                    claimDetailsData.putString("claimNumber",reportPOJO.getClaimNumber());
                    claimDetailsData.putString("clientName",reportPOJO.getClientName());

                    claimDetailsData.putString("locationLat",reportPOJO.getLocationLat());
                    claimDetailsData.putString("locationLong",reportPOJO.getLocationLong());

                    claimDetailsFragment.setArguments(claimDetailsData);
                    fragmentTransaction.replace(R.id.content_frame,claimDetailsFragment );
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    tabName="ClaimDetailsFragment";

                    getSupportActionBar().setTitle("Claim Details");


                    selectedFragmentPosition=0;


                } else if (parentMenuItems.get(groupPosition).equals("Cause Of Loss")) {
                    mDrawerLayout.closeDrawers();
                    FragmentManager transactionManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
                    fragmentTransaction.replace(R.id.content_frame,new CauseOfLossFragment());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    tabName="CauseOfLossFragment";

                    selectedFragmentPosition=1;
                    getSupportActionBar().setTitle("Cause Of Loss");

                } else if (parentMenuItems.get(groupPosition).equals("Point Of Origin")) {
                    mDrawerLayout.closeDrawers();
                    FragmentManager transactionManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
                    fragmentTransaction.replace(R.id.content_frame,new PointOfOriginFragment());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    tabName="PointOfOriginFragment";

                    selectedFragmentPosition=2;
                    getSupportActionBar().setTitle("Point Of Origin");
                }
                return false;
            }
        });


        mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                mDrawerLayout.closeDrawers();
                FragmentManager transactionManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
                AddEditReportSelectedImagesFragment addEditReportSelectedImagesFragment= AddEditReportSelectedImagesFragment.initFragment(reportPOJO.getLabelArrayList().get(childPosition).getSelectedImages(),reportPOJO.getLabelArrayList().get(childPosition).getSelectedElevationImages(),childPosition);
                fragmentTransaction.replace(R.id.content_frame,addEditReportSelectedImagesFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                tabName="AddEditReportSelectedImagesFragment";
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
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.back_button_confirmation_dialog, null);
        dialogBuilder.setView(dialogView);

        TextView positiveBtn = dialogView.findViewById(R.id.positive_button);
        TextView negativeBtn = dialogView.findViewById(R.id.negative_button);


        final AlertDialog alertDialog = dialogBuilder.create();

        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                AddEditReportActivity.super.onBackPressed();
            }
        });

        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    public void onItemClick(int position) {
        FragmentManager transactionManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, new AddEditLabelFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        mDrawerLayout.closeDrawer(Gravity.LEFT);
    }

    @Override
    public void onEditLabelClick(Label label, int childPosition) {
        FragmentManager transactionManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
        AddEditLabelFragment addEditLabelFragment = new AddEditLabelFragment();
        Bundle bundle = new Bundle();

        bundle.putLong("labelID", label.getId());
        bundle.putString("labelName", label.getName());
        bundle.putString("labelDesc", label.getDescription());
        bundle.putInt("categoryID", label.getCategoryID());
        bundle.putInt("childPosition", childPosition);

        addEditLabelFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.content_frame, addEditLabelFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        mDrawerLayout.closeDrawer(Gravity.LEFT);
    }

    @Override
    public void onLabelAdded(Label label) {

        List<Label> labelList =  childMenuItems.get("Inspection");
        labelList.add(label);
        drawerMenuListAdapter.notifyDataSetChanged();

        reportPOJO.getLabelArrayList().add(label);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        AddEditReportSelectedImagesFragment addEditReportSelectedImagesFragment = AddEditReportSelectedImagesFragment.initFragment(new ArrayList<ImageDetailsPOJO>(),new ArrayList<ImageDetailsPOJO>(),labelList.size()-1);
        fragmentTransaction.replace(R.id.content_frame, addEditReportSelectedImagesFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();


    }

    @Override
    public void onLabelEdited(Label label, int childPosition) {

        List<Label> labelList =  childMenuItems.get("Inspection");
        labelList.set(childPosition, label);
        drawerMenuListAdapter.notifyDataSetChanged();


       Label tempLabel=  reportPOJO.getLabelArrayList().get(childPosition);


       tempLabel.setName(label.getName());
       tempLabel.setDescription(label.getDescription());
       tempLabel.setCategoryID(label.getCategoryID());

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        AddEditReportSelectedImagesFragment addEditReportSelectedImagesFragment = AddEditReportSelectedImagesFragment.initFragment(reportPOJO.getLabelArrayList().get(childPosition).getSelectedImages(),reportPOJO.getLabelArrayList().get(childPosition).getSelectedElevationImages(),childPosition);
        fragmentTransaction.replace(R.id.content_frame, addEditReportSelectedImagesFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
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


}
