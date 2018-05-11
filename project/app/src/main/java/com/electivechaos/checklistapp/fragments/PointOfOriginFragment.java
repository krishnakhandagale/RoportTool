package com.electivechaos.checklistapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.electivechaos.checklistapp.R;


public class PointOfOriginFragment extends Fragment {

    private Boolean isFabOpen = false;
    private FloatingActionButton showFabBtn,fabGoNextBtn, fabAddImagesBtn, fabAddLabelBtn, fabGenerateReportBtn;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_point_of_origin, container, false);

        return view;
    }

    public void animateFAB() {
        if (isFabOpen) {
            showFabBtn.startAnimation(rotate_backward);
            fabGoNextBtn.startAnimation(fab_close);
            fabAddImagesBtn.startAnimation(fab_close);
            fabAddLabelBtn.startAnimation(fab_close);
            fabGenerateReportBtn.startAnimation(fab_close);
            fabGoNextBtn.setClickable(false);
            fabAddImagesBtn.setClickable(false);
            fabAddLabelBtn.setClickable(false);
            fabGenerateReportBtn.setClickable(false);
            isFabOpen = false;

        } else {
            showFabBtn.startAnimation(rotate_forward);
            fabGoNextBtn.startAnimation(fab_open);
            fabAddImagesBtn.startAnimation(fab_open);
            fabAddLabelBtn.startAnimation(fab_open);
            fabGenerateReportBtn.startAnimation(fab_open);
            fabGoNextBtn.setClickable(true);
            fabAddImagesBtn.setClickable(true);
            fabAddLabelBtn.setClickable(true);
            fabGenerateReportBtn.setClickable(true);
            isFabOpen = true;
        }

    }

}
