package com.electivechaos.claimsadjuster.modules.login;

import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.ViewModel;

import com.electivechaos.claimsadjuster.application.App;
import com.electivechaos.claimsadjuster.interfaces.APICallbackInterface;
import com.electivechaos.claimsadjuster.models.OTPVerificationResponse;
import com.electivechaos.claimsadjuster.models.OTPVerificationResult;
import com.electivechaos.claimsadjuster.models.ResendOTPResponse;
import com.electivechaos.claimsadjuster.modules.registration.RegistrationActivity;
import com.electivechaos.claimsadjuster.router.Routable;
import com.electivechaos.claimsadjuster.utils.APIEndPoint;
import com.electivechaos.claimsadjuster.utils.ProgressDialogUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OTPVerificationViewModel extends ViewModel {
    //    private MutableLiveData<ContentPojo> contentPojo;
    private ProgressDialogUtil mProgressDialogUtil;
    private APIEndPoint mApiEndPoint = new APIEndPoint();

    public OTPVerificationViewModel(Context context) {
        mProgressDialogUtil = new ProgressDialogUtil(context);
    }

    public OTPVerificationViewModel() {

    }
//    public LiveData<ContentPojo> getContentDetails() {
//        if (contentPojo == null) {
//            contentPojo = new MutableLiveData<ContentPojo>();
//            loadContent();
//        }
//        return contentPojo;
//    }

    private void loadContent() {
        // perform an asynchronous operation to fetch users.
    }

    protected void verifyOTP(Context context, ArrayList paramList) {
        mProgressDialogUtil = new ProgressDialogUtil(context);
        try {
            JSONArray jsonArray = new JSONArray();
            JSONObject obj = new JSONObject();
            obj.put("Password", paramList.get(0).toString());
            obj.put("EmailID", paramList.get(1).toString());
            jsonArray.put(obj);

            mProgressDialogUtil.show("Verifying OTP...");
            mApiEndPoint.setApiRequestParameters("UserService.VerifyRegistrationOTP", jsonArray);
            mApiEndPoint.fetchData(new APICallbackInterface() {
                @Override
                public void apiCallCompletion(JSONObject jsonObject) {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    if (jsonObject != null) {
                        OTPVerificationResponse verificationResponse = gson.fromJson(jsonObject.toString(), OTPVerificationResponse.class);
                        if (verificationResponse != null && verificationResponse.getResult() != null) {
                            OTPVerificationResult result = verificationResponse.getResult();
                            Toast.makeText(App.getContext(), "User verified successfully...", Toast.LENGTH_SHORT).show();
                            Routable.loginScreen(context);
                            if (context instanceof LoginActivity) {
                                LoginActivity loginActivity = (LoginActivity) context;
                                loginActivity.finish();
                            } else if (context instanceof RegistrationActivity) {
                                RegistrationActivity registrationActivity = (RegistrationActivity) context;
                                registrationActivity.finish();
                            }
                        }
                        if (verificationResponse != null && verificationResponse.getError() != null) {
                            Toast.makeText(App.getContext(), verificationResponse.getError() + "", Toast.LENGTH_SHORT).show();
                        }
                    }
                    mProgressDialogUtil.hide();
                }

                @Override
                public void apiCallFailure(String error) {
                    mProgressDialogUtil.hide();
                    Toast.makeText(App.getContext(), "" + error, Toast.LENGTH_SHORT).show();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
            mProgressDialogUtil.hide();
        }

    }

    protected void resendOTP(Context context, ArrayList paramList) {
        try {
            mProgressDialogUtil = new ProgressDialogUtil(context);
            JSONArray jsonArray = new JSONArray();
            JSONObject obj = new JSONObject();
            obj.put("EmailID", paramList.get(0));
            jsonArray.put(obj);
            mProgressDialogUtil.show("Sending OTP...");
            mApiEndPoint.setApiRequestParameters("UserService.ReSendRegistrationOTP", jsonArray);
            mApiEndPoint.fetchData(new APICallbackInterface() {
                @Override
                public void apiCallCompletion(JSONObject jsonObject) {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    if (jsonObject != null) {
                        ResendOTPResponse verificationResponse = gson.fromJson(jsonObject.toString(), ResendOTPResponse.class);
                        if (verificationResponse != null && verificationResponse.getError() != null) {
                            Toast.makeText(App.getContext(), verificationResponse.getError() + "", Toast.LENGTH_SHORT).show();
                            return;
                        }

                    }
                    mProgressDialogUtil.hide();
                }

                @Override
                public void apiCallFailure(String error) {
                    mProgressDialogUtil.hide();
                    Toast.makeText(App.getContext(), "" + error, Toast.LENGTH_SHORT).show();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
            mProgressDialogUtil.hide();
        }

    }

}
