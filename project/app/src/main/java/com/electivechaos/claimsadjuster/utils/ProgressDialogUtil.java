package com.electivechaos.claimsadjuster.utils;

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressDialogUtil {
    Context context;
    ProgressDialog mProgressDialog;

    public ProgressDialogUtil(Context context) {
        this.context = context;
    }

    public void show(String message) {
        mProgressDialog = new ProgressDialog(this.context);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    public void hide() {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
    }

}
