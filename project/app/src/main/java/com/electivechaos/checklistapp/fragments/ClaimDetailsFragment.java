package com.electivechaos.checklistapp.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.electivechaos.checklistapp.MainActivity;
import com.electivechaos.checklistapp.R;
import com.electivechaos.checklistapp.adapters.SimpleTabPagerAdapter;
import com.electivechaos.checklistapp.adapters.TitleFrgamentTabPagerAdapter;


public class ClaimDetailsFragment extends Fragment {

    View v;
    ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.claim_details_layout, container, false);
        TabLayout tabLayout = v.findViewById(R.id.sliding_tabs);
        tabLayout.addTab(tabLayout.newTab().setText("Claims Details"));
        tabLayout.addTab(tabLayout.newTab().setText("Loss Location"));
        tabLayout.addTab(tabLayout.newTab().setText("Cause Of Loss"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager =v.findViewById(R.id.viewpager);
        final PagerAdapter adapter = new SimpleTabPagerAdapter(getActivity().getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        return v;

    }

}
