package com.electivechaos.claimsadjuster.modules.login;

import android.widget.Toast;

import androidx.lifecycle.ViewModel;

import com.electivechaos.claimsadjuster.Constants;
import com.electivechaos.claimsadjuster.interfaces.APICallbackInterface;
import com.electivechaos.claimsadjuster.models.LogInResponse;
import com.electivechaos.claimsadjuster.router.Routable;
import com.electivechaos.claimsadjuster.utils.APIEndPoint;
import com.electivechaos.claimsadjuster.utils.CommonUtils;
import com.electivechaos.claimsadjuster.utils.ProgressDialogUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginViewModel extends ViewModel {
    //    private MutableLiveData<ContentPojo> contentPojo;
    private ProgressDialogUtil mProgressDialogUtil;
    private APIEndPoint mApiEndPoint = new APIEndPoint();


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

    protected void loginUser(String emailID, String password, LoginActivity loginActivity) {
        mProgressDialogUtil = new ProgressDialogUtil(loginActivity);
        try {
            JSONArray jsonArray = new JSONArray();
            JSONObject obj = new JSONObject();

            JSONObject objDevice = new JSONObject();
            objDevice.put("Platform", Constants.DEVICE_NAME);
            obj.put("Device", objDevice);
            obj.put("EmailID", emailID);
            obj.put("Password", password);

            jsonArray.put(obj);

            mProgressDialogUtil.show("Loading...");
            mApiEndPoint.setApiRequestParameters("AuthenticationService.SignIn", jsonArray);
            mApiEndPoint.fetchData(new APICallbackInterface() {
                @Override
                public void apiCallCompletion(JSONObject jsonObject) {
                    mProgressDialogUtil.hide();
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    LogInResponse loginResponse = gson.fromJson(jsonObject.toString(), LogInResponse.class);
                    if (loginResponse != null) {
                        if (loginResponse.getResult() != null) {
                            CommonUtils.setUserDefaultValues(loginResponse.getResult(), loginActivity);
                            CommonUtils.setEmailId(emailID, loginActivity);

                            if (!loginResponse.getResult().getVerifiedUser()) {
                                Routable.otpVerification(loginActivity, emailID);
                                mProgressDialogUtil.hide();
                                return;
                            }

                            if (loginResponse.getResult() != null && loginResponse.getResult().getUserInfo() != null) {
                                String fullName = loginResponse.getResult().getUserInfo().getFirstName() + " " + loginResponse.getResult().getUserInfo().getLastName();
                                CommonUtils.setReportByField(fullName, loginActivity);
                            }

                            Routable.tosScreen(loginActivity);
                            loginActivity.finish();
                        }
                        if (loginResponse.getError() != null)
                            Toast.makeText(loginActivity, loginResponse.getError().toString(), Toast.LENGTH_SHORT).show();
                    }
                    mProgressDialogUtil.hide();
                }

                @Override
                public void apiCallFailure(String error) {
                    mProgressDialogUtil.hide();
                    Toast.makeText(loginActivity, error, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (JSONException e) {
            mProgressDialogUtil.hide();
            e.printStackTrace();
        }
    }
}
