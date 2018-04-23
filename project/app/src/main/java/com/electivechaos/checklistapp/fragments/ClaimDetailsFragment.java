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
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View  view = inflater.inflate(R.layout.claim_details_layout,container, false);

        viewPager = view.findViewById(R.id.claim_details_view_pager);
        tabLayout = view.findViewById(R.id.claim_details_tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        ClaimDetailsTabsPagerAdapter adapter=new ClaimDetailsTabsPagerAdapter(getActivity().getSupportFragmentManager());
        viewPager.setAdapter(adapter);



        return view;
    }






    //View pager  for showing two tabs in the welcome activity

    public class ClaimDetailsTabsPagerAdapter extends FragmentStatePagerAdapter {

        public ClaimDetailsTabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            if( i ==0 ){
                Fragment fragment = new ClaimDetailsTabsFragment();
                return fragment;
            }else if(i==1){
                Fragment fragment = new CauseOfLossFragment();
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
                return  "Cause Of Loss";
            }else {
                return "Loss Type";
            }
        }
    }


}
