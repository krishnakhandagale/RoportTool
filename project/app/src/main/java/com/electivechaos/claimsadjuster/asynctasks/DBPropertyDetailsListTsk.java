package com.electivechaos.claimsadjuster.asynctasks;

import android.os.AsyncTask;

import com.electivechaos.claimsadjuster.database.CategoryListDBHelper;
import com.electivechaos.claimsadjuster.interfaces.AsyncTaskStatusCallback;
import com.electivechaos.claimsadjuster.pojo.BuildingTypePOJO;
import com.electivechaos.claimsadjuster.pojo.FoundationPOJO;
import com.electivechaos.claimsadjuster.pojo.RoofSystemPOJO;
import com.electivechaos.claimsadjuster.pojo.SidingPOJO;

import java.util.ArrayList;

/**
 * Created by nafeesa on 5/28/18.
 */


public class DBPropertyDetailsListTsk extends AsyncTask<String,Integer,ArrayList> {
    private CategoryListDBHelper categoryListDBHelper;
    private String type;
    private AsyncTaskStatusCallback taskCompleteCallback;

    public  DBPropertyDetailsListTsk(CategoryListDBHelper categoryListDBHelper, String type, AsyncTaskStatusCallback taskCompleteCallback) {
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

        ArrayList list=null;
        if(type.equalsIgnoreCase("roof_system")) {

            ArrayList<RoofSystemPOJO> rlist =categoryListDBHelper.getRoofSystemList();
            return rlist;
        }
        else if(type.equalsIgnoreCase("siding")) {

            ArrayList<SidingPOJO> rlist = categoryListDBHelper.getSidingList();
           return rlist;
        }
        else if(type.equalsIgnoreCase("foundation")) {

            ArrayList<FoundationPOJO> rlist = categoryListDBHelper.getFoundationList();
            return rlist;
        }
        else if(type.equalsIgnoreCase("building_type")) {

            ArrayList<BuildingTypePOJO> rlist = categoryListDBHelper.getBuildingTypeList();
            return rlist;
        }
        return list;
    }

    @Override
    protected void onPostExecute(ArrayList result) {
        taskCompleteCallback.onPostExecute(result,type);

    }
}

