package com.electivechaos.checklistapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.electivechaos.checklistapp.R;
import com.electivechaos.checklistapp.interfaces.ClaimDetailsDataInterface;

public class ClaimDetailsTabsFragment extends Fragment {

    private EditText reportTitleEditText;
    private EditText reportDescriptionEditText;
    private EditText clientNameEditText ;
    private EditText claimNumberEditText;
    private EditText addressEditText;


    private String reportTitle;
    private String reportDescription;
    private String clientName;
    private String claimNumber;
    private String address;

    private ClaimDetailsDataInterface claimDetailsDataInterface;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        reportTitle = getArguments() != null ? getArguments().getString("reportTitle") : "";
        reportDescription = getArguments() != null ? getArguments().getString("reportDescription") : "";
        clientName = getArguments() != null ? getArguments().getString("clientName") : "";
        claimNumber = getArguments() != null ? getArguments().getString("claimNumber") : "";
        address = getArguments() != null ? getArguments().getString("address") : "";
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_claim_details, container, false);
        reportTitleEditText = view.findViewById(R.id.reportTitle);
        reportDescriptionEditText =view.findViewById(R.id.reportDescription);
        clientNameEditText = view.findViewById(R.id.clientName);
        claimNumberEditText = view.findViewById(R.id.claimNumber);
        addressEditText = view.findViewById(R.id.address);


        reportTitleEditText.setText(reportTitle);
        reportDescriptionEditText.setText(reportDescription);
        clientNameEditText.setText(clientName);
        claimNumberEditText.setText(claimNumber);
        addressEditText.setText(address);


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

        addressEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                claimDetailsDataInterface.setReportAddress(s.toString().trim());
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
        addressEditText.setText(address);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        super.onSaveInstanceState(outState);

    }
}
