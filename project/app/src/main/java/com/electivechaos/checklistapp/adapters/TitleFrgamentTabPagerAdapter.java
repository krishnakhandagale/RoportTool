package com.electivechaos.checklistapp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.electivechaos.checklistapp.titletabs.ClaimsDetailsFragment;
import com.electivechaos.checklistapp.titletabs.LossLocationFragment;

/**
 * Created by krishna on 2/26/18.
 */

public class TitleFrgamentTabPagerAdapter extends FragmentStatePagerAdapter {

   int mNumOfTabs;
    public TitleFrgamentTabPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                ClaimsDetailsFragment claimsDetailsFragment = new ClaimsDetailsFragment();
                return claimsDetailsFragment;
            case 1:
                LossLocationFragment lossLocationFragment = new LossLocationFragment();
                return lossLocationFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return  mNumOfTabs;
    }
}