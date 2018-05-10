package com.electivechaos.checklistapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.electivechaos.checklistapp.R;
import com.electivechaos.checklistapp.interfaces.ClaimDetailsDataInterface;

public class ClaimDetailsTabsFragment extends Fragment {

    private EditText reportTitleEditText;
    private EditText reportDescriptionEditText;
    private EditText clientNameEditText ;
    private EditText claimNumberEditText;



    private String reportTitle;
    private String reportDescription;
    private String clientName;
    private String claimNumber;

    private Boolean isFabOpen = false;
    private FloatingActionButton add_button,goToNextBtn, fab2, fab3, fab4;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;


    private ClaimDetailsDataInterface claimDetailsDataInterface;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        reportTitle = getArguments() != null ? getArguments().getString("reportTitle") : "";
        reportDescription = getArguments() != null ? getArguments().getString("reportDescription") : "";
        clientName = getArguments() != null ? getArguments().getString("clientName") : "";
        claimNumber = getArguments() != null ? getArguments().getString("claimNumber") : "";

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_claim_details, container, false);
        add_button = (FloatingActionButton) view.findViewById(R.id.show_fab);
        goToNextBtn = (FloatingActionButton)view. findViewById(R.id.goToNext);
        fab2 = (FloatingActionButton)view. findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) view.findViewById(R.id.fab3);
        fab4 = (FloatingActionButton) view.findViewById(R.id.fab4);

        fab_open = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_backward);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFAB();
            }
        });
        reportTitleEditText = view.findViewById(R.id.reportTitle);
        reportDescriptionEditText =view.findViewById(R.id.reportDescription);
        clientNameEditText = view.findViewById(R.id.clientName);
        claimNumberEditText = view.findViewById(R.id.claimNumber);



        reportTitleEditText.setText(reportTitle);
        reportDescriptionEditText.setText(reportDescription);
        clientNameEditText.setText(clientName);
        claimNumberEditText.setText(claimNumber);



        reportTitleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                claimDetailsDataInterface.setReportTitle(s.toString().trim());
            }
        });

        reportDescriptionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                claimDetailsDataInterface.setReportDescription(s.toString().trim());
            }
        });


        clientNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                claimDetailsDataInterface.setReportClientName(s.toString().trim());
            }
        });


        claimNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                claimDetailsDataInterface.setReportClaimNumber(s.toString().trim());
            }
        });

        return view;
    }

   @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            claimDetailsDataInterface = (ClaimDetailsDataInterface)getActivity();
        }catch (ClassCastException exception){
            exception.printStackTrace();
        }


    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        reportTitleEditText.setText(reportTitle);
        reportDescriptionEditText.setText(reportDescription);
        clientNameEditText.setText(clientName);
        claimNumberEditText.setText(claimNumber);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        super.onSaveInstanceState(outState);

    }
    public void animateFAB() {
        CoordinatorLayout.LayoutParams layoutParams = new CoordinatorLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);


        if (isFabOpen) {
            add_button.startAnimation(rotate_backward);
            goToNextBtn.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab3.startAnimation(fab_close);
            fab4.startAnimation(fab_close);
            fab2.setClickable(false);
            fab3.setClickable(false);
            fab4.setClickable(false);
            isFabOpen = false;

        } else {
            add_button.startAnimation(rotate_forward);
            goToNextBtn.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab3.startAnimation(fab_open);
            fab4.startAnimation(fab_open);
            goToNextBtn.setClickable(true);
            fab2.setClickable(true);
            fab3.setClickable(true);
            fab4.setClickable(true);
            isFabOpen = true;
        }

    }

}
