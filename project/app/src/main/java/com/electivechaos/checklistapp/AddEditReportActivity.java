package com.electivechaos.checklistapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.electivechaos.checklistapp.Pojo.ImageDetailsPOJO;
import com.electivechaos.checklistapp.Pojo.Label;
import com.electivechaos.checklistapp.adapters.DrawerMenuListAdapter;
import com.electivechaos.checklistapp.fragments.AddEditLabelFragment;
import com.electivechaos.checklistapp.fragments.CauseOfLossFragment;
import com.electivechaos.checklistapp.fragments.ClaimDetailsFragment;
import com.electivechaos.checklistapp.fragments.ClaimDetailsTabsFragment;
import com.electivechaos.checklistapp.fragments.PointOfOriginFragment;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddEditReportActivity extends AppCompatActivity implements ClaimDetailsTabsFragment.SendImageDetails,AddEditReportSelectedImagesFragment.SendReportDBChangeSignal, DrawerMenuListAdapter.MyItemClickListener, AddEditLabelFragment.AddEditLabelInterface{
    private DrawerLayout mDrawerLayout;
    private ExpandableListView mExpandableListView;
    DrawerMenuListAdapter drawerMenuListAdapter;
    Fragment mContent;
    String tabName;

    private  String reportTitle = null;
    private  String reportDescription = null;
    private  String clientName = null;
    private String claimNumber = null;
    private String address = null;

    private String reportId = null;
    private String reportPath = null;
    private ArrayList<ImageDetailsPOJO> selectedImagesList = new ArrayList<>();
    private ArrayList<ImageDetailsPOJO> selectedElevationImagesList = new ArrayList<>();
    HashMap<String,List<String>> childMenuItems = new HashMap<>();
    List<String> inspectionChildMenu = new ArrayList<>();


    private static final String MY_FRAGMENT_TAG = "AddEditReportSelectedImagesFragment";
    private AddEditReportSelectedImagesFragment myFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
           // myFragment = AddEditReportSelectedImagesFragment.newInstance();
          /*  getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, myFragment, MY_FRAGMENT_TAG)
                    .commit();*/
        } else {
            myFragment = (AddEditReportSelectedImagesFragment) getSupportFragmentManager()
                    .findFragmentByTag(MY_FRAGMENT_TAG);
        }
        if(savedInstanceState != null){

            String tName = savedInstanceState.getString("tabName").toString();

            selectedImagesList = (ArrayList<ImageDetailsPOJO>) savedInstanceState.getSerializable("selectedImagesList");
            selectedElevationImagesList = (ArrayList<ImageDetailsPOJO>) savedInstanceState.getSerializable("selectedElevationImagesList");
            reportTitle =  savedInstanceState.getString("reportTitle");
            reportDescription =  savedInstanceState.getString("reportDescription");
            clientName =  savedInstanceState.getString("clientName");
            claimNumber =  savedInstanceState.getString("claimNumber");
            address =  savedInstanceState.getString("address");
            reportPath =  savedInstanceState.getString("reportPath");


            if(tName.equalsIgnoreCase("PointOfOriginFragment")) {
                FragmentManager transactionManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_frame,new PointOfOriginFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                tabName="PointOfOriginFragment";
            }
            else if(tName.equalsIgnoreCase("CauseOfLossFragment")) {
                FragmentManager transactionManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_frame,new CauseOfLossFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                tabName="CauseOfLossFragment";

            }
            else if(tName.equalsIgnoreCase("AddEditReportSelectedImagesFragment")){
              /*  if(getIntent().getExtras()!= null){
                    selectedImagesList = (ArrayList<ImageDetailsPOJO>) getIntent().getExtras().getSerializable("selectedImagesList");
                    selectedElevationImagesList = (ArrayList<ImageDetailsPOJO>) getIntent().getExtras().getSerializable("selectedElevationImagesList");
                    reportTitle =  getIntent().getExtras().getString("reportTitle");
                    reportDescription =  getIntent().getExtras().getString("reportDescription");
                    clientName =  getIntent().getExtras().getString("clientName");
                    claimNumber =  getIntent().getExtras().getString("claimNumber");
                    address =  getIntent().getExtras().getString("address");
                    reportId =  getIntent().getExtras().getString("reportId");
                    reportPath =  getIntent().getExtras().getString("reportPath");
                }*/

                myFragment = (AddEditReportSelectedImagesFragment) getSupportFragmentManager()
                        .findFragmentByTag(MY_FRAGMENT_TAG);
              /*  FragmentManager transactionManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
                AddEditReportSelectedImagesFragment ae=new AddEditReportSelectedImagesFragment();
                fragmentTransaction.replace(R.id.content_frame,ae.initFragment(selectedImagesList,reportId,reportPath,selectedElevationImagesList));
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();*/
                tabName="AddEditReportSelectedImagesFragment";
            }
            else {
                FragmentManager transactionManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_frame,new ClaimDetailsFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                tabName="ClaimDetailsFragment";
            }

        }
        else {
                if(getIntent().getExtras()!= null){
                    selectedImagesList = (ArrayList<ImageDetailsPOJO>) getIntent().getExtras().getSerializable("selectedImagesList");
                    selectedElevationImagesList = (ArrayList<ImageDetailsPOJO>) getIntent().getExtras().getSerializable("selectedElevationImagesList");
                    reportTitle =  getIntent().getExtras().getString("reportTitle");
                    reportDescription =  getIntent().getExtras().getString("reportDescription");
                    clientName =  getIntent().getExtras().getString("clientName");
                    claimNumber =  getIntent().getExtras().getString("claimNumber");
                    address =  getIntent().getExtras().getString("address");
                    reportId =  getIntent().getExtras().getString("reportId");
                    reportPath =  getIntent().getExtras().getString("reportPath");
                    }

            FragmentManager transactionManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_frame,new ClaimDetailsFragment());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            tabName="ClaimDetailsFragment";
        }



        setContentView(R.layout.activity_main);
     /*   FragmentManager transactionManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame,new ClaimDetailsFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();*/





        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);



        mExpandableListView = findViewById(R.id.slider_menu);

        final ArrayList<String> parentMenuItems = new ArrayList<>();
        parentMenuItems.add("Claim Details");
        parentMenuItems.add("Cause Of Loss");
        parentMenuItems.add("Point Of Origin");
        parentMenuItems.add("Inspection");





