package com.electivechaos.claimsadjuster.interfaces;

public interface AsyncTaskStatusCallbackForNotes {

    void onPostExecute(Object object, String type);

    void onPreExecute();

    void onProgress(int progress);
}
