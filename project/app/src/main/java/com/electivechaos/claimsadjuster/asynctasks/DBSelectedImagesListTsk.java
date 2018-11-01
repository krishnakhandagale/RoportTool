package com.electivechaos.claimsadjuster.asynctasks;

import android.os.AsyncTask;

import com.electivechaos.claimsadjuster.database.CategoryListDBHelper;
import com.electivechaos.claimsadjuster.interfaces.AsyncTaskStatusCallback;
import com.electivechaos.claimsadjuster.pojo.Image;
import com.electivechaos.claimsadjuster.pojo.ImageDetailsPOJO;
import com.electivechaos.claimsadjuster.pojo.Label;

import java.util.ArrayList;

/**
 * Created by nafeesa on 5/28/18.
 */


public class DBSelectedImagesListTsk extends AsyncTask<String,Integer,ArrayList> {
    private CategoryListDBHelper categoryListDBHelper;
    private String type;
    private AsyncTaskStatusCallback taskCompleteCallback;
    private Label label;
    private ArrayList<ImageDetailsPOJO> imageList;

    public DBSelectedImagesListTsk(CategoryListDBHelper categoryListDBHelper, String type, Label label ,ArrayList<ImageDetailsPOJO> imageList, AsyncTaskStatusCallback taskCompleteCallback) {
        this.categoryListDBHelper = categoryListDBHelper;
        this.type = type;
        this.taskCompleteCallback = taskCompleteCallback;
        this.label = label;
        this.imageList = imageList;

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

        if(type.equalsIgnoreCase("insert_selected_images")) {
            return categoryListDBHelper.appendSelectedImages(label, imageList);

        } else if(type.equalsIgnoreCase("insert_captured_image")) {
            ImageDetailsPOJO imgObj = categoryListDBHelper.appendCapturedImages(label, imageList);
            ArrayList<ImageDetailsPOJO> imageList = new ArrayList<>();
            imageList.add(imgObj);
            return imageList;

        } else if(type.equalsIgnoreCase("insert_rearranged_image")) {
            categoryListDBHelper.updateSelectedImages(label, imageList);
            return categoryListDBHelper.getLabelImages(label.getId());
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList result) {
        taskCompleteCallback.onPostExecute(result,type);

    }
}

