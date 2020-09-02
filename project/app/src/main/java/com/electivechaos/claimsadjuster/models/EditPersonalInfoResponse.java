package com.electivechaos.claimsadjuster.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EditPersonalInfoResponse {

    @SerializedName("result")
    @Expose
    private EditPersonalInfoResult result;
    @SerializedName("error")
    @Expose
    private Object error;
    @SerializedName("id")
    @Expose
    private String id;

    public EditPersonalInfoResult getResult() {
        return result;
    }

    public void setResult(EditPersonalInfoResult result) {
        this.result = result;
    }

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
}
