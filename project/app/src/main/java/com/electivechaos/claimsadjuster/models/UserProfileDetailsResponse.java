package com.electivechaos.claimsadjuster.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserProfileDetailsResponse {

    @SerializedName("error")
    @Expose
    private Object error;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("result")
    @Expose
    private UserProfileDetailsResult result;

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserProfileDetailsResult getResult() {
        return result;
    }

    public void setResult(UserProfileDetailsResult result) {
        this.result = result;
    }

}
