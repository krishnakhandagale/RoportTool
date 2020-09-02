package com.electivechaos.claimsadjuster.application;

import android.app.Application;
import android.content.Context;

public class App extends Application {

    private static Context mContext;


    public static Context getContext() {
        return mContext;
    }

    public static void setContext(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }
}