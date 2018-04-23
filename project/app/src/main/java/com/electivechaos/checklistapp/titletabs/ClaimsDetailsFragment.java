package com.electivechaos.checklistapp.titletabs;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import com.electivechaos.checklistapp.R;

import java.util.Calendar;

/**
 * Created by krishna on 2/26/18.
 */

public class ClaimsDetailsFragment extends Fragment {
    Button insuranceDate = null;
    Calendar calendarInstance = Calendar.getInstance();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.claims_details_fragment, container, false);
    }

    //@Override
   /* public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        insuranceDate = getActivity().findViewById(R.id.input_insurance_date);
        insuranceDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(), date, calendarInstance
                        .get(Calendar.YEAR), calendarInstance.get(Calendar.MONTH),
                        calendarInstance.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        super.onActivityCreated(savedInstanceState);
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendarInstance.set(Calendar.YEAR, year);
            calendarInstance.set(Calendar.MONTH, monthOfYear);
            calendarInstance.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        }

    };*/
}
