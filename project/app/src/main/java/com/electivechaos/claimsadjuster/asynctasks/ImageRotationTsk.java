package com.electivechaos.claimsadjuster.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import com.electivechaos.claimsadjuster.database.CategoryListDBHelper;
import com.electivechaos.claimsadjuster.interfaces.AsyncTaskStatusCallback;
import com.electivechaos.claimsadjuster.pojo.ImageDetailsPOJO;
import com.electivechaos.claimsadjuster.pojo.Label;
import com.electivechaos.claimsadjuster.ui.SingleImageDetailsActivity;
import com.electivechaos.claimsadjuster.utils.CommonUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by nafeesa on 5/28/18.
 */


public class ImageRotationTsk extends AsyncTask<String, Integer, ArrayList> {
    String path;
    File file;
    Bitmap bitmap;
    Matrix matrix;
    View progressBar;
    boolean isProgressBar;
    private WeakReference<View> viewWeakReference;
    private WeakReference<Context> contextWeakReference;
    private AsyncTaskStatusCallback asyncTaskStatusCallback;



    public ImageRotationTsk(Context context, String path, File file, Bitmap bitmap, Matrix matrix, View progressBar, boolean isProgressBar, AsyncTaskStatusCallback asyncTaskStatusCallback) {
        this.path = path;
        this.file = file;
        this.bitmap = bitmap;
        this.matrix = matrix;
        this.progressBar = progressBar;
        this.isProgressBar = isProgressBar;
        this.contextWeakReference = new WeakReference<>(context);
        this.viewWeakReference = new WeakReference<>(progressBar);
        this.asyncTaskStatusCallback = asyncTaskStatusCallback;
    }

    @Override
    protected void onPreExecute() {
        Context context = contextWeakReference.get();
        View progressBarLayout = viewWeakReference.get();

        if (context == null) {
            return;
        }
        CommonUtils.lockOrientation((Activity) context);

        if (isProgressBar) {
            if (progressBarLayout != null) {
                progressBarLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
    }

    @Override
    protected ArrayList doInBackground(String... strings) {

        bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
        byte[]  image = outStream.toByteArray();

        if (file.getPath() == null) {
            return null;
        }
        File file1 = new File(file.getPath());
        if (!file1.exists()) {
            try {
                file1.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(path, false);
            outputStream.write(image);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

       // categoryListDBHelper.updateImageDetails(selectedImage);

        return null;
    }

    @Override
    protected void onPostExecute(ArrayList result) {
        Context context = contextWeakReference.get();
        View progressBarLayout = viewWeakReference.get();
        if (context == null) {
            return;
        }
        if (progressBarLayout != null) {
            progressBarLayout.setVisibility(View.GONE);
        }
        //   Toast.makeText(context, "Image edited sucessfully...", Toast.LENGTH_SHORT).show();

        CommonUtils.unlockOrientation((Activity) context);
        if (asyncTaskStatusCallback != null) {
            asyncTaskStatusCallback.onPostExecute(null, "report_save_complete");
        }


    }


}

