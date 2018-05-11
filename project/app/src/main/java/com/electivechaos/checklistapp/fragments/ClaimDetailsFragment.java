package com.electivechaos.checklistapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.electivechaos.checklistapp.R;
import com.electivechaos.checklistapp.adapters.DrawerMenuListAdapter;
import com.electivechaos.checklistapp.interfaces.NextButtonClickListener;

public class ClaimDetailsFragment  extends Fragment{


    ViewPager viewPager;
    TabLayout tabLayout;
    View view;


    String reportTitle = "";
    String reportDescription = "";
    String clientName = "";
    String claimNumber = "";
    String address = "";


    String locationLat = "";
    String locationLong = "";

    private Boolean isFabOpen = false;
    private FloatingActionButton showFabBtn,fabGoNextBtn, fabAddLabelBtn, fabGenerateReportBtn;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;

    private NextButtonClickListener nextButtonClickListener;
    private DrawerMenuListAdapter.OnLabelAddClickListener onLabelAddClickListener;

    @Override
    public void onStart() {
        super.onStart();
        Bundle passedArgs = getArguments();

        if(passedArgs != null){
            reportTitle = passedArgs.get("reportTitle").toString();
            reportDescription = passedArgs.get("reportDescription").toString();
            clientName = passedArgs.get("clientName").toString();
            claimNumber = passedArgs.get("claimNumber").toString();

            locationLat = passedArgs.get("locationLat").toString();
            locationLong = passedArgs.get("locationLong").toString();

        }
    }

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
        showFabBtn =  view.findViewById(R.id.showFab);
        fabGoNextBtn = view. findViewById(R.id.fabGoNext);
        fabAddLabelBtn = view. findViewById(R.id.fabAddLabel);
        fabGenerateReportBtn =  view.findViewById(R.id.fabGenerateReport);

        fab_open = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_backward);
        showFabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFAB();
            }
        });
        viewPager = view.findViewById(R.id.claim_details_view_pager);
        tabLayout = view.findViewById(R.id.claim_details_tab_layout);
        tabLayout.setupWithViewPager(viewPager);


        fabAddLabelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLabelAddClickListener.onLabelAddClick();
            }
        });

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

        fabGoNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextButtonClickListener.onNextButtonClick();
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
                Bundle claimDetailsArgs = new Bundle();
                claimDetailsArgs.putString("reportTitle", reportTitle);
                claimDetailsArgs.putString("reportDescription", reportDescription);
                claimDetailsArgs.putString("clientName", clientName);
                claimDetailsArgs.putString("claimNumber", claimNumber);
                claimDetailsArgs.putString("address", address);
                fragment.setArguments(claimDetailsArgs);
                return fragment;
            }else if(i==1){
                Fragment fragment = new LossLocationFragment();

                Bundle claimDetailsLocationArgs = new Bundle();
                claimDetailsLocationArgs.putString("locationLat", locationLat);
                claimDetailsLocationArgs.putString("locationLong", locationLong);
                fragment.setArguments(claimDetailsLocationArgs);
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
    public void animateFAB() {
        if (isFabOpen) {
            showFabBtn.startAnimation(rotate_backward);
            fabGoNextBtn.startAnimation(fab_close);
            fabAddLabelBtn.startAnimation(fab_close);
            fabGenerateReportBtn.startAnimation(fab_close);
            fabGoNextBtn.setClickable(false);
            fabAddLabelBtn.setClickable(false);
            fabGenerateReportBtn.setClickable(false);
            isFabOpen = false;

        } else {
            showFabBtn.startAnimation(rotate_forward);
            fabGoNextBtn.startAnimation(fab_open);
            fabAddLabelBtn.startAnimation(fab_open);
            fabGenerateReportBtn.startAnimation(fab_open);
            fabGoNextBtn.setClickable(true);
            fabAddLabelBtn.setClickable(true);
            fabGenerateReportBtn.setClickable(true);
            isFabOpen = true;
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            nextButtonClickListener = (NextButtonClickListener) getActivity();
            onLabelAddClickListener = (DrawerMenuListAdapter.OnLabelAddClickListener)getActivity();
        }catch (ClassCastException ex) {
            ex.printStackTrace();
        }
    }
}
