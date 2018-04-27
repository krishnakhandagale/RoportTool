package com.electivechaos.checklistapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.electivechaos.checklistapp.R;

import org.json.JSONObject;

public class ClaimDetailsFragment  extends Fragment implements ClaimDetailsTabsFragment.SendImageDetails{


    ViewPager viewPager;
    TabLayout tabLayout;
    View view;
    int tabIndex;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        Toast.makeText(getContext(),"On Create called",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("tabIndex",tabIndex);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void sendData(JSONObject message) {
       /* String tag = "android:switcher:"  + ":" + 1;
        ClaimDetailsTabsFragment f = (ClaimDetailsTabsFragment) getSupportFragmentManager().findFragmentByTag(tag);
        //f.setReceivedImageDetailsData(message);*/
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.claim_details_layout,container, false);
        viewPager = view.findViewById(R.id.claim_details_view_pager);
        tabLayout = view.findViewById(R.id.claim_details_tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        ClaimDetailsTabsPagerAdapter adapter=new ClaimDetailsTabsPagerAdapter(getActivity().getSupportFragmentManager(),tabIndex);
        viewPager.setAdapter(adapter);



        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab){
                int position = tab.getPosition();
                if(position==0){
                   tabIndex =position;
                    Toast.makeText(getContext(),"TAB INDEX"+position,Toast.LENGTH_SHORT).show();
                   // viewPager.setCurrentItem(position);

                    tab.select();

                }
                else if(position==1)
                {
                    Toast.makeText(getContext(),"TAB INDEX"+position,Toast.LENGTH_SHORT).show();
                  //  viewPager.setCurrentItem(position);
                    tab.select();

                }
                else{
                    Toast.makeText(getContext(),"TAB INDEX"+position,Toast.LENGTH_SHORT).show();

                }
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







    //View pager  for showing two tabs in the welcome activity

    public class ClaimDetailsTabsPagerAdapter extends FragmentStatePagerAdapter {
        int tabIndex;

        public ClaimDetailsTabsPagerAdapter(FragmentManager fm,int tabIndex) {
            super(fm);
            this.tabIndex=tabIndex;
            viewPager.setCurrentItem(tabIndex);
        }

        @Override
        public Fragment getItem(int i) {

            if( i ==0 ){
                Fragment fragment = new ClaimDetailsTabsFragment();
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
