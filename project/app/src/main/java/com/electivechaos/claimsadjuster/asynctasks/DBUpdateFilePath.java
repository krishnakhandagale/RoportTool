package com.electivechaos.claimsadjuster.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import com.electivechaos.claimsadjuster.database.CategoryListDBHelper;
import com.electivechaos.claimsadjuster.pojo.Label;
import com.electivechaos.claimsadjuster.utils.CommonUtils;

/**
 * Created by nafeesa on 5/18/18.
 */

public class DBUpdateFilePath extends AsyncTask<String,Void,Void> {

    private Context context;
    private boolean isProgressBar;
    private View progressBarLayout;
    private CategoryListDBHelper categoryListDBHelper;
    String reportId;
    String filePath;

    public  DBUpdateFilePath(Context context, View progressBarLayout, String reportId, String filePath, boolean isProgressBar, CategoryListDBHelper categoryListDBHelper) {
        this.context = context;
        this.isProgressBar = isProgressBar;
        this.categoryListDBHelper = categoryListDBHelper;
        this.reportId=reportId;
        this.progressBarLayout = progressBarLayout;
        this.filePath = filePath;
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
        categoryListDBHelper.updateFilePath(filePath,reportId);
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