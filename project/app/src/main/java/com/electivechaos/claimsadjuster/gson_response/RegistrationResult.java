package com.electivechaos.claimsadjuster.gson_response;

import com.google.gson.annotations.SerializedName;

public class RegistrationResult {
    @SerializedName("AppID")
    String appID;

    public String getAppID() {
        return appID;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }
}
