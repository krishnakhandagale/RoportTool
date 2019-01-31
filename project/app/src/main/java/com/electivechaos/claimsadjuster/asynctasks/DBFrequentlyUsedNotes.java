package com.electivechaos.claimsadjuster.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.electivechaos.claimsadjuster.database.CategoryListDBHelper;
import com.electivechaos.claimsadjuster.interfaces.AsyncTaskStatusCallback;
import com.electivechaos.claimsadjuster.interfaces.AsyncTaskStatusCallbackForNotes;
import com.electivechaos.claimsadjuster.utils.CommonUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by nafeesa on 1/22/19.
 */

// Task for getting  cat list
public class DBFrequentlyUsedNotes extends AsyncTask<String, Integer, ArrayList> {

    private WeakReference<Context> contextWeakReference;
    private CategoryListDBHelper categoryListDBHelper;
    private AsyncTaskStatusCallbackForNotes taskCompleteCallback;
    private String type;


    public DBFrequentlyUsedNotes(Context context, CategoryListDBHelper categoryListDBHelper, String type, AsyncTaskStatusCallbackForNotes taskCompleteCallback) {
        this.contextWeakReference = new WeakReference<>(context);
        this.categoryListDBHelper = categoryListDBHelper;
        this.taskCompleteCallback = taskCompleteCallback;
        this.type = type;
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
    protected ArrayList<String> doInBackground(String... strings) {
        ArrayList<String> notesList;
        if(type.equals("lastnote")){
            notesList = categoryListDBHelper.getLastNote();
        }else {
             notesList = categoryListDBHelper.getNotesList();
        }
        return notesList;
    }

    @Override
    protected void onPostExecute(ArrayList result) {
        taskCompleteCallback.onPostExecute(result,type);
        CommonUtils.unlockOrientation((Activity) contextWeakReference.get());
    }
}

