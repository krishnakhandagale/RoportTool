package com.electivechaos.claimsadjuster.interfaces;

public interface AsyncTaskStatusCallback {

    void onPostExecute(Object object, String type);

    void onPreExecute();

    void onProgress(int progress);
}
