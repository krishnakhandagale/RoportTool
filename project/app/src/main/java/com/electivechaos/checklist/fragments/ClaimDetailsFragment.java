package com.electivechaos.checklist.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.electivechaos.checklist.MainActivity;
import com.electivechaos.checklist.R;


public class ClaimDetailsFragment extends Fragment {

    private FloatingActionButton floatingActionButton;
    private EditText text1;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v= inflater.inflate(R.layout.fragment_claim_details, container, false);
       /* floatingActionButton=v.findViewById(R.id.floating_btn_one);
        text1=v.findViewById(R.id.demo_claim);


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CauseOfLossFragment fragment1=new CauseOfLossFragment();
                Bundle bundle=new Bundle();
                bundle.putString("message",text1.getText().toString());
                fragment1.setArguments(bundle);
                Toast.makeText(getContext(),""+text1.getText().toString(),Toast.LENGTH_LONG).show();

                //Inflate the fragment
                getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment1).commit();

            }
        });*/

        return v;
    }



}
