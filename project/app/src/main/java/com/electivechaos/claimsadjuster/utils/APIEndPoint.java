package com.electivechaos.claimsadjuster.utils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.electivechaos.claimsadjuster.Constants;
import com.electivechaos.claimsadjuster.application.App;
import com.electivechaos.claimsadjuster.interfaces.APICallbackInterface;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class APIEndPoint {
    private static RequestQueue mRequestQueue = Volley.newRequestQueue(App.getContext());
    private JSONObject apiRequestParameters;


    public JSONObject getApiRequestParameters() {
        return apiRequestParameters;
    }

    public void setApiRequestParameters(String methodName, JSONArray methodParameters) {
        try {
            apiRequestParameters = new JSONObject();
            this.apiRequestParameters.put("id", 1);
            this.apiRequestParameters.put("method", methodName);
            this.apiRequestParameters.put("params", methodParameters);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void fetchData(APICallbackInterface apiCallbackInterface) throws JSONException {
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                Constants.SERVICE_URL, getApiRequestParameters(), response -> {
            if (response != null) {
                apiCallbackInterface.apiCallCompletion(response);
            } else {
            }

        }, volleyError -> {
            if (CommonUtils.getRequestError(volleyError) != null) {
                apiCallbackInterface.apiCallFailure(CommonUtils.getRequestError(volleyError));
            }

        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(jsonObjectRequest);
    }
}
