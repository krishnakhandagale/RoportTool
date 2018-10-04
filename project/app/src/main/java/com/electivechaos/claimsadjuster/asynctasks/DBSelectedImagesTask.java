package com.electivechaos.claimsadjuster.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import com.electivechaos.claimsadjuster.database.CategoryListDBHelper;
import com.electivechaos.claimsadjuster.pojo.Label;
import com.electivechaos.claimsadjuster.utils.CommonUtils;

import java.lang.ref.WeakReference;

/**
 * Created by nafeesa on 5/17/18.
 */

public class DBSelectedImagesTask  extends AsyncTask<String,Void,Void> {


    private WeakReference<Context> contextWeakReference;
    private WeakReference<View> viewWeakReference;
    private boolean isProgressBar;
    private CategoryListDBHelper categoryListDBHelper;
    private String type;
    private Label label;

    public  DBSelectedImagesTask (Context context, View progressBarLayout, Label label,  boolean isProgressBar, CategoryListDBHelper categoryListDBHelper, String type) {
        this.isProgressBar = isProgressBar;
        this.categoryListDBHelper = categoryListDBHelper;
        this.type = type;
        this.label = label;
        this.contextWeakReference = new WeakReference<>(context);
        this.viewWeakReference = new WeakReference<>(progressBarLayout);


    }

    @Override
    protected void onPreExecute() {
        Context context = contextWeakReference.get();
        View progressBarLayout  =  viewWeakReference.get();

        if(context == null){
            return;
        }
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
        if(type.equals("selected_images")) {
            categoryListDBHelper.updateSelectedImages(label);
        }
        else if(type.equals("elevation_images")){
            categoryListDBHelper.updateElevationImages(label);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {

        Context context = contextWeakReference.get();
        View progressBarLayout  =  viewWeakReference.get();
        if(context == null){
            return;
        }
        if(progressBarLayout != null){
            progressBarLayout.setVisibility(View.GONE);
        }
        CommonUtils.unlockOrientation((Activity)context);

    }
}