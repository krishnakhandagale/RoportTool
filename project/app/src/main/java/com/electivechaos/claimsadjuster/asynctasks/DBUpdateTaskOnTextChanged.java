package com.electivechaos.claimsadjuster.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import com.electivechaos.claimsadjuster.database.CategoryListDBHelper;
import com.electivechaos.claimsadjuster.pojo.ReportPOJO;
import com.electivechaos.claimsadjuster.utils.CommonUtils;

/**
 * Created by nafeesa on 5/17/18.
 */

public class DBUpdateTaskOnTextChanged extends AsyncTask<String,Void,Void> {

    private Context context;
    private boolean isProgressBar;
    View progressBarLayout;
    CategoryListDBHelper categoryListDBHelper;
    String value;
    String type;
    String reportId;

    public  DBUpdateTaskOnTextChanged(Context context, View progressBarLayout, String value,String reportId, boolean isProgressBar, CategoryListDBHelper categoryListDBHelper, String type) {
        this.context = context;
        this.isProgressBar = isProgressBar;
        this.categoryListDBHelper = categoryListDBHelper;
        this.value = value;
        this.type = type;
        this.reportId = reportId;
        this.progressBarLayout = progressBarLayout;
    }

  @Override
    protected void onPreExecute() {
        CommonUtils.lockOrientation((Activity) context);
        if(isProgressBar)
        {
            if (progressBarLayout != null) {
                progressBarLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected Void doInBackground(String... strings) {
        if(type.equalsIgnoreCase("title")) {
            categoryListDBHelper.updateReportTitle(value,reportId);
        }
        else if(type.equalsIgnoreCase("description")){
            categoryListDBHelper.updateReportDecription(value, reportId);
        }
        else if(type.equalsIgnoreCase("clientName")){
            categoryListDBHelper.updateClientName(value, reportId);
        }
        else if(type.equalsIgnoreCase("claimNumber")){
            categoryListDBHelper.updateClaimNumber(value, reportId);
        }
        else if(type.equalsIgnoreCase("addressLine")){
            categoryListDBHelper.updateAddressLine(value, reportId);
        }
        else if(type.equalsIgnoreCase("latitude")){
            categoryListDBHelper.updateLocationLat(value, reportId);
        }
        else if(type.equalsIgnoreCase("longitude")){
            categoryListDBHelper.updateLocationLong(value, reportId);
        }


        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        if(progressBarLayout != null){
            progressBarLayout.setVisibility(View.GONE);
        }
        CommonUtils.unlockOrientation((Activity)context);

    }
}