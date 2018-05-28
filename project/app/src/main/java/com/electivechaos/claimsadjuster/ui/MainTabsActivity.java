package com.electivechaos.claimsadjuster.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.electivechaos.claimsadjuster.BaseActivity;
import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.maintabs.AlertFragment;
import com.electivechaos.claimsadjuster.maintabs.CategoryListFragment;
import com.electivechaos.claimsadjuster.maintabs.PerilListFragment;
import com.electivechaos.claimsadjuster.maintabs.ReportListFragment;

public class MainTabsActivity extends BaseActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.main_tabs_layout_activity);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ViewPager viewPager = findViewById(R.id.main_viewpager);

        MainTabsActivityPagerAdapter mainTabsActivityPagerAdapter = new MainTabsActivityPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mainTabsActivityPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }



    //View pager  for showing two tabs in the welcome activity

    public class MainTabsActivityPagerAdapter extends FragmentStatePagerAdapter {

        public MainTabsActivityPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            if( i ==0 ){
                Fragment fragment = new ReportListFragment();
                return fragment;
            }else if(i == 1){
                Fragment fragment = new CategoryListFragment();
                return fragment;
            }else {
                Fragment fragment = new AlertFragment();
                return fragment;
            }

        }
        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(position == 0){
                return  "Report List";
            }else if(position == 1){
                return  "Categories";
            }else {
                return "Alert";
            }
        }
    }





}
