package com.electivechaos.claimsadjuster.interfaces;

import org.json.JSONObject;

public interface APICallbackInterface {
    void apiCallCompletion(JSONObject jsonObject);
    void apiCallFailure(String error);
}
