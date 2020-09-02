package com.electivechaos.claimsadjuster.interfaces;

import org.json.JSONObject;

import java.util.List;

public interface APIReturnListCallbackInterface {
    List apiCallCompletion(JSONObject jsonObject);
    void apiCallFailure(String error);
}
