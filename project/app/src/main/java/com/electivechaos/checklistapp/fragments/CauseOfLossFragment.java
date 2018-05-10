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

public class CauseOfLossFragment extends Fragment {

    private Boolean isFabOpen = false;
    private FloatingActionButton showFabBtn,fabGoNextBtn, fabAddImagesBtn, fabAddLabelBtn, fabGenerateReportBtn;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_cause_of_loss, container, false);
        showFabBtn = (FloatingActionButton) view.findViewById(R.id.showFab);
        fabGoNextBtn = (FloatingActionButton)view. findViewById(R.id.fabGoNext);
        fabAddLabelBtn = (FloatingActionButton)view. findViewById(R.id.fabAddLabel);
        fabAddImagesBtn = (FloatingActionButton) view.findViewById(R.id.fabAddImages);
        fabGenerateReportBtn = (FloatingActionButton) view.findViewById(R.id.fabGenerateReport);

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

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

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
