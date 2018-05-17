package com.electivechaos.claimsadjuster.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import com.electivechaos.claimsadjuster.database.CategoryListDBHelper;
import com.electivechaos.claimsadjuster.pojo.Label;
import com.electivechaos.claimsadjuster.utils.CommonUtils;

import java.util.ArrayList;

/**
 * Created by nafeesa on 5/17/18.
 */

public class DBSelectedImagesTask  extends AsyncTask<String,Void,Void> {

    private Context context;
    private boolean isProgressBar;
    private View progressBarLayout;
    private CategoryListDBHelper categoryListDBHelper;
    String type;
    long labelId;
    ArrayList<Label> labelArrayList;

    public  DBSelectedImagesTask (Context context, ArrayList<Label> labelArrayList,long labelId,  boolean isProgressBar, CategoryListDBHelper categoryListDBHelper, String type) {
        this.context = context;
        this.isProgressBar = isProgressBar;
        this.categoryListDBHelper = categoryListDBHelper;
        this.type = type;
        this.labelArrayList=labelArrayList;
        this.labelId=labelId;

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
            categoryListDBHelper.updateSelectedImages(labelId,labelArrayList);
        }
        else if(type.equalsIgnoreCase("elevationImages")){
            categoryListDBHelper.updateElevationImages(labelId, labelArrayList);
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