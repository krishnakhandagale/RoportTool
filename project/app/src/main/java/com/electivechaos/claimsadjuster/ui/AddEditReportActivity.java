package com.electivechaos.claimsadjuster.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.adapters.CustomCategoryPopUpAdapter;
import com.electivechaos.claimsadjuster.adapters.DrawerMenuListAdapter;
import com.electivechaos.claimsadjuster.asynctasks.DBSelectedImagesTask;
import com.electivechaos.claimsadjuster.asynctasks.DBUpdateFilePath;
import com.electivechaos.claimsadjuster.asynctasks.DBUpdateTaskOnTextChanged;
import com.electivechaos.claimsadjuster.asynctasks.DatabaseSaveReportTask;
import com.electivechaos.claimsadjuster.database.CategoryListDBHelper;
import com.electivechaos.claimsadjuster.fragments.AddEditReportSelectedImagesFragment;
import com.electivechaos.claimsadjuster.fragments.CauseOfLossFragment;
import com.electivechaos.claimsadjuster.fragments.ClaimDetailsFragment;
import com.electivechaos.claimsadjuster.fragments.PointOfOriginFragment;
import com.electivechaos.claimsadjuster.interfaces.AddEditLabelInterface;
import com.electivechaos.claimsadjuster.interfaces.ClaimDetailsDataInterface;
import com.electivechaos.claimsadjuster.interfaces.LossLocationDataInterface;
import com.electivechaos.claimsadjuster.interfaces.NextButtonClickListener;
import com.electivechaos.claimsadjuster.interfaces.OnGenerateReportClickListener;
import com.electivechaos.claimsadjuster.interfaces.OnSaveReportClickListener;
import com.electivechaos.claimsadjuster.interfaces.SelectedImagesDataInterface;
import com.electivechaos.claimsadjuster.pojo.Category;
import com.electivechaos.claimsadjuster.pojo.ImageDetailsPOJO;
import com.electivechaos.claimsadjuster.pojo.Label;
import com.electivechaos.claimsadjuster.pojo.ReportPOJO;
import com.electivechaos.claimsadjuster.utils.CommonUtils;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AddEditReportActivity extends AppCompatActivity implements DrawerMenuListAdapter.OnLabelAddClickListener, AddEditLabelInterface, ClaimDetailsDataInterface, LossLocationDataInterface,SelectedImagesDataInterface,NextButtonClickListener,OnSaveReportClickListener, OnGenerateReportClickListener{
    private DrawerLayout mDrawerLayout;
    private ExpandableListView mExpandableListView;
    private DrawerMenuListAdapter drawerMenuListAdapter;
    private String tabName;

    private HashMap<String, List<Label>> childMenuItems = new HashMap<>();
    private List<Label> inspectionChildMenu = new ArrayList<>();
    private ArrayList<String> parentMenuItems;

    private int selectedFragmentPosition = 0;

    private ArrayList<Category> categories = null;
    static CategoryListDBHelper categoryListDBHelper;

    private ReportPOJO reportPOJO ;
    private View progressBarLayout;

    private  Toolbar toolbar;
    private MenuItem actionBarEditBtn;
    private ActionBar activityActionBar;
    private View parentLayoutForMessages;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_edit_report_activity_layout);

        parentLayoutForMessages = findViewById(R.id.parentLayoutForMessages);
        progressBarLayout = findViewById(R.id.progressBarLayout);
        categoryListDBHelper = CategoryListDBHelper.getInstance(this);

        if(getIntent().getExtras() != null){
            reportPOJO = categoryListDBHelper.getReportItem(getIntent().getExtras().getString("reportId"));
        }else{
            reportPOJO = new ReportPOJO();
            reportPOJO.setId(String.valueOf(new Date().getTime()));

            DateFormat myFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
            String reportSavedDate = myFormat.format(new Date().getTime());

            setCreatedDate(reportSavedDate);
            onReportSave(false);
        }

        selectedFragmentPosition = 0;

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        activityActionBar = getSupportActionBar();
        activityActionBar.setTitle("Claim Details");


        activityActionBar.setDisplayHomeAsUpEnabled(true);
        activityActionBar.setHomeAsUpIndicator(R.drawable.ic_menu);


        mExpandableListView = findViewById(R.id.slider_menu);


        putClaimDetailsFragment();
        parentMenuItems = new ArrayList<>();

        parentMenuItems.add("Claim Details");
        parentMenuItems.add("Cause Of Loss");
        parentMenuItems.add("Point Of Origin");
        parentMenuItems.add("Inspection");


        inspectionChildMenu = (List<Label>) reportPOJO.getLabelArrayList().clone();
        childMenuItems.put("Inspection", inspectionChildMenu);


        mDrawerLayout = findViewById(R.id.drawer_layout);

        if (parentMenuItems != null && parentMenuItems.size() > 0) {
            drawerMenuListAdapter = new DrawerMenuListAdapter(this, parentMenuItems, childMenuItems);
            mExpandableListView.setAdapter(drawerMenuListAdapter);
            mExpandableListView.setIndicatorBounds(0, 20);
        }
        mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                if (parentMenuItems.get(groupPosition).equals("Claim Details")) {

                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    putClaimDetailsFragment();
                    actionBarEditBtn.setVisible(false);


                } else if (parentMenuItems.get(groupPosition).equals("Cause Of Loss")) {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    FragmentManager transactionManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
                    fragmentTransaction.replace(R.id.content_frame, new CauseOfLossFragment());
                    fragmentTransaction.commit();
                    tabName = "CauseOfLossFragment";

                    selectedFragmentPosition = 1;
                    activityActionBar.setTitle("Cause Of Loss");
                    actionBarEditBtn.setVisible(false);

                } else if (parentMenuItems.get(groupPosition).equals("Point Of Origin")) {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    FragmentManager transactionManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
                    fragmentTransaction.replace(R.id.content_frame, new PointOfOriginFragment());
                    fragmentTransaction.commit();
                    tabName = "PointOfOriginFragment";

                    selectedFragmentPosition = 2;

                    activityActionBar.setTitle("Point Of Origin");
                    actionBarEditBtn.setVisible(false);
                }
                return false;
            }
        });


        mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                mDrawerLayout.closeDrawer(Gravity.LEFT);
                FragmentManager transactionManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
                AddEditReportSelectedImagesFragment addEditReportSelectedImagesFragment = AddEditReportSelectedImagesFragment.initFragment(reportPOJO.getLabelArrayList().get(childPosition).getSelectedImages(), reportPOJO.getLabelArrayList().get(childPosition).getSelectedElevationImages(), childPosition);
                fragmentTransaction.replace(R.id.content_frame, addEditReportSelectedImagesFragment);
                fragmentTransaction.commit();
                selectedFragmentPosition = childPosition + 3;
                tabName = "AddEditReportSelectedImagesFragment";
                activityActionBar.setTitle(reportPOJO.getLabelArrayList().get(childPosition).getName());
                actionBarEditBtn.setVisible(true);
                toolbar.setTag(reportPOJO.getLabelArrayList().get(childPosition));
                return false;
            }
        });


    }

    private void putClaimDetailsFragment() {
        FragmentManager transactionManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();

        ClaimDetailsFragment claimDetailsFragment = new ClaimDetailsFragment();
        Bundle claimDetailsData = new Bundle();

        claimDetailsData.putString("reportTitle", reportPOJO.getReportTitle());
        claimDetailsData.putString("reportDescription", reportPOJO.getReportDescription());
        claimDetailsData.putString("claimNumber", reportPOJO.getClaimNumber());
        claimDetailsData.putString("clientName", reportPOJO.getClientName());
        claimDetailsData.putString("createdDate", reportPOJO.getCreatedDate());
        claimDetailsData.putString("locationLat", reportPOJO.getLocationLat());
        claimDetailsData.putString("locationLong", reportPOJO.getLocationLong());
        claimDetailsData.putString("addressLine", reportPOJO.getAddressLine());

        claimDetailsFragment.setArguments(claimDetailsData);
        fragmentTransaction.replace(R.id.content_frame, claimDetailsFragment);
        fragmentTransaction.commit();
        tabName = "ClaimDetailsFragment";


        selectedFragmentPosition = 0;
        activityActionBar.setTitle("Claim Details");
    }


    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                CommonUtils.hideKeyboard(AddEditReportActivity.this);
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.edit:

                View view = getLayoutInflater().inflate(R.layout.edit_label_actionbar_layout, null);
                final EditText editLabel=view.findViewById(R.id.editText);

                editLabel.setText(activityActionBar.getTitle());
                editLabel.setFocusableInTouchMode(true);
                editLabel.setFocusable(true);
                editLabel.requestFocus();
                final Label label=(Label)toolbar.getTag();

                view.findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activityActionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP);
                        activityActionBar.setDisplayShowTitleEnabled(true);
                        activityActionBar.setDisplayShowCustomEnabled(false);
                        item.setVisible(true);
                    }
                });

                view.findViewById(R.id.doneBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //database call for edit label and notify data set changed for expandable list view.
                        label.setName(editLabel.getText().toString());
                        categoryListDBHelper.updateLabel(label);
                        activityActionBar.setTitle(editLabel.getText().toString());
                        drawerMenuListAdapter.notifyDataSetChanged();

                        activityActionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP);
                        activityActionBar.setDisplayShowTitleEnabled(true);
                        activityActionBar.setDisplayShowCustomEnabled(false);
                        item.setVisible(true);
                        CommonUtils.hideKeyboard(AddEditReportActivity.this);
                    }
                });

                activityActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
                activityActionBar.setDisplayShowCustomEnabled(true);



                Toolbar.LayoutParams layout = new Toolbar.LayoutParams(Toolbar.LayoutParams.FILL_PARENT, Toolbar.LayoutParams.FILL_PARENT);
                activityActionBar.setCustomView(view, layout);
                item.setVisible(false);
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putString("tabName",tabName);


    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(AddEditReportActivity.this);
        builder.setTitle("Go Back")
                .setMessage("Pressing back will take you to previous screen, are you sure wanna go back ?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                      AddEditReportActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
        Button negativeButton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setTextColor(ContextCompat.getColor(AddEditReportActivity.this,R.color.colorPrimaryDark));
        Button positiveButton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setTextColor(ContextCompat.getColor(AddEditReportActivity.this,R.color.colorPrimaryDark));
    }

    @Override
    public void onLabelAddClick() {

        mDrawerLayout.closeDrawer(Gravity.LEFT);

        try {
            categories = new DatabaseTaskCategoryList(AddEditReportActivity.this).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        final CustomCategoryPopUpAdapter adapter = new CustomCategoryPopUpAdapter(this, categories);
                final android.app.AlertDialog.Builder ad = new android.app.AlertDialog.Builder(AddEditReportActivity.this);
                ad.setCancelable(true);
                ad.setTitle("Select Category");

                ad.setSingleChoiceItems(adapter, -1,  new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialogInterface, int pos) {

                                final Label label = new Label();
                                label.setCategoryID(categories.get(pos).getCategoryId());
                                label.setName(categories.get(pos).getCategoryName());
                                label.setReportId(reportPOJO.getId());
                                String id = "";
                                try {
                                    id = new DatabaseTaskHelper(AddEditReportActivity.this,label).execute().get();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }

                                label.setId(id);

                                onLabelAdded(label);
                                dialogInterface.dismiss();
                            }
                        });

                ad.show();

    }


    public void onLabelAdded(Label label) {

        List<Label> labelList =  childMenuItems.get("Inspection");
        labelList.add(label);
        drawerMenuListAdapter.notifyDataSetChanged();
        reportPOJO.getLabelArrayList().add(label);


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        AddEditReportSelectedImagesFragment addEditReportSelectedImagesFragment = AddEditReportSelectedImagesFragment.initFragment(new ArrayList<ImageDetailsPOJO>(),new ArrayList<ImageDetailsPOJO>(),labelList.size()-1);
        fragmentTransaction.replace(R.id.content_frame, addEditReportSelectedImagesFragment);
        fragmentTransaction.commit();
        selectedFragmentPosition = (labelList.size() -1) + 3;
        activityActionBar.setTitle(label.getName());
        actionBarEditBtn.setVisible(true);
        toolbar.setTag(label);
        activityActionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP);
        activityActionBar.setDisplayShowTitleEnabled(true);
        activityActionBar.setDisplayShowCustomEnabled(false);

    }

    @Override
    public void onLabelDeleted(int position) {
        childMenuItems.get("Inspection").remove(position);
        drawerMenuListAdapter.notifyDataSetChanged();
        reportPOJO.getLabelArrayList().remove(position);

        if(reportPOJO.getLabelArrayList().size() > 0 ){
            selectedFragmentPosition = position > 0 ? position - 1 + 3 : position + 3;
            ArrayList<Label> labelArrayList =  reportPOJO.getLabelArrayList();
            if( labelArrayList!= null && labelArrayList.size() > selectedFragmentPosition - 3 && labelArrayList.get(selectedFragmentPosition - 3) != null) {
                FragmentManager transactionManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
                AddEditReportSelectedImagesFragment addEditReportSelectedImagesFragment = AddEditReportSelectedImagesFragment.initFragment(labelArrayList.get(selectedFragmentPosition - 3).getSelectedImages(), labelArrayList.get(selectedFragmentPosition - 3).getSelectedElevationImages(), selectedFragmentPosition - 3);
                fragmentTransaction.replace(R.id.content_frame, addEditReportSelectedImagesFragment);
                fragmentTransaction.commit();
                tabName = "AddEditReportSelectedImagesFragment";

                activityActionBar.setTitle(labelArrayList.get(selectedFragmentPosition - 3).getName());
                actionBarEditBtn.setVisible(true);
                toolbar.setTag(labelArrayList.get(selectedFragmentPosition - 3));
            }
        }else{
            mDrawerLayout.closeDrawer(Gravity.LEFT);
            putClaimDetailsFragment();
            actionBarEditBtn.setVisible(false);

        }


    }


    @Override
    public void setReportTitle(String reportTitle) {
        try {
            new DBUpdateTaskOnTextChanged(AddEditReportActivity.this, progressBarLayout,reportTitle,reportPOJO.getId(),false,categoryListDBHelper,"title").execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
            reportPOJO.setReportTitle(reportTitle);
    }

    @Override
    public void setReportDescription(String reportDescription) {

        reportPOJO.setReportDescription(reportDescription);
        try {
            new DBUpdateTaskOnTextChanged(AddEditReportActivity.this, progressBarLayout,reportDescription,reportPOJO.getId(),false,categoryListDBHelper,"description").execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setReportClientName(String reportClientName) {
        try {
            new DBUpdateTaskOnTextChanged(AddEditReportActivity.this, progressBarLayout,reportClientName,reportPOJO.getId(),false,categoryListDBHelper,"client name").execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        reportPOJO.setClientName(reportClientName);
    }

    @Override
    public void setReportClaimNumber(String reportClaimNumber) {
        try {
            new DBUpdateTaskOnTextChanged(AddEditReportActivity.this, progressBarLayout, reportClaimNumber,reportPOJO.getId(),false,categoryListDBHelper,"claim number").execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        reportPOJO.setClaimNumber(reportClaimNumber);
    }

    @Override
    public void setCreatedDate(String createdDate) {
        reportPOJO.setCreatedDate(createdDate);
    }


    @Override
    public void setLocationLat(String locationLat) {
        reportPOJO.setLocationLat(locationLat);
    }

    @Override
    public void setLocationLong(String locationLong) {
        reportPOJO.setLocationLong(locationLong);
    }

    @Override
    public void setAddressLine(String addressLine) {

        reportPOJO.setAddressLine(addressLine);
        try {
            new DBUpdateTaskOnTextChanged(AddEditReportActivity.this, progressBarLayout, addressLine,reportPOJO.getId(),false,categoryListDBHelper,"address line").execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void setSelectedElevationImages(ArrayList<ImageDetailsPOJO> elevationImagesList,int labelPosition) {

        reportPOJO.getLabelArrayList().get(labelPosition).setSelectedElevationImages(elevationImagesList);


        try {
            new DBSelectedImagesTask(AddEditReportActivity.this, progressBarLayout, reportPOJO.getLabelArrayList().get(labelPosition),false,categoryListDBHelper,"elevationImages").execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setSelectedImages(ArrayList<ImageDetailsPOJO> imagesList , int labelPosition) {

        reportPOJO.getLabelArrayList().get(labelPosition).setSelectedImages(imagesList);


        try {
            new DBSelectedImagesTask(AddEditReportActivity.this, progressBarLayout, reportPOJO.getLabelArrayList().get(labelPosition),false,categoryListDBHelper,"selectedImages").execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onNextButtonClick() {

        selectedFragmentPosition = selectedFragmentPosition + 1;
        if(selectedFragmentPosition == 1) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
            FragmentManager transactionManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_frame,new CauseOfLossFragment());
            fragmentTransaction.commit();
            tabName="CauseOfLossFragment";

            activityActionBar.setTitle("Cause Of Loss");
            actionBarEditBtn.setVisible(false);

        }
        else if(selectedFragmentPosition == 2) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
            FragmentManager transactionManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_frame,new PointOfOriginFragment());
            fragmentTransaction.commit();
            tabName="PointOfOriginFragment";

            activityActionBar.setTitle("Point Of Origin");
            actionBarEditBtn.setVisible(false);

        }else if(selectedFragmentPosition > 2){

            ArrayList<Label> labelArrayList =  reportPOJO.getLabelArrayList();
            if( labelArrayList!= null && labelArrayList.size() > selectedFragmentPosition - 3 && labelArrayList.get(selectedFragmentPosition - 3) != null){
                FragmentManager transactionManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = transactionManager.beginTransaction();
                AddEditReportSelectedImagesFragment addEditReportSelectedImagesFragment = AddEditReportSelectedImagesFragment.initFragment(labelArrayList.get(selectedFragmentPosition - 3).getSelectedImages(),labelArrayList.get(selectedFragmentPosition - 3).getSelectedElevationImages(), selectedFragmentPosition - 3);
                fragmentTransaction.replace(R.id.content_frame, addEditReportSelectedImagesFragment);
                fragmentTransaction.commit();
                tabName = "AddEditReportSelectedImagesFragment";

                activityActionBar.setTitle(labelArrayList.get(selectedFragmentPosition - 3).getName());
                actionBarEditBtn.setVisible(true);
                toolbar.setTag(labelArrayList.get(selectedFragmentPosition - 3));
            }else{
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                putClaimDetailsFragment();
                actionBarEditBtn.setVisible(false);
            }


        }

    }

    @Override
    public void onReportSave(boolean isProgressBar) {

        //Is from user action then validate
        if(isProgressBar) {
          if(reportPOJO.getReportTitle().trim().isEmpty()){
              CommonUtils.showSnackbarMessage(getString(R.string.please_enter_title), true, true,parentLayoutForMessages, AddEditReportActivity.this);

          return;
          }else if(reportPOJO.getReportDescription().trim().isEmpty()){
              CommonUtils.showSnackbarMessage(getString(R.string.enter_description_message), true, true, parentLayoutForMessages, AddEditReportActivity.this);
          return;
          }else if(reportPOJO.getClientName().trim().isEmpty()){
              CommonUtils.showSnackbarMessage(getString(R.string.enter_client_name_message), true, true, parentLayoutForMessages, AddEditReportActivity.this);
          return;
          }else if(reportPOJO.getClaimNumber().trim().isEmpty()){
              CommonUtils.showSnackbarMessage(getString(R.string.enter_claim_number_message), true, true, parentLayoutForMessages, AddEditReportActivity.this);
          return;
          }else if(reportPOJO.getAddressLine().trim().isEmpty()){
              CommonUtils.showSnackbarMessage(getString(R.string.please_add_address_message), true, true, parentLayoutForMessages, AddEditReportActivity.this);
              return;
          }

        }
        try {
            new DatabaseSaveReportTask(AddEditReportActivity.this, progressBarLayout, reportPOJO,false,categoryListDBHelper).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onReportGenerateClicked() {
        // If report already exists , delete it
        if(!reportPOJO.getFilePath().trim().isEmpty()){
            File file = new File(reportPOJO.getFilePath());
            if (file != null && file.exists()) {
                file.delete();
            }
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-mm-yyyy-hh:mm:ss");
        String dateString = simpleDateFormat.format(new Date());
        File destination = new File(Environment.getExternalStorageDirectory(), dateString + ".pdf");
        FileOutputStream fo;
        Font fontTitles = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);



        
        // This will be dynamic
        int numberOfImagesPerPage = 4;

        PDFDocHeader event = new PDFDocHeader(reportPOJO.getReportTitle());
        try{
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            final Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            addMetaData(document);
            PdfWriter pdfWriter = PdfWriter.getInstance(document, fo);
            document.open();
            pdfWriter.setPageEvent(event);
            document.add(new Paragraph("Report Description", fontTitles));
            document.add(new Paragraph(reportPOJO.getReportDescription()));
            document.add(new Paragraph(""));
            document.add(new Paragraph("Client Name", fontTitles));
            document.add(new Paragraph(reportPOJO.getClientName()));
            document.add(new Paragraph(""));

            document.add(new Paragraph("Claim Number", fontTitles));
            document.add(new Paragraph(reportPOJO.getClaimNumber()));
            document.add(new Paragraph(""));

            LineSeparator ls = new LineSeparator();
            ls.setLineColor(new BaseColor(99,100,99));
            document.add(new Chunk(ls));
            document.add(new Paragraph("Address", fontTitles));
            document.add(new Paragraph(reportPOJO.getAddressLine()));

            document.newPage();

            //Now read labels and show images accordingly.

            ArrayList<Label> labels = reportPOJO.getLabelArrayList();
            for(int p=0;p <labels.size() ;p++){

                Label label = labels.get(p);

                event.setHeader(label.getName());

                ArrayList<ImageDetailsPOJO> selectedElevationImagesList =  label.getSelectedElevationImages();
                ArrayList<ImageDetailsPOJO> selectedImageList = label.getSelectedImages();

                int j =0, k= 0;
                while( j< selectedElevationImagesList.size()){
                    if(!selectedElevationImagesList.get(j).getImageUrl().isEmpty()){
                        try {
                            PdfPTable table = new PdfPTable(3);
                            byte[] imageBytesResized;
                            table.setWidths(new float[]{1, 5, 4});
                            imageBytesResized = resizeImage(selectedElevationImagesList.get(j).getImageUrl(), (int) ((document.getPageSize().getWidth() / 2) - 100), (int) ((document.getPageSize().getHeight() / numberOfImagesPerPage) - 100));
                            com.itextpdf.text.Image img = com.itextpdf.text.Image.getInstance(imageBytesResized);

                            table.setHorizontalAlignment(Element.ALIGN_LEFT);
                            table.setWidthPercentage(100);
                            table.addCell(getImageNumberPdfPCell("", PdfPCell.ALIGN_LEFT));
                            table.addCell(getCellImagCell(img, PdfPCell.ALIGN_LEFT, document, numberOfImagesPerPage));
                            table.addCell(getCell(selectedElevationImagesList.get(j).getTitle(), selectedElevationImagesList.get(j).getDescription(), PdfPCell.LEFT, document, numberOfImagesPerPage));

                            document.add(table);
                            document.add(new Paragraph(" "));

                            if ((k + 1) % numberOfImagesPerPage == 0) {
                                document.newPage();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        k++;
                    }
                    j++;
                }

                if(k +1 < selectedElevationImagesList.size()){
                    document.newPage();
                }

                for (int i = 0; i < selectedImageList.size(); i++) {
                    try {
                        PdfPTable table = new PdfPTable(3);
                        byte[] imageBytesResized;
                        table.setWidths(new float[]{1, 5, 4});
                        imageBytesResized = resizeImage(selectedImageList.get(i).getImageUrl(), (int) ((document.getPageSize().getWidth() / 2) - 100), (int) ((document.getPageSize().getHeight() / numberOfImagesPerPage) - 100));
                        com.itextpdf.text.Image img = com.itextpdf.text.Image.getInstance(imageBytesResized);

                        table.setHorizontalAlignment(Element.ALIGN_LEFT);
                        table.setWidthPercentage(100);
                        table.addCell(getImageNumberPdfPCell((i + 1) + ".", PdfPCell.ALIGN_LEFT));
                        table.addCell(getCellImagCell(img, PdfPCell.ALIGN_LEFT, document, numberOfImagesPerPage));
                        table.addCell(getCell(selectedImageList.get(i).getTitle(), selectedImageList.get(i).getDescription(), PdfPCell.LEFT, document, numberOfImagesPerPage));
                        document.add(table);
                        document.add(new Paragraph(" "));

                        if ((i + 1) % numberOfImagesPerPage == 0) {
                            document.newPage();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                document.newPage();

            }

            try {
                new DBUpdateFilePath(AddEditReportActivity.this,progressBarLayout, reportPOJO.getId(), destination.getAbsolutePath(), true, categoryListDBHelper).execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            document.close();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private byte[] resizeImage(String imagePath, int maxWidth, int maxHeight) {
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap bmp = BitmapFactory.decodeFile(imagePath, options);
        return resizeImage(bmp, maxWidth, maxHeight, orientation);

    }

    public PdfPCell getCell(String title, String description, int alignment, Document document, int perPage) {
        PdfPCell cell = new PdfPCell();
         Font font=new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
        cell.addElement(new Phrase(title,font));
        cell.addElement(new Phrase(description,font));
        cell.setPadding(0);
        cell.setBorder(Rectangle.NO_BORDER);

        if (perPage == 2) {
            cell.setFixedHeight(document.getPageSize().getHeight() / perPage - 100);

        } else {
            cell.setFixedHeight(document.getPageSize().getHeight() / perPage - 100);
        }
        return cell;

    }

    public PdfPCell getCellImagCell(com.itextpdf.text.Image img, int alignment, Document document, int perPage) {

        PdfPCell cell = new PdfPCell(img);
        cell.setPadding(0);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(Rectangle.NO_BORDER);
        if (perPage == 2) {
            cell.setFixedHeight(document.getPageSize().getHeight() / perPage - 100);

        } else {
            cell.setFixedHeight(document.getPageSize().getHeight() / perPage - 100);
        }
        return cell;
    }


    private byte[] resizeImage(Bitmap image, int maxWidth, int maxHeight, int orientation) {

        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > ratioBitmap) {
                finalWidth = (int) ((float) maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float) maxHeight / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            Bitmap rotatedBitmap;
            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(image, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(image, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(image, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = image;
            }
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            return outStream.toByteArray();
        } else {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            return outStream.toByteArray();
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public PdfPCell getImageNumberPdfPCell(String number, int alignment) {
        PdfPCell cell = new PdfPCell();
        cell.addElement(new Phrase(number));
        cell.setPadding(0);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    class PDFDocHeader extends PdfPageEventHelper {
        Font footerFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.ITALIC);
        Font headerFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
        String reportTitle;

        PDFDocHeader(String reportTitle) {
            this.reportTitle = reportTitle;
        }
        public void setHeader(String reportTitle) {
            this.reportTitle = reportTitle;
        }



        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            Phrase header = new Phrase(reportTitle, headerFont);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    header,
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.top() + 10, 0);
            Phrase footer = new Phrase("Page " + writer.getPageNumber() + "", footerFont);
            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
                    footer,
                    (document.right()),
                    document.bottom() - 10, 0);

        }
    }

    private static void addMetaData(Document document) {
        document.addTitle("PDF Report");
        document.addSubject("Created By ElectiveChaos");
        document.addKeywords("ElectiveChaos");
        document.addAuthor("ElectiveChaos");
        document.addCreator("ElectiveChaos");
    }
    // Task for label addition
    class DatabaseTaskHelper extends AsyncTask <String,Void,String>{


        private Label label;
        private Context context;


        public  DatabaseTaskHelper(Context context,Label label) {
            this.context=context;
            this.label=label;

        }

        @Override
        protected void onPreExecute() {
            CommonUtils.lockOrientation((Activity) context);
            if(progressBarLayout != null){
                progressBarLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected String doInBackground(String... strings) {

            return categoryListDBHelper.addLabel(label);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(progressBarLayout != null){
                progressBarLayout.setVisibility(View.GONE);
            }
            CommonUtils.unlockOrientation((Activity)context);

        }
    }

    // Task for getting  cat list
    class DatabaseTaskCategoryList extends AsyncTask <String,Void,ArrayList<Category>>{

        private Context context;


        public  DatabaseTaskCategoryList(Context context) {
            this.context=context;

        }

        @Override
        protected void onPreExecute() {
            CommonUtils.lockOrientation((Activity) context);
            if(progressBarLayout != null){
                progressBarLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected ArrayList<Category> doInBackground(String... strings) {

          ArrayList<Category> categories= categoryListDBHelper.getCategoryList();
            return categories;
        }

        @Override
        protected void onPostExecute(ArrayList<Category> result) {
            if(progressBarLayout != null){
                progressBarLayout.setVisibility(View.GONE);
            }
            CommonUtils.unlockOrientation((Activity)context);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_action_menu, menu);
        actionBarEditBtn= menu.findItem(R.id.edit);
        actionBarEditBtn.setVisible(false);

        return true;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    
}
