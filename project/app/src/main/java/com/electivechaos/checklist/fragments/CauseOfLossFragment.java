package com.electivechaos.checklist.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.electivechaos.checklist.R;

public class CauseOfLossFragment extends Fragment {
    private String value;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /*Bundle bundle=getArguments();
        if(bundle!=null) {
            value = getArguments().getString("message");
            Toast.makeText(getContext(),""+value,Toast.LENGTH_LONG).show();
        }*/

        return inflater.inflate(R.layout.fragment_cause_of_loss, container, false);
    }

}
