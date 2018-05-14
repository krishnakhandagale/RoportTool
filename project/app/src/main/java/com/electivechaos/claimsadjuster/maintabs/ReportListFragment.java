package com.electivechaos.claimsadjuster.maintabs;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.ui.AddEditReportActivity;

public class ReportListFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.report_list_fragment, container, false);

        FloatingActionButton  btnAddReport = view.findViewById(R.id.btnAddReport);
        btnAddReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent addReportActivity = new Intent(getActivity(), AddEditReportActivity.class);
                startActivity(addReportActivity);
            }
        });

        return view;
    }
}
