package com.electivechaos.claimsadjuster.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import com.electivechaos.claimsadjuster.database.CategoryListDBHelper;
import com.electivechaos.claimsadjuster.pojo.ReportPOJO;
import com.electivechaos.claimsadjuster.utils.CommonUtils;

/**
 * Created by nafeesa on 5/16/18.
 */

public class DatabaseSaveReportTask extends AsyncTask<String,Void,Void> {

    private Context context;
    private boolean isProgressBar;
    View progressBarLayout;
    CategoryListDBHelper categoryListDBHelper;
    ReportPOJO reportPOJO;

    public  DatabaseSaveReportTask(Context context, View progressBarLayout, ReportPOJO reportPOJO, boolean isProgressBar, CategoryListDBHelper categoryListDBHelper) {
        this.context=context;
        this.isProgressBar=isProgressBar;
        this.categoryListDBHelper = categoryListDBHelper;
        this.reportPOJO=reportPOJO;
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
        categoryListDBHelper.addReportEntry(reportPOJO);
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
