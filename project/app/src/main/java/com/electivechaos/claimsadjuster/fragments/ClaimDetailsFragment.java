package com.electivechaos.claimsadjuster.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.adapters.DrawerMenuListAdapter;
import com.electivechaos.claimsadjuster.interfaces.BackButtonClickListener;
import com.electivechaos.claimsadjuster.interfaces.NextButtonClickListener;
import com.electivechaos.claimsadjuster.interfaces.OnGenerateReportClickListener;
import com.electivechaos.claimsadjuster.interfaces.OnSaveReportClickListener;

public class ClaimDetailsFragment  extends Fragment{


    ViewPager viewPager;
    TabLayout tabLayout;
    View view;


    String reportTitle = "";
    String reportDescription = "";
    String clientName = "";
    String claimNumber = "";
    String reportBy = "";
    String address = "";


    String locationLat = "";
    String locationLong = "";
    String addressLine = "";

    private Boolean isFabOpen = false;
    private FloatingActionButton showFabBtn,fabGoNextBtn,fabGoBackBtn, fabAddLabelBtn, fabGenerateReportBtn, fabSaveReportBtn;
    private Animation fab_open;
    private Animation fab_close;

    private NextButtonClickListener nextButtonClickListener;
    private DrawerMenuListAdapter.OnLabelAddClickListener onLabelAddClickListener;
    private OnSaveReportClickListener onSaveReportClickListener;
    private OnGenerateReportClickListener onGenerateReportClickListener;
    private BackButtonClickListener backButtonClickListener;

    @Override
    public void onStart() {
        super.onStart();
        Bundle passedArgs = getArguments();

        if(passedArgs != null){
            reportTitle = passedArgs.get("reportTitle").toString();
            reportDescription = passedArgs.get("reportDescription").toString();
            clientName = passedArgs.get("clientName").toString();
            claimNumber = passedArgs.get("claimNumber").toString();
            reportBy = passedArgs.get("reportBy").toString();

            locationLat = passedArgs.get("locationLat").toString();
            locationLong = passedArgs.get("locationLong").toString();
            addressLine = passedArgs.get("addressLine").toString();

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
        fabGoBackBtn = view.findViewById(R.id.fabGoBack);

        fabAddLabelBtn = view. findViewById(R.id.fabAddLabel);
        fabGenerateReportBtn =  view.findViewById(R.id.fabGenerateReport);
        fabSaveReportBtn  = view.findViewById(R.id.fabSaveReport);

        fab_open = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_close);
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
                animateFAB();

            }
        });

        fabSaveReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveReportClickListener.onReportSave(true);
                animateFAB();
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
                animateFAB();
            }
        });

        //Add code
        fabGoBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backButtonClickListener.onBackButtonClick();
                animateFAB();
            }
        });

        fabGenerateReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFAB();
                onGenerateReportClickListener.onReportGenerateClicked();

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
                claimDetailsArgs.putString("reportBy", reportBy);
                claimDetailsArgs.putString("address", address);
                fragment.setArguments(claimDetailsArgs);
                return fragment;
            }else{
                Fragment fragment = new LossLocationFragment();

                Bundle claimDetailsLocationArgs = new Bundle();
                claimDetailsLocationArgs.putString("locationLat", locationLat);
                claimDetailsLocationArgs.putString("locationLong", locationLong);
                claimDetailsLocationArgs.putString("addressLine", addressLine);

                fragment.setArguments(claimDetailsLocationArgs);
                return fragment;
            }

        }
        @Override
        public int getCount() {

            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "Claim Details";
            } else{
                return "Loss Location";
            }
        }
    }
    public void animateFAB() {
        if (isFabOpen) {

            fabGoNextBtn.startAnimation(fab_close);
            fabGoBackBtn.startAnimation(fab_close);
            fabAddLabelBtn.startAnimation(fab_close);
            fabGenerateReportBtn.startAnimation(fab_close);
            fabSaveReportBtn.startAnimation(fab_close);
            fabGoNextBtn.setClickable(false);
            fabGoBackBtn.setClickable(false);
            fabAddLabelBtn.setClickable(false);
            fabGenerateReportBtn.setClickable(false);
            fabSaveReportBtn.setClickable(false);
            showFabBtn.setImageResource(R.drawable.ic_more_vertical_white);
            isFabOpen = false;

        } else {

            
            fabGoNextBtn.startAnimation(fab_open);
            fabGoBackBtn.startAnimation(fab_open);
            fabAddLabelBtn.startAnimation(fab_open);
            fabGenerateReportBtn.startAnimation(fab_open);
            fabSaveReportBtn.startAnimation(fab_open);
            fabGoNextBtn.setClickable(true);
            fabGoBackBtn.setClickable(true);
            fabAddLabelBtn.setClickable(true);
            fabGenerateReportBtn.setClickable(true);
            fabSaveReportBtn.setClickable(true);
            showFabBtn.setImageResource(R.drawable.ic_close_white);
            isFabOpen = true;
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            nextButtonClickListener = (NextButtonClickListener) getActivity();
            backButtonClickListener = (BackButtonClickListener) getActivity();
            onLabelAddClickListener = (DrawerMenuListAdapter.OnLabelAddClickListener)getActivity();
            onSaveReportClickListener = (OnSaveReportClickListener)getActivity();
            onGenerateReportClickListener = (OnGenerateReportClickListener)getActivity();

        }catch (ClassCastException ex) {
            ex.printStackTrace();
        }
    }
}
