package com.electivechaos.checklistapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.electivechaos.checklistapp.R;

public class ClaimDetailsFragment  extends Fragment{


    ViewPager viewPager;
    TabLayout tabLayout;
    View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.claim_details_layout,container, false);
        viewPager = view.findViewById(R.id.claim_details_view_pager);
        tabLayout = view.findViewById(R.id.claim_details_tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        ClaimDetailsTabsPagerAdapter adapter=new ClaimDetailsTabsPagerAdapter(getActivity().getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab){

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return view;


    }


    public class ClaimDetailsTabsPagerAdapter extends FragmentStatePagerAdapter {

        public ClaimDetailsTabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {

            if( i ==0 ){
                Fragment fragment =  new ClaimDetailsTabsFragment();
                return fragment;
            }else if(i==1){
                Fragment fragment = new LossLocationFragment();
                return fragment;
            }
            else {
                Fragment fragment = new LossTypeFragment();
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
                return  "Claim Details";
            }else if(position==1){
                return  "Loss Location";
            }else {
                return "Loss Type";
            }
        }
    }


}
