package com.electivechaos.claimsadjuster.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import com.electivechaos.claimsadjuster.database.CategoryListDBHelper;
import com.electivechaos.claimsadjuster.utils.CommonUtils;

import java.lang.ref.WeakReference;

/**
 * Created by nafeesa on 5/17/18.
 */

public class DBUpdateTaskOnTextChanged extends AsyncTask<String, Void, Void> {

    private WeakReference<Context> contextWeakReference;
    private WeakReference<View> viewWeakReference;

    private boolean isProgressBar;
    private CategoryListDBHelper categoryListDBHelper;
    private String value;
    private String type;
    private String reportId;

    public DBUpdateTaskOnTextChanged(Context context, View progressBarLayout, String value, String reportId, boolean isProgressBar, CategoryListDBHelper categoryListDBHelper, String type) {
        this.isProgressBar = isProgressBar;
        this.categoryListDBHelper = categoryListDBHelper;
        this.value = value;
        this.type = type;
        this.reportId = reportId;
        this.contextWeakReference = new WeakReference<>(context);
        this.viewWeakReference = new WeakReference<>(progressBarLayout);
    }

    @Override
    protected void onPreExecute() {

        Context context = contextWeakReference.get();
        View progressBarLayout = viewWeakReference.get();

        if (context == null) {
            return;
        }
        CommonUtils.lockOrientation((Activity) context);
        if (isProgressBar) {
            if (progressBarLayout != null) {
                progressBarLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected Void doInBackground(String... strings) {
        if (type.equals("title")) {
            categoryListDBHelper.updateReportTitle(value, reportId);
        }
        if (type.equals("description")) {
            categoryListDBHelper.updateReportDescription(value, reportId);
        } else if (type.equals("client_name")) {
            categoryListDBHelper.updateClientName(value, reportId);
        } else if (type.equals("claim_number")) {
            categoryListDBHelper.updateClaimNumber(value, reportId);
        } else if (type.equals("report_by")) {
            categoryListDBHelper.updateReportBy(value, reportId);
        } else if (type.equals("address_line")) {
            categoryListDBHelper.updateAddressLine(value, reportId);
        } else if (type.equals("latitude")) {
            categoryListDBHelper.updateLocationLat(value, reportId);
        } else if (type.equals("longitude")) {
            categoryListDBHelper.updateLocationLong(value, reportId);
        } else if (type.equals("property_date")) {
            categoryListDBHelper.updatePropertyDate(value, reportId);
        } else if (type.equals("square_footage")) {
            categoryListDBHelper.updateSquareFootage(value, reportId);
        } else if (type.equals("roof_system")) {
            categoryListDBHelper.updateRoofSystem(value, reportId);
        } else if (type.equals("siding")) {
            categoryListDBHelper.updateSiding(value, reportId);
        } else if (type.equals("foundation")) {
            categoryListDBHelper.updateFoundation(value, reportId);
        } else if (type.equals("building_type")) {
            categoryListDBHelper.updateBuildingType(value, reportId);
        } else if (type.equals("peril")) {
            categoryListDBHelper.updatePerilName(value, reportId);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        Context context = contextWeakReference.get();
        View progressBarLayout = viewWeakReference.get();

        if (context == null) {
            return;
        }
        if (progressBarLayout != null) {
            progressBarLayout.setVisibility(View.GONE);
        }
        CommonUtils.unlockOrientation((Activity) context);

    }
}