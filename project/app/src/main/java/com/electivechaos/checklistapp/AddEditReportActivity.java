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

import com.electivechaos.checklistapp.pojo.ImageDetailsPOJO;
import com.electivechaos.checklistapp.pojo.Label;
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
    private DrawerMenuListAdapter drawerMenuListAdapter;
    private String tabName;

    private  String reportTitle = null;
    private  String reportDescription = null;
    private  String clientName = null;
    private String claimNumber = null;
    private String address = null;
    private String reportId = null;
    private String reportPath = null;



    private ArrayList<ImageDetailsPOJO> selectedImagesList = new ArrayList<>();
    private ArrayList<ImageDetailsPOJO> selectedElevationImagesList = new ArrayList<>();


    HashMap<String,List<Label>> childMenuItems = new HashMap<>();
    List<Label> inspectionChildMenu = new ArrayList<>();
    ArrayList<String> parentMenuItems;


    private static final String MY_FRAGMENT_TAG = "AddEditReportSelectedImagesFragment";

    private static final String CLAIM_DETAILS_FRAGMENT_TAG = "claimdetailsfragment";
    private static final String CAUSE_OF_LOSS_FRAGMENT_TAG = "causeoflossfragment";
    private static final String POINT_OF_ORIGIN_FRAGMENT_TAG = "pointoforiginfragment";

    private AddEditReportSelectedImagesFragment myFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);



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

                Fragment fragmentToRemove = transactionManager.findFragmentByTag(CLAIM_DETAILS_FRAGMENT_TAG);
                FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
                if(fragmentToRemove != null){
                    fragmentTransaction.remove(fragmentToRemove);
                }
                fragmentTransaction.add(R.id.content_frame,new PointOfOriginFragment(),CLAIM_DETAILS_FRAGMENT_TAG);
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


                myFragment = (AddEditReportSelectedImagesFragment) getSupportFragmentManager().findFragmentByTag(MY_FRAGMENT_TAG);
                tabName="AddEditReportSelectedImagesFragment";
            }
            else {


                FragmentManager transactionManager = getSupportFragmentManager();

                transactionManager.popBackStack(0,FragmentManager.POP_BACK_STACK_INCLUSIVE);

                Fragment fragmentToRemove = transactionManager.findFragmentByTag(CLAIM_DETAILS_FRAGMENT_TAG);

                FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();

                if(fragmentToRemove != null){
                    fragmentTransaction.remove(fragmentToRemove);
                }
                fragmentTransaction.add(R.id.content_frame,new ClaimDetailsFragment(), CLAIM_DETAILS_FRAGMENT_TAG);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                tabName="ClaimDetailsFragment";
            }

        }
        else {


            FragmentManager transactionManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_frame,new ClaimDetailsFragment());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            tabName="ClaimDetailsFragment";
        }



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);



        mExpandableListView = findViewById(R.id.slider_menu);

        parentMenuItems = new ArrayList<>();

        parentMenuItems.add("Claim Details");
        parentMenuItems.add("Cause Of Loss");
        parentMenuItems.add("Point Of Origin");
        parentMenuItems.add("Inspection");



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

                    transactionManager.popBackStack(0,FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    Fragment fragment = transactionManager.findFragmentByTag(CLAIM_DETAILS_FRAGMENT_TAG);

                    FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();

                    if(fragment != null){
                        fragmentTransaction.remove(fragment);
                    }
                    fragmentTransaction.add(R.id.content_frame,new ClaimDetailsFragment(),CLAIM_DETAILS_FRAGMENT_TAG);
                    fragmentTransaction.addToBackStack(CLAIM_DETAILS_FRAGMENT_TAG);
                    fragmentTransaction.commit();
                    transactionManager.executePendingTransactions();
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

//                FragmentManager transactionManager = getSupportFragmentManager();
//                transactionManager.findFragmentByTag();
//                FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
//                AddEditReportSelectedImagesFragment df=AddEditReportSelectedImagesFragment.initFragment(selectedImagesList,reportId,reportPath,selectedElevationImagesList);
//                fragmentTransaction.replace(R.id.content_frame,df.initFragment(selectedImagesList,reportId,reportPath,selectedElevationImagesList));
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commit();
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
        Intent intent = new Intent();
        setResult(RESULT_OK,intent);
        finish();

    }


    @Override
    public void sendData(JSONObject message) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

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
    }

    @Override
    public void onEditLabelClick(Label label, int childPosition) {
        FragmentManager transactionManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
        AddEditLabelFragment addEditLabelFragment = new AddEditLabelFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("labelID", label.getID());
        bundle.putString("labelName", label.getName());
        bundle.putString("labelDesc", label.getDescription());
        bundle.putInt("categoryID", label.getCategoryID());
        bundle.putInt("childPosition", childPosition);
        addEditLabelFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.content_frame, addEditLabelFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onLabelDataReceive(Label label) {
       List<Label> labelList =  childMenuItems.get("Inspection");
       labelList.add(label);
       childMenuItems.put("Inspection", labelList);
       drawerMenuListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLabelDataEdited(Label label, int childPosition) {
        List<Label> labelList =  childMenuItems.get("Inspection");
        labelList.set(childPosition, label);
        childMenuItems.put("Inspection", labelList);
        drawerMenuListAdapter.notifyDataSetChanged();
    }
}
