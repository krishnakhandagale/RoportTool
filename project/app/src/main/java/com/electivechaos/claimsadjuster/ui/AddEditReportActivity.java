package com.electivechaos.claimsadjuster.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;

import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.adapters.CustomCategoryPopUpAdapter;
import com.electivechaos.claimsadjuster.adapters.DrawerMenuListAdapter;
import com.electivechaos.claimsadjuster.asynctasks.DBUpdateTaskOnTextChanged;
import com.electivechaos.claimsadjuster.database.CategoryListDBHelper;
import com.electivechaos.claimsadjuster.asynctasks.DatabaseSaveReportTask;
import com.electivechaos.claimsadjuster.fragments.AddEditReportSelectedImagesFragment;
import com.electivechaos.claimsadjuster.fragments.CauseOfLossFragment;
import com.electivechaos.claimsadjuster.fragments.ClaimDetailsFragment;
import com.electivechaos.claimsadjuster.fragments.PointOfOriginFragment;
import com.electivechaos.claimsadjuster.interfaces.AddEditLabelInterface;
import com.electivechaos.claimsadjuster.interfaces.ClaimDetailsDataInterface;
import com.electivechaos.claimsadjuster.interfaces.LossLocationDataInterface;
import com.electivechaos.claimsadjuster.interfaces.NextButtonClickListener;
import com.electivechaos.claimsadjuster.interfaces.OnSaveReportClickListener;
import com.electivechaos.claimsadjuster.interfaces.SelectedImagesDataInterface;
import com.electivechaos.claimsadjuster.pojo.Category;
import com.electivechaos.claimsadjuster.pojo.ImageDetailsPOJO;
import com.electivechaos.claimsadjuster.pojo.Label;
import com.electivechaos.claimsadjuster.pojo.ReportPOJO;
import com.electivechaos.claimsadjuster.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AddEditReportActivity extends AppCompatActivity implements DrawerMenuListAdapter.OnLabelAddClickListener, AddEditLabelInterface, ClaimDetailsDataInterface, LossLocationDataInterface,SelectedImagesDataInterface,NextButtonClickListener,OnSaveReportClickListener{
    private DrawerLayout mDrawerLayout;
    private ExpandableListView mExpandableListView;
    private DrawerMenuListAdapter drawerMenuListAdapter;
    private String tabName;

    HashMap<String, List<Label>> childMenuItems = new HashMap<>();
    List<Label> inspectionChildMenu = new ArrayList<>();
    ArrayList<String> parentMenuItems;

    private int selectedFragmentPosition = 0;

    private ArrayList<Category> categories = null;
    static CategoryListDBHelper categoryListDBHelper;

    private ReportPOJO reportPOJO ;
    private View progressBarLayout;

    private  Toolbar toolbar;
    private MenuItem actionBarEditBtn;
    private ActionBar activityActionBar;

    private String reportId;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_edit_report_activity_layout);
        progressBarLayout = findViewById(R.id.progressBarLayout);
        categoryListDBHelper = CategoryListDBHelper.getInstance(this);

        if(getIntent().getExtras() != null){
            reportId = getIntent().getExtras().getString("reportId");
            reportPOJO = categoryListDBHelper.getReportItem(getIntent().getExtras().getString("reportId"));
        }else{
            reportPOJO = new ReportPOJO();
            reportPOJO.setId(String.valueOf(new Date().getTime()));
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

                if(label != null){
                  // Here we get all the label information
                }

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
                                long id = 0;
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
            new DBUpdateTaskOnTextChanged(AddEditReportActivity.this,reportTitle,reportId,false,categoryListDBHelper,"title").execute().get();
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
            new DBUpdateTaskOnTextChanged(AddEditReportActivity.this,reportDescription,reportId,false,categoryListDBHelper,"description").execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Log.d("MY VALUE"," "+reportPOJO.getReportDescription());
    }

    @Override
    public void setReportClientName(String reportClientName) {
        //db call
        try {
            new DBUpdateTaskOnTextChanged(AddEditReportActivity.this,reportClientName,reportId,false,categoryListDBHelper,"client name").execute().get();
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
            new DBUpdateTaskOnTextChanged(AddEditReportActivity.this,reportClaimNumber,reportId,false,categoryListDBHelper,"claim number").execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        reportPOJO.setClaimNumber(reportClaimNumber);
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
        try {
            new DBUpdateTaskOnTextChanged(AddEditReportActivity.this,addressLine,reportId,false,categoryListDBHelper,"address line").execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        reportPOJO.setAddressLine(addressLine);
    }


    @Override
    public void setSelectedElevationImages(ArrayList<ImageDetailsPOJO> elevationImagesList,int labelPosition) {
        reportPOJO.getLabelArrayList().get(labelPosition).setSelectedElevationImages(elevationImagesList);
    }

    @Override
    public void setSelectedImages(ArrayList<ImageDetailsPOJO> imagesList , int labelPosition) {
        reportPOJO.getLabelArrayList().get(labelPosition).setSelectedImages(imagesList);

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
        try {
            new DatabaseSaveReportTask(AddEditReportActivity.this,reportPOJO,false,categoryListDBHelper).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }


    // Task for label addition
    class DatabaseTaskHelper extends AsyncTask <String,Void,Long>{


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
        protected Long doInBackground(String... strings) {

            long id = categoryListDBHelper.addLabel(label);
            return id;
        }

        @Override
        protected void onPostExecute(Long result) {
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
