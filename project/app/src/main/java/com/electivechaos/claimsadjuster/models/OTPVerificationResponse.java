package com.electivechaos.claimsadjuster.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OTPVerificationResponse {
    @SerializedName("result")
    @Expose
    private OTPVerificationResult result;
    @SerializedName("error")
    @Expose
    private Object error;
    @SerializedName("id")
    @Expose
    private Integer id;

    public OTPVerificationResult getResult() {
        return result;
    }

    public void setResult(OTPVerificationResult result) {
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
