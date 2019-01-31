package com.electivechaos.claimsadjuster.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.electivechaos.claimsadjuster.database.CategoryListDBHelper;
import com.electivechaos.claimsadjuster.interfaces.AsyncTaskStatusCallback;
import com.electivechaos.claimsadjuster.interfaces.AsyncTaskStatusCallbackForNotes;
import com.electivechaos.claimsadjuster.pojo.Label;
import com.electivechaos.claimsadjuster.utils.CommonUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by nafeesa on 1/22/19.
 */

// Task for getting  cat list
public class DBReportLabelList extends AsyncTask<String, Integer, ArrayList<Label>> {

    private WeakReference<Context> contextWeakReference;
    private CategoryListDBHelper categoryListDBHelper;
    private AsyncTaskStatusCallback taskCompleteCallback;
    private String reportId;


    public DBReportLabelList(Context context, CategoryListDBHelper categoryListDBHelper, String reportId, AsyncTaskStatusCallback taskCompleteCallback) {
        this.contextWeakReference = new WeakReference<>(context);
        this.categoryListDBHelper = categoryListDBHelper;
        this.taskCompleteCallback = taskCompleteCallback;
        this.reportId = reportId;
    }

    @Override
    protected void onPreExecute() {
        taskCompleteCallback.onPreExecute();
        Context context = contextWeakReference.get();
        if(context !=null)
        CommonUtils.lockOrientation((Activity) context);
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        taskCompleteCallback.onProgress(values[0]);
    }


    @Override
    protected ArrayList<Label> doInBackground(String... strings) {
        ArrayList<Label> labelList;
            labelList = categoryListDBHelper.getReportLabelList(reportId);
        return labelList;
    }

    @Override
    protected void onPostExecute(ArrayList result) {
        taskCompleteCallback.onPostExecute(result,reportId);
        CommonUtils.unlockOrientation((Activity) contextWeakReference.get());
    }
}

