package com.electivechaos.claimsadjuster.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.electivechaos.claimsadjuster.Constants;
import com.electivechaos.claimsadjuster.application.App;
import com.electivechaos.claimsadjuster.interfaces.APICallbackInterface;
import com.electivechaos.claimsadjuster.interfaces.APIReturnListCallbackInterface;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class CommonApiCall {
    private static CommonApiCall mCommonApiCallInstance;
    private static Context mContext;
    private RequestQueue mRequestQueue;


    public CommonApiCall(Context context) {
        mContext = context;         // Specify the application context
        mRequestQueue = getRequestQueue();// Get the request queue
    }

    public static synchronized CommonApiCall getInstance(Context context) {
        // If Instance is null then initialize new Instance
        if (mCommonApiCallInstance == null) {
            mCommonApiCallInstance = new CommonApiCall(context);
        }
        // Return MySingleton new Instance
        return mCommonApiCallInstance;
    }

    public RequestQueue getRequestQueue() {
        // If RequestQueue is null the initialize new RequestQueue
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(App.getContext());
        }

        // Return RequestQueue
        return mRequestQueue;
    }


    public <T> void addToRequestQueue(Request<T> request) {
        // Add the specified request to the request queue
        getRequestQueue().add(request);
    }

    public void getData(RequestQueue requestQueue, String methodName, JSONArray jsonArray, APICallbackInterface apiCallbackInterface) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", 1);
        jsonObject.put("method", methodName);
        jsonObject.put("params", jsonArray);

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                Constants.SERVICE_URL, jsonObject, response -> {
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

//        jsonObjectRequest.setShouldCache(true);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjectRequest);
//        CommonApiCall.getInstance(mContext).addToRequestQueue(jsonObjectRequest);

    }

    public void getDataAndSetTag(RequestQueue requestQueue, String methodName, JSONArray jsonArray, String TAG, APICallbackInterface apiCallbackInterface) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", 1);
        jsonObject.put("method", methodName);
        jsonObject.put("params", jsonArray);

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                Constants.SERVICE_URL, jsonObject, response -> {
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

//        jsonObjectRequest.setShouldCache(true);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonObjectRequest.setTag(TAG);
//        requestQueue.add(jsonObjectRequest);
        CommonApiCall.getInstance(mContext).addToRequestQueue(jsonObjectRequest);

    }


    public void getData(RequestQueue requestQueue, String methodName, JSONArray jsonArray, APIReturnListCallbackInterface apiCallbackInterface) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", 1);
        jsonObject.put("method", methodName);
        jsonObject.put("params", jsonArray);

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                Constants.SERVICE_URL, jsonObject, response -> {
            if (response != null) {
                apiCallbackInterface.apiCallCompletion(response);
            } else {
            }

        }, volleyError -> {
            if (CommonUtils.getRequestError(volleyError) != null) {
                Log.d("API RESPONSE:", "error:: null");
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
        CommonApiCall.getInstance(mContext).addToRequestQueue(jsonObjectRequest);
//        requestQueue.add(jsonObjectRequest);

    }

}
