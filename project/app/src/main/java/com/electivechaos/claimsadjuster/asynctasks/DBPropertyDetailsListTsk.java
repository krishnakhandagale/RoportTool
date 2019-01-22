package com.electivechaos.claimsadjuster.asynctasks;

import android.os.AsyncTask;

import com.electivechaos.claimsadjuster.database.CategoryListDBHelper;
import com.electivechaos.claimsadjuster.interfaces.AsyncTaskStatusCallback;

import java.util.ArrayList;

/**
 * Created by nafeesa on 5/28/18.
 */


public class DBPropertyDetailsListTsk extends AsyncTask<String, Integer, ArrayList> {
    private CategoryListDBHelper categoryListDBHelper;
    private String type;
    private AsyncTaskStatusCallback taskCompleteCallback;

    public DBPropertyDetailsListTsk(CategoryListDBHelper categoryListDBHelper, String type, AsyncTaskStatusCallback taskCompleteCallback) {
        this.categoryListDBHelper = categoryListDBHelper;
        this.type = type;
        this.taskCompleteCallback = taskCompleteCallback;

    }

    @Override
    protected void onPreExecute() {
        taskCompleteCallback.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        taskCompleteCallback.onProgress(values[0]);
    }

    @Override
    protected ArrayList doInBackground(String... strings) {

        if (type.equalsIgnoreCase("roof_system")) {

            return categoryListDBHelper.getRoofSystemList();
        } else if (type.equalsIgnoreCase("siding")) {

            return categoryListDBHelper.getSidingList();
        } else if (type.equalsIgnoreCase("foundation")) {

            return categoryListDBHelper.getFoundationList();
        } else if (type.equalsIgnoreCase("building_type")) {

            return categoryListDBHelper.getBuildingTypeList();

        } else if (type.equalsIgnoreCase("coverage_type")) {

            return categoryListDBHelper.getCoverageList();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList result) {
        taskCompleteCallback.onPostExecute(result, type);

    }
}

