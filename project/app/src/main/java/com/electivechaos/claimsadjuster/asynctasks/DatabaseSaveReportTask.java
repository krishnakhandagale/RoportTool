package com.electivechaos.claimsadjuster.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import com.electivechaos.claimsadjuster.database.CategoryListDBHelper;
import com.electivechaos.claimsadjuster.interfaces.AsyncTaskStatusCallback;
import com.electivechaos.claimsadjuster.pojo.ReportPOJO;
import com.electivechaos.claimsadjuster.utils.CommonUtils;

import java.lang.ref.WeakReference;

/**
 * Created by nafeesa on 5/16/18.
 */

public class DatabaseSaveReportTask extends AsyncTask<String, Void, Void> {

    private WeakReference<Context> contextWeakReference;
    private WeakReference<View> viewWeakReference;

    private boolean isProgressBar;
    private CategoryListDBHelper categoryListDBHelper;
    private ReportPOJO reportPOJO;
    private AsyncTaskStatusCallback asyncTaskStatusCallback;

    public DatabaseSaveReportTask(Context context, View progressBarLayout, ReportPOJO reportPOJO, boolean isProgressBar, CategoryListDBHelper categoryListDBHelper) {
        this.isProgressBar = isProgressBar;
        this.categoryListDBHelper = categoryListDBHelper;
        this.reportPOJO = reportPOJO;
        this.contextWeakReference = new WeakReference<>(context);
        this.viewWeakReference = new WeakReference<>(progressBarLayout);
    }

    public DatabaseSaveReportTask(Context context, View progressBarLayout, ReportPOJO reportPOJO, boolean isProgressBar, CategoryListDBHelper categoryListDBHelper, AsyncTaskStatusCallback asyncTaskStatusCallback) {
        this.isProgressBar = isProgressBar;
        this.categoryListDBHelper = categoryListDBHelper;
        this.reportPOJO = reportPOJO;
        this.asyncTaskStatusCallback = asyncTaskStatusCallback;
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

        if (asyncTaskStatusCallback != null) {
            asyncTaskStatusCallback.onPreExecute();
        }
        if (isProgressBar) {
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
        Context context = contextWeakReference.get();
        View progressBarLayout = viewWeakReference.get();
        if (context == null) {
            return;
        }
        if (asyncTaskStatusCallback != null) {
            asyncTaskStatusCallback.onPostExecute(null, "report_save_complete");
        }
        if (progressBarLayout != null) {
            progressBarLayout.setVisibility(View.GONE);
        }
        CommonUtils.unlockOrientation((Activity) context);

    }
}
