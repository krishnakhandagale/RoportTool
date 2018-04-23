package com.electivechaos.checklist.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.electivechaos.checklist.fragments.ClaimDetailsFragment;
import com.electivechaos.checklist.fragments.InspectionFragment;

/**
 * Created by nafeea on 4/20/18.
 */

public class SimpleTabPagerAdapter extends FragmentPagerAdapter {

    private int NumOfTabs;

    public SimpleTabPagerAdapter( FragmentManager fm,int NumOfTabs) {
        super(fm);
        this.NumOfTabs=NumOfTabs;

    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new InspectionFragment();
        } else if (position == 1){
            return new InspectionFragment();
        } else{
            return new InspectionFragment();
        }

    }



    @Override
    public int getCount() {
        return NumOfTabs;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0)
        {
            title = "Tab-1";
        }
        else if (position == 1)
        {
            title = "Tab-2";
        }
        else if (position == 2)
        {
            title = "Tab-3";
        }
        return title;
    }
}
