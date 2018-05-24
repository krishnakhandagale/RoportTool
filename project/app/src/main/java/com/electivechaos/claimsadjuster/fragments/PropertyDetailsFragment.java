package com.electivechaos.claimsadjuster.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.adapters.CustomMenuAdapter;
import com.electivechaos.claimsadjuster.adapters.DrawerMenuListAdapter;
import com.electivechaos.claimsadjuster.interfaces.NextButtonClickListener;
import com.electivechaos.claimsadjuster.interfaces.OnGenerateReportClickListener;
import com.electivechaos.claimsadjuster.interfaces.OnPropertyDetailsClickListener;
import com.electivechaos.claimsadjuster.interfaces.OnSaveReportClickListener;
import com.electivechaos.claimsadjuster.pojo.PropertyDetailsPOJO;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class PropertyDetailsFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private Button pickTime;
    private int day, month, year, dayFinal, monthFinal, yearFinal;

    private boolean isFabOpen = false;
    private FloatingActionButton showFabBtn, fabGoNextBtn, fabAddLabelBtn, fabGenerateReportBtn, fabSaveReportBtn;
    private Animation fab_open, fab_close;
    private TextView txtDate, menuRoofSystem, menuSiding, menuFoundation;

    private NextButtonClickListener nextButtonClickListener;
    private DrawerMenuListAdapter.OnLabelAddClickListener onLabelAddClickListener;
    private OnSaveReportClickListener onSaveReportClickListener;
    private OnGenerateReportClickListener onGenerateReportClickListener;
    private OnPropertyDetailsClickListener onPropertyDetailsClickListener;

    private  int selectedPositionOne = -1;
    private  int selectedPositionTwo = -1;
    private  int selectedPositionThree = -1;

    private EditText squareFootage;

    private PropertyDetailsPOJO propertyDetailsPOJO;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        propertyDetailsPOJO = getArguments() != null ? (PropertyDetailsPOJO) getArguments().getParcelable("propertyDetails") : new PropertyDetailsPOJO();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_property_details, container, false);
        showFabBtn = view.findViewById(R.id.showFab);
        fabGoNextBtn = view.findViewById(R.id.fabGoNext);
        fabAddLabelBtn = view.findViewById(R.id.fabAddLabel);
        fabGenerateReportBtn = view.findViewById(R.id.fabGenerateReport);
        fabSaveReportBtn = view.findViewById(R.id.fabSaveReport);

        fab_open = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_close);

        pickTime = view.findViewById(R.id.btnDate);
        txtDate = view.findViewById(R.id.txtDate);
        squareFootage = view.findViewById(R.id.squareFootage);
        menuRoofSystem = view.findViewById(R.id.menuOne);
        menuSiding = view.findViewById(R.id.menuTwo);
        menuFoundation = view.findViewById(R.id.menuThree);

        txtDate.setText(propertyDetailsPOJO.getPropertyDate().toString());
        squareFootage.setText(String.valueOf(propertyDetailsPOJO.getSquareFootage()));
        if(propertyDetailsPOJO.getRoofSystem().isEmpty() || propertyDetailsPOJO.getRoofSystem()==null){

        }else {
            menuRoofSystem.setText(propertyDetailsPOJO.getRoofSystem().toString());
        }
        if(propertyDetailsPOJO.getSiding().isEmpty() || propertyDetailsPOJO.getSiding()==null) {

        }else {
            menuSiding.setText(propertyDetailsPOJO.getSiding().toString());
        }
        if(propertyDetailsPOJO.getSiding().isEmpty() || propertyDetailsPOJO.getSiding()==null){

        }else {
            menuFoundation.setText(propertyDetailsPOJO.getFoundation().toString());
        }

        fabGoNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextButtonClickListener.onNextButtonClick();
                animateFAB();
            }
        });
        showFabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFAB();
            }
        });

        fabAddLabelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLabelAddClickListener.onLabelAddClick();
                animateFAB();
            }
        });

        fabGenerateReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGenerateReportClickListener.onReportGenerateClicked();
                animateFAB();
            }
        });

        fabSaveReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveReportClickListener.onReportSave(true);
                animateFAB();
            }
        });

        pickTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                String enteredDate = txtDate.getText().toString().trim();
                if(enteredDate.isEmpty() || enteredDate==null) {

                }else {
                    DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                    Date startDate = null;
                    try {
                        startDate = df.parse(enteredDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    c.setTime(startDate);
                }
                month= c.get(Calendar.MONTH);
                year = c.get(Calendar.YEAR);
                day= c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), PropertyDetailsFragment.this, year, month, day);
                datePickerDialog.show();

            }
        });

        menuRoofSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<String> roofSystemList = new ArrayList<>();
                roofSystemList.add("item1");
                roofSystemList.add("item2");

                final AlertDialog.Builder ad = new AlertDialog.Builder(getContext());
                ad.setCancelable(true);
                ad.setTitle("Roof System");
                CustomMenuAdapter adapter=new CustomMenuAdapter(getContext(),roofSystemList,selectedPositionOne,propertyDetailsPOJO.getRoofSystem().toString());

                ad.setSingleChoiceItems(adapter, selectedPositionOne,  new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        selectedPositionOne =  position;
                        menuRoofSystem.setText(roofSystemList.get(position).toString());
                        onPropertyDetailsClickListener.setPropertyRoofSystem(roofSystemList.get(position).toString());
                        dialogInterface.dismiss();

                    }
                });

                ad.show();

            }
        });

        menuSiding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<String> sidingList = new ArrayList<>();
                sidingList.add("item1");
                sidingList.add("item2");

                final AlertDialog.Builder ad = new AlertDialog.Builder(getContext());
                ad.setCancelable(true);
                ad.setTitle("Siding");
                CustomMenuAdapter adapter=new CustomMenuAdapter(getContext(),sidingList,selectedPositionTwo,propertyDetailsPOJO.getSiding().toString());

                ad.setSingleChoiceItems(adapter, selectedPositionTwo,  new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        selectedPositionTwo =  position;
                        menuSiding.setText(sidingList.get(position).toString());
                        onPropertyDetailsClickListener.setPropertySiding(sidingList.get(position).toString());
                        dialogInterface.dismiss();

                    }
                });

                ad.show();

            }

        });

        menuFoundation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<String> foundationList = new ArrayList<>();
                foundationList.add("123");
                foundationList.add("29");

                final AlertDialog.Builder ad = new AlertDialog.Builder(getContext());
                ad.setCancelable(true);
                ad.setTitle("Foundation");
                CustomMenuAdapter adapter=new CustomMenuAdapter(getContext(),foundationList,selectedPositionThree, propertyDetailsPOJO.getFoundation().toString());

                ad.setSingleChoiceItems(adapter, selectedPositionThree,  new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        selectedPositionThree =  position;
                        menuFoundation.setText(foundationList.get(position).toString());
                        onPropertyDetailsClickListener.setPropertyFoundation(foundationList.get(position).toString());
                        dialogInterface.dismiss();

                    }
                });

                ad.show();
            }
        });

        squareFootage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().trim().isEmpty() || s.toString().trim() == null) {

                }else {
                    try {
                        onPropertyDetailsClickListener.setPropertySquareFootage(Double.parseDouble(s.toString().trim()));
                    } catch (NumberFormatException e) {
                        onPropertyDetailsClickListener.setPropertySquareFootage(0);
                    }

                }
            }
        });

        return view;
    }


    public void animateFAB() {
        if (isFabOpen) {
            showFabBtn.setImageResource(R.drawable.ic_more_vertical_white);
            fabGoNextBtn.startAnimation(fab_close);
            fabAddLabelBtn.startAnimation(fab_close);
            fabGenerateReportBtn.startAnimation(fab_close);
            fabSaveReportBtn.startAnimation(fab_close);
            fabGoNextBtn.setClickable(false);
            fabAddLabelBtn.setClickable(false);
            fabGenerateReportBtn.setClickable(false);
            fabSaveReportBtn.setClickable(false);
            isFabOpen = false;

        } else {
            showFabBtn.setImageResource(R.drawable.ic_close_white);
            fabGoNextBtn.startAnimation(fab_open);
            fabAddLabelBtn.startAnimation(fab_open);
            fabGenerateReportBtn.startAnimation(fab_open);
            fabSaveReportBtn.startAnimation(fab_open);
            fabGoNextBtn.setClickable(true);
            fabAddLabelBtn.setClickable(true);
            fabGenerateReportBtn.setClickable(true);
            fabSaveReportBtn.setClickable(true);
            isFabOpen = true;
        }

    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        yearFinal = i;
        monthFinal = i1 + 1;
        dayFinal = i2;
        txtDate.setText(dayFinal + "/" + monthFinal + "/" + yearFinal);
        onPropertyDetailsClickListener.setPropertyDate(dayFinal + "/" + monthFinal + "/" + yearFinal);

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            nextButtonClickListener = (NextButtonClickListener) getActivity();
            onLabelAddClickListener = (DrawerMenuListAdapter.OnLabelAddClickListener) getActivity();
            onSaveReportClickListener = (OnSaveReportClickListener) getActivity();
            onGenerateReportClickListener = (OnGenerateReportClickListener) getActivity();
            onPropertyDetailsClickListener = (OnPropertyDetailsClickListener) getActivity();
        } catch (ClassCastException ex) {
            ex.printStackTrace();
        }
    }
}
