package com.electivechaos.claimsadjuster.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LogInResponse {
    @SerializedName("result")
    @Expose
    private LogInResult result;
    @SerializedName("error")
    @Expose
    private Object error;
    @SerializedName("id")
    @Expose
    private String id;

    public LogInResult getResult() {
        return result;
    }

    public void setResult(LogInResult result) {
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
