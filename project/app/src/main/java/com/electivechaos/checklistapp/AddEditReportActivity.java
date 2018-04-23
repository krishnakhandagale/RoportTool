package com.electivechaos.checklistapp;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import com.electivechaos.checklistapp.adapters.DrawerMenuListAdapter;
import com.electivechaos.checklistapp.fragments.CategoryWiseImagesFragment;
import com.electivechaos.checklistapp.fragments.CauseOfLossFragment;
import com.electivechaos.checklistapp.fragments.ClaimDetailsFragment;
import com.electivechaos.checklistapp.fragments.PointOfOriginFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddEditReportActivity extends AppCompatActivity{
    private DrawerLayout mDrawerLayout;
    private ExpandableListView mExpandableListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        FragmentManager transactionManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame,new ClaimDetailsFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();





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


        HashMap<String,List<String>> childMenuItems = new HashMap<>();

        List<String> inspectionChildMenu = new ArrayList<>();
        inspectionChildMenu.add("Example1");
        inspectionChildMenu.add("Example2");
        inspectionChildMenu.add("Example3");
        inspectionChildMenu.add("Example4");

        childMenuItems.put("Inspection", inspectionChildMenu);


        mDrawerLayout = findViewById(R.id.drawer_layout);

        if(parentMenuItems != null && parentMenuItems.size() > 0){
            DrawerMenuListAdapter drawerMenuListAdapter = new DrawerMenuListAdapter(this,parentMenuItems, childMenuItems);
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

                } else if (parentMenuItems.get(groupPosition).equals("Cause Of Loss")) {

                    FragmentManager transactionManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
                    fragmentTransaction.replace(R.id.content_frame,new CauseOfLossFragment());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

                } else if (parentMenuItems.get(groupPosition).equals("Point Of Origin")) {

                    FragmentManager transactionManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
                    fragmentTransaction.replace(R.id.content_frame,new PointOfOriginFragment());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                return false;
            }
        });


        mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                FragmentManager transactionManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_frame,new CategoryWiseImagesFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
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

}
