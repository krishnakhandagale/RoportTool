package com.electivechaos.claimsadjuster.interfaces;

import java.util.Map;

public interface APIReturnCallbackInterface {
    void apiCallCompletion(String jsonObject, Map<String, String>[] maps);
    void apiCallFailure(String error);
}
