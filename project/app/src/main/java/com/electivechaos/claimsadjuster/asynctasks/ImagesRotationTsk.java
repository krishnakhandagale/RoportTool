package com.electivechaos.claimsadjuster.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;

import com.electivechaos.claimsadjuster.dialog.ImageDetailsFragment;
import com.electivechaos.claimsadjuster.interfaces.AsyncTaskStatusCallback;
import com.electivechaos.claimsadjuster.pojo.ImageDetailsPOJO;
import com.electivechaos.claimsadjuster.utils.CommonUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by nafeesa on 5/28/18.
 */


public class ImagesRotationTsk extends AsyncTask<String, Integer, ArrayList> {
    String path;
    File file;
    Bitmap bitmap;
    Matrix matrix;
    View progressBar;
    boolean isProgressBar;
    private ArrayList<ImageDetailsPOJO> imageDetailsPOJOArrayList;
    private WeakReference<View> viewWeakReference;
    private WeakReference<Context> contextWeakReference;
    private AsyncTaskStatusCallback asyncTaskStatusCallback;
    private Context context;



    public ImagesRotationTsk(Context context, ArrayList<ImageDetailsPOJO> imageDetailsPOJOArrayList, View progressBar, boolean isProgressBar, AsyncTaskStatusCallback asyncTaskStatusCallback) {
        this.path = path;
        this.file = file;
        this.bitmap = bitmap;
        this.matrix = matrix;
        this.progressBar = progressBar;
        this.isProgressBar = isProgressBar;
        this.contextWeakReference = new WeakReference<>(context);
        this.viewWeakReference = new WeakReference<>(progressBar);
        this.asyncTaskStatusCallback = asyncTaskStatusCallback;
        this.imageDetailsPOJOArrayList = imageDetailsPOJOArrayList;
        this.context = context;
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

        for (int i= 0; i<imageDetailsPOJOArrayList.size() ; i++) {

            int rotateDegree = imageDetailsPOJOArrayList.get(i).getRotateDegree();
            String path = imageDetailsPOJOArrayList.get(i).getImageUrl();
            if (rotateDegree != 0) {
                File file = new File(path);

                InputStream iStream = null;
                try {
                    iStream = context.getContentResolver().openInputStream(Uri.fromFile(file));

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                byte[] inputData = getBytes(iStream);

                Bitmap bitmap = BitmapFactory.decodeByteArray(inputData, 0, inputData.length);

                Matrix matrix = new Matrix();
                matrix.postRotate(rotateDegree);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                byte[] image = outStream.toByteArray();

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
                } catch (IOException e) {
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
            }
        }
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

    public byte[] getBytes(InputStream inputStream){
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while (true) {
            try {
                if (!((len = inputStream.read(buffer)) != -1)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }


}