//        inspectionChildMenu.add("Example1");
//        inspectionChildMenu.add("Example2");
//        inspectionChildMenu.add("Example3");
//        inspectionChildMenu.add("Example4");

        childMenuItems.put("Inspection", inspectionChildMenu);


        mDrawerLayout = findViewById(R.id.drawer_layout);

        if(parentMenuItems != null && parentMenuItems.size() > 0){
            drawerMenuListAdapter = new DrawerMenuListAdapter(this,parentMenuItems, childMenuItems);
            mExpandableListView.setAdapter(drawerMenuListAdapter);
        }
        mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                if(parentMenuItems.get(groupPosition).equals("Claim Details")){


                    FragmentManager transactionManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
                    fragmentTransaction.replace(R.id.content_frame,new ClaimDetailsFragment());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    tabName="ClaimDetailsFragment";

                } else if (parentMenuItems.get(groupPosition).equals("Cause Of Loss")) {

                    FragmentManager transactionManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
                    fragmentTransaction.replace(R.id.content_frame,new CauseOfLossFragment());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    tabName="CauseOfLossFragment";

                } else if (parentMenuItems.get(groupPosition).equals("Point Of Origin")) {

                    FragmentManager transactionManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
                    fragmentTransaction.replace(R.id.content_frame,new PointOfOriginFragment());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    tabName="PointOfOriginFragment";
                }
                return false;
            }
        });


        mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                FragmentManager transactionManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
                AddEditReportSelectedImagesFragment df=AddEditReportSelectedImagesFragment.initFragment(selectedImagesList,reportId,reportPath,selectedElevationImagesList);
                fragmentTransaction.replace(R.id.content_frame,df.initFragment(selectedImagesList,reportId,reportPath,selectedElevationImagesList));
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
    public void notifyReportDBChanged() {

//        String tag1 = "android:switcher:" + R.id.viewpager + ":" + 0;
//        AddEditReportDetailsFragment reportDetailsFragment = (AddEditReportDetailsFragment) getSupportFragmentManager().findFragmentByTag(tag1);
//        reportDetailsFragment.resetAllData();
//
//        String tag2 = "android:switcher:" + R.id.viewpager + ":" + 1;
//        AddEditReportSelectedImagesFragment selectImagesFragment = (AddEditReportSelectedImagesFragment) getSupportFragmentManager().findFragmentByTag(tag2);
//        selectImagesFragment.resetAllData();

        Intent intent = new Intent();
        setResult(RESULT_OK,intent);
        finish();

    }


    @Override
    public void sendData(JSONObject message) {
       /* String tag = "android:switcher:" + R.id.viewpager + ":" + 1;
        AddEditReportSelectedImagesFragment f = (AddEditReportSelectedImagesFragment) getSupportFragmentManager().findFragmentByTag(tag);
        f.setReceivedImageDetailsData(message);*/
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putString("tabName",tabName);
        outState.putSerializable("selectedImagesList",selectedImagesList);
        outState.putSerializable("selectedElevationImagesList",selectedElevationImagesList);
        outState.putString("reportTitle",reportTitle);
        outState.putString("reportDescription",reportDescription);
        outState.putString("clientName",clientName);
        outState.putString("claimNumber",claimNumber);
        outState.putString("address",address);
        outState.putString("reportId",reportId);
        outState.putString("reportPath",reportPath);
        super.onSaveInstanceState(outState);

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
        android.support.v4.app.FragmentManager transactionManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
//                  AddEditReportSelectedImagesFragment df=new AddEditReportSelectedImagesFragment();
        fragmentTransaction.replace(R.id.content_frame, new AddEditLabelFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onLabelDataReceive(Label label) {
        DrawerMenuListAdapter adapter =
                (DrawerMenuListAdapter) mExpandableListView.getExpandableListAdapter();
       adapter.getChildList(3).add(label.getName().toString());
        adapter.notifyDataSetChanged();
    }
}
