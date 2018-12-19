package com.electivechaos.claimsadjuster.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.interfaces.ClaimDetailsDataInterface;
import com.electivechaos.claimsadjuster.utils.CommonUtils;

public class ClaimDetailsTabsFragment extends Fragment {

    private EditText reportTitleEditText;
    private EditText reportDescriptionEditText;
    private EditText clientNameEditText;
    private EditText claimNumberEditText;
    private EditText reportByEditText;


    private String reportTitle;
    private String reportDescription;
    private String clientName;
    private String claimNumber;
    private String reportBy;


    private ClaimDetailsDataInterface claimDetailsDataInterface;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        reportTitle = getArguments() != null ? getArguments().getString("reportTitle") : "";
        reportDescription = getArguments() != null ? getArguments().getString("reportDescription") : "";
        clientName = getArguments() != null ? getArguments().getString("clientName") : "";
        claimNumber = getArguments() != null ? getArguments().getString("claimNumber") : "";
        reportBy = getArguments() != null ? getArguments().getString("reportBy") : "";

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_claim_details, container, false);

        reportTitleEditText = view.findViewById(R.id.reportTitle);
        reportDescriptionEditText = view.findViewById(R.id.reportDescription);
        clientNameEditText = view.findViewById(R.id.insuredName);
        claimNumberEditText = view.findViewById(R.id.claimNumber);
        reportByEditText = view.findViewById(R.id.reportBy);

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


        reportByEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                /*if(isTooLarge(reportDescriptionEditText,s.toString())){

                }*/
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                claimDetailsDataInterface.setReportBy(s.toString().trim());
            }
        });


        return view;
    }

    private boolean isTooLarge(EditText text, String newText) {
        float textWidth = text.getPaint().measureText(newText);
        return (textWidth >= text.getMeasuredWidth());
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            claimDetailsDataInterface = (ClaimDetailsDataInterface) getActivity();
        } catch (ClassCastException exception) {
            exception.printStackTrace();
        }


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (TextUtils.isEmpty(reportTitle)) {
            reportTitleEditText.setText(CommonUtils.getReportTitle(getActivity()).toString());
        } else {
            reportTitleEditText.setText(reportTitle);
        }

        if (TextUtils.isEmpty(reportDescription)) {
            reportDescriptionEditText.setText(CommonUtils.getReportDescription(getActivity()).toString());
        } else {
            reportDescriptionEditText.setText(reportDescription);
        }

        if (TextUtils.isEmpty(reportBy)) {
            reportByEditText.setText(CommonUtils.getReportByField(getActivity()).toString());
        } else {
            reportByEditText.setText(reportBy);
        }
        clientNameEditText.setText(clientName);
        claimNumberEditText.setText(claimNumber);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        super.onSaveInstanceState(outState);

    }


}
