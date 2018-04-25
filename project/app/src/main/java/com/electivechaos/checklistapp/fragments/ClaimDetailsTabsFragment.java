package com.electivechaos.checklistapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.electivechaos.checklistapp.R;

import org.json.JSONObject;

public class ClaimDetailsTabsFragment extends Fragment {


   // ClaimDetailsTabsFragment.SendImageDetails sendImageDetails;
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
    public static ClaimDetailsTabsFragment initFragment(String rTitle,String rDescription, String cName, String cNumber,String addr){
        Bundle args = new Bundle();

        args.putString("reportTitle", rTitle);
        args.putString("reportDescription",rDescription);
        args.putString("clientName",cName);
        args.putString("claimNumber", cNumber);
        args.putString("address", addr);
        ClaimDetailsTabsFragment fragment = new ClaimDetailsTabsFragment();
        fragment.setArguments(args);
        return fragment;
    };

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
        return view;
    }

   /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            sendImageDetails = (ClaimDetailsTabsFragment.SendImageDetails) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Error in retrieving data. Please try again");
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

    public interface SendImageDetails {
        void sendData(JSONObject message);
    }*/
}
