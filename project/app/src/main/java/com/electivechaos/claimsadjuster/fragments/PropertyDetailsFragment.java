package com.electivechaos.claimsadjuster.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import com.electivechaos.claimsadjuster.asynctasks.DBPropertyDetailsListTsk;
import com.electivechaos.claimsadjuster.database.CategoryListDBHelper;
import com.electivechaos.claimsadjuster.interfaces.AsyncTaskStatusCallback;
import com.electivechaos.claimsadjuster.interfaces.NextButtonClickListener;
import com.electivechaos.claimsadjuster.interfaces.OnGenerateReportClickListener;
import com.electivechaos.claimsadjuster.interfaces.OnPropertyDetailsClickListener;
import com.electivechaos.claimsadjuster.interfaces.OnSaveReportClickListener;
import com.electivechaos.claimsadjuster.pojo.BuildingTypePOJO;
import com.electivechaos.claimsadjuster.pojo.FoundationPOJO;
import com.electivechaos.claimsadjuster.pojo.PropertyDetailsPOJO;
import com.electivechaos.claimsadjuster.pojo.RoofSystemPOJO;
import com.electivechaos.claimsadjuster.pojo.SidingPOJO;
import com.electivechaos.claimsadjuster.ui.BuildingTypeActivity;
import com.electivechaos.claimsadjuster.ui.FoundationActivity;
import com.electivechaos.claimsadjuster.ui.RoofSystemActivity;
import com.electivechaos.claimsadjuster.ui.SidingActivity;
import com.electivechaos.claimsadjuster.utils.CommonUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class PropertyDetailsFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private int day;
    private int month;
    private int year;

    private boolean isFabOpen = false;
    private FloatingActionButton showFabBtn, fabGoNextBtn, fabAddLabelBtn, fabGenerateReportBtn, fabSaveReportBtn;
    private Animation fab_open, fab_close;
    private TextView txtDate, menuRoofSystem, menuSiding, menuFoundation, menuBuildingType;

    private NextButtonClickListener nextButtonClickListener;
    private DrawerMenuListAdapter.OnLabelAddClickListener onLabelAddClickListener;
    private OnSaveReportClickListener onSaveReportClickListener;
    private OnGenerateReportClickListener onGenerateReportClickListener;
    private OnPropertyDetailsClickListener onPropertyDetailsClickListener;


    private PropertyDetailsPOJO propertyDetailsPOJO;

    private View progressBarLayout;
    private CategoryListDBHelper categoryListDBHelper;

    @Override
    public void onStart() {
        super.onStart();
        categoryListDBHelper = CategoryListDBHelper.getInstance(getActivity());
    }

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

        Button pickTime = view.findViewById(R.id.btnDate);
        txtDate = view.findViewById(R.id.txtDate);
        EditText squareFootage = view.findViewById(R.id.squareFootage);
        menuRoofSystem = view.findViewById(R.id.menuOne);
        menuSiding = view.findViewById(R.id.menuTwo);
        menuFoundation = view.findViewById(R.id.menuThree);
        menuBuildingType = view.findViewById(R.id.menuFour);
        progressBarLayout = view.findViewById(R.id.progressBarLayout);


        txtDate.setText(propertyDetailsPOJO.getPropertyDate());
        String squareFootageValue = String.format("%.2f", propertyDetailsPOJO.getSquareFootage());
        squareFootage.setText(squareFootageValue);

        if (propertyDetailsPOJO != null) {
            if (!propertyDetailsPOJO.getRoofSystem().isEmpty()) {
                menuRoofSystem.setText(propertyDetailsPOJO.getRoofSystem());
            }
            if (!propertyDetailsPOJO.getSiding().isEmpty()) {
                menuSiding.setText(propertyDetailsPOJO.getSiding());
            }
            if (!propertyDetailsPOJO.getFoundation().isEmpty()) {
                menuFoundation.setText(propertyDetailsPOJO.getFoundation());
            }
            if (!propertyDetailsPOJO.getBuildingType().isEmpty()) {
                menuBuildingType.setText(propertyDetailsPOJO.getBuildingType());
            }
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
                if (enteredDate.isEmpty() || enteredDate == null) {

                } else {
                    DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                    Date startDate = null;
                    try {
                        startDate = df.parse(enteredDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    c.setTime(startDate);
                }
                month = c.get(Calendar.MONTH);
                year = c.get(Calendar.YEAR);
                day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), PropertyDetailsFragment.this, year, month, day);
                datePickerDialog.show();

            }
        });

        menuRoofSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new DBPropertyDetailsListTsk(categoryListDBHelper, "roof_system", new AsyncTaskStatusCallback() {
                    @Override
                    public void onPostExecute(Object object, String type) {

                        final ArrayList<RoofSystemPOJO> roofSystemList = (ArrayList<RoofSystemPOJO>) object;
                        RoofSystemPOJO addNew = new RoofSystemPOJO();
                        addNew.setName("Add New");
                        roofSystemList.add(addNew);

                        final AlertDialog.Builder ad = new AlertDialog.Builder(getContext());
                        ad.setCancelable(true);
                        ad.setTitle("Roof System");
                        CustomMenuAdapter adapter = new CustomMenuAdapter(getContext(), roofSystemList, propertyDetailsPOJO.getRoofSystem().toString(), "roof_system");

                        ad.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int position) {
                                if(roofSystemList.get(position).getName().toString().equalsIgnoreCase("Add New")) {
                                    Intent intent = new Intent(getContext(), RoofSystemActivity.class);
                                    startActivityForResult(intent, 30);

                                }else {
                                    menuRoofSystem.setText(roofSystemList.get(position).getName().toString());
                                    onPropertyDetailsClickListener.setPropertyRoofSystem(roofSystemList.get(position).getName().toString());
                                }
                                dialogInterface.dismiss();

                            }
                        });

                        ad.show();
                        CommonUtils.unlockOrientation(getActivity());
                        progressBarLayout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onPreExecute() {
                        CommonUtils.lockOrientation(getActivity());
                        progressBarLayout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onProgress(int progress) {

                    }
                }).execute();


            }
        });

        menuSiding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DBPropertyDetailsListTsk(categoryListDBHelper, "siding", new AsyncTaskStatusCallback() {

                    @Override
                    public void onPostExecute(Object object, String type) {
                        final ArrayList<SidingPOJO> sidingList = (ArrayList<SidingPOJO>) object;
                        SidingPOJO addNew = new SidingPOJO();
                        addNew.setName("Add New");
                        sidingList.add(addNew);

                        final AlertDialog.Builder ad = new AlertDialog.Builder(getContext());
                        ad.setCancelable(true);
                        ad.setTitle("Siding");
                        CustomMenuAdapter adapter = new CustomMenuAdapter(getContext(), sidingList, propertyDetailsPOJO.getSiding().toString(), "siding");

                        ad.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int position) {
                                if(sidingList.get(position).getName().toString().equalsIgnoreCase("Add New")) {
                                    Intent intent = new Intent(getContext(), SidingActivity.class);
                                    startActivityForResult(intent,31);
                                }else {
                                    menuSiding.setText(sidingList.get(position).getName().toString());
                                    onPropertyDetailsClickListener.setPropertySiding(sidingList.get(position).getName().toString());
                                }
                                dialogInterface.dismiss();

                            }
                        });

                        ad.show();
                        CommonUtils.unlockOrientation(getActivity());
                        progressBarLayout.setVisibility(View.GONE);

                    }

                    @Override
                    public void onPreExecute() {

                        CommonUtils.lockOrientation(getActivity());
                        progressBarLayout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onProgress(int progress) {

                    }
                }).execute();
            }
        });


        menuFoundation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                new DBPropertyDetailsListTsk(categoryListDBHelper, "foundation", new AsyncTaskStatusCallback() {
                    @Override
                    public void onPostExecute(Object object, String type) {

                        final ArrayList<FoundationPOJO> foundationList = (ArrayList<FoundationPOJO>) object;
                        FoundationPOJO addNew = new FoundationPOJO();
                        addNew.setName("Add New");
                        foundationList.add(addNew);

                        final AlertDialog.Builder ad = new AlertDialog.Builder(getContext());
                        ad.setCancelable(true);
                        ad.setTitle("Foundation");
                        CustomMenuAdapter adapter = new CustomMenuAdapter(getContext(), foundationList, propertyDetailsPOJO.getFoundation().toString(), "foundation");

                        ad.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int position) {
                                if(foundationList.get(position).getName().toString().equalsIgnoreCase("Add New")) {
                                    Intent intent = new Intent(getContext(), FoundationActivity.class);
                                    startActivityForResult(intent,32);
                                }else {
                                    menuFoundation.setText(foundationList.get(position).getName().toString());
                                    onPropertyDetailsClickListener.setPropertyFoundation(foundationList.get(position).getName().toString());
                                }
                                dialogInterface.dismiss();

                            }
                        });

                        ad.show();
                        CommonUtils.unlockOrientation(getActivity());
                        progressBarLayout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onPreExecute() {
                        CommonUtils.lockOrientation(getActivity());
                        progressBarLayout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onProgress(int progress) {

                    }
                }).execute();
            }
        });

        menuBuildingType.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                new DBPropertyDetailsListTsk(categoryListDBHelper, "building_type", new AsyncTaskStatusCallback() {
                    @Override
                    public void onPostExecute(Object object, String type) {

                        final ArrayList<BuildingTypePOJO> buildingTypeList = (ArrayList<BuildingTypePOJO>) object;
                        BuildingTypePOJO addNew = new BuildingTypePOJO();
                        addNew.setName("Add New");
                        buildingTypeList.add(addNew);

                        final AlertDialog.Builder ad = new AlertDialog.Builder(getContext());
                        ad.setCancelable(true);
                        ad.setTitle("Building Type");
                        CustomMenuAdapter adapter = new CustomMenuAdapter(getContext(), buildingTypeList, propertyDetailsPOJO.getBuildingType().toString(), "building_type");

                        ad.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int position) {
                                if(buildingTypeList.get(position).getName().toString().equalsIgnoreCase("Add New")) {
                                    Intent intent = new Intent(getContext(), BuildingTypeActivity.class);
                                    startActivityForResult(intent,33);
                                }else {
                                    menuBuildingType.setText(buildingTypeList.get(position).getName().toString());
                                    onPropertyDetailsClickListener.setPropertyBuildingType(buildingTypeList.get(position).getName().toString());
                                }

                                dialogInterface.dismiss();

                            }
                        });

                        ad.show();
                        CommonUtils.unlockOrientation(getActivity());
                        progressBarLayout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onPreExecute() {
                        CommonUtils.lockOrientation(getActivity());
                        progressBarLayout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onProgress(int progress) {

                    }
                }).execute();
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
                String tempString = s.toString().trim();
                if (!tempString.isEmpty()) {
                    onPropertyDetailsClickListener.setPropertySquareFootage(Double.parseDouble(tempString));
                } else {
                    onPropertyDetailsClickListener.setPropertySquareFootage(0.0);
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
        int yearFinal = i;
        int monthFinal = i1 + 1;
        int dayFinal = i2;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 30) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle dataFromActivity = data.getExtras().getBundle("roofSystemDetails");
                String roofName = dataFromActivity.get("roofSystemName").toString();
                RoofSystemPOJO roofSystemPOJO = new RoofSystemPOJO();
                roofSystemPOJO.setName(roofName);

                long i = categoryListDBHelper.addRoofSystem(roofSystemPOJO);
                if(i == -111){
                    Toast.makeText(getContext(),"Roof System with same name already exists.",Toast.LENGTH_LONG).show();
                }
                menuRoofSystem.setText(roofName);
                onPropertyDetailsClickListener.setPropertyRoofSystem(roofName);

            }
        }
        else if(requestCode == 31){
            if (resultCode == Activity.RESULT_OK) {
                Bundle dataFromActivity = data.getExtras().getBundle("sidingDetails");
                String sidingName = dataFromActivity.get("sidingName").toString();
                SidingPOJO sidingPOJO = new SidingPOJO();
                sidingPOJO.setName(sidingName);

                long i = categoryListDBHelper.addSiding(sidingPOJO);
                if(i == -111){
                    Toast.makeText(getContext(),"Siding with same name already exists.",Toast.LENGTH_LONG).show();
                }
                menuSiding.setText(sidingName);
                onPropertyDetailsClickListener.setPropertySiding(sidingName);
            }
        }
        else if(requestCode == 32){
            if (resultCode == Activity.RESULT_OK) {
                Bundle dataFromActivity = data.getExtras().getBundle("foundationDetails");
                String foundationName = dataFromActivity.get("foundationName").toString();
                FoundationPOJO foundationPOJO = new FoundationPOJO();
                foundationPOJO.setName(foundationName);

               long i = categoryListDBHelper.addFoundation(foundationPOJO);
                if(i == -111){
                    Toast.makeText(getContext(),"Foundation with same name already exists.",Toast.LENGTH_LONG).show();
                }
                menuFoundation.setText(foundationName);
                onPropertyDetailsClickListener.setPropertyFoundation(foundationName);

            }
        }
        else if(requestCode == 33){
            if (resultCode == Activity.RESULT_OK) {
                Bundle dataFromActivity = data.getExtras().getBundle("buildingDetails");
                String buildingName = dataFromActivity.get("buildingName").toString();
                BuildingTypePOJO buildingTypePOJO = new BuildingTypePOJO();
                buildingTypePOJO.setName(buildingName);

                long i = categoryListDBHelper.addBuildingType(buildingTypePOJO);
                if(i == -111){
                    Toast.makeText(getContext(),"Building type with same name already exists.",Toast.LENGTH_LONG).show();
                }
                menuBuildingType.setText(buildingName);
                onPropertyDetailsClickListener.setPropertyBuildingType(buildingName);

            }
        }
    }
}
