package com.electivechaos.claimsadjuster.gson_response;

public class RegistrationResponse {
    String id;
    String error;
    RegistrationResult result;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public RegistrationResult getResult() {
        return result;
    }

    public void setResult(RegistrationResult result) {
        this.result = result;
    }
}

