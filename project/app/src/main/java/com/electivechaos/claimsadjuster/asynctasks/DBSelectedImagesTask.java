package com.electivechaos.claimsadjuster.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import com.electivechaos.claimsadjuster.database.CategoryListDBHelper;
import com.electivechaos.claimsadjuster.pojo.Label;
import com.electivechaos.claimsadjuster.utils.CommonUtils;

/**
 * Created by nafeesa on 5/17/18.
 */

public class DBSelectedImagesTask  extends AsyncTask<String,Void,Void> {

    private Context context;
    private boolean isProgressBar;
    private View progressBarLayout;
    private CategoryListDBHelper categoryListDBHelper;
    String type;
    Label label;

    public  DBSelectedImagesTask (Context context, Label label,  boolean isProgressBar, CategoryListDBHelper categoryListDBHelper, String type) {
        this.context = context;
        this.isProgressBar = isProgressBar;
        this.categoryListDBHelper = categoryListDBHelper;
        this.type = type;
        this.label = label;

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
        if(type.equalsIgnoreCase("selectedImages")) {
            categoryListDBHelper.updateSelectedImages(label);
        }
        else if(type.equalsIgnoreCase("elevationImages")){
            categoryListDBHelper.updateElevationImages(label);
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