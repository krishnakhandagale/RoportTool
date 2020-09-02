package com.electivechaos.claimsadjuster.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResendOTPResponse {
    @SerializedName("result")
    @Expose
    private ResendOTPResult result;
    @SerializedName("error")
    @Expose
    private Object error;
    @SerializedName("id")
    @Expose
    private Integer id;

    public ResendOTPResult getResult() {
        return result;
    }

    public void setResult(ResendOTPResult result) {
        this.result = result;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
