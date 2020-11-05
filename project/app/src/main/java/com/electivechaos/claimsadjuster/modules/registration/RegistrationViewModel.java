package com.electivechaos.claimsadjuster.modules.registration;

import android.text.TextUtils;
import android.widget.Toast;

import androidx.lifecycle.ViewModel;

import com.electivechaos.claimsadjuster.Constants;
import com.electivechaos.claimsadjuster.application.App;
import com.electivechaos.claimsadjuster.gson_response.RegistrationResponse;
import com.electivechaos.claimsadjuster.interfaces.APICallbackInterface;
import com.electivechaos.claimsadjuster.models.EditPersonalInfoResponse;
import com.electivechaos.claimsadjuster.models.PeoplePojo;
import com.electivechaos.claimsadjuster.router.Routable;
import com.electivechaos.claimsadjuster.utils.APIEndPoint;
import com.electivechaos.claimsadjuster.utils.CommonUtils;
import com.electivechaos.claimsadjuster.utils.ProgressDialogUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RegistrationViewModel extends ViewModel {
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

    protected void registerUser(RegistrationActivity registrationActivity, PeoplePojo userPojo) {
        mProgressDialogUtil = new ProgressDialogUtil(registrationActivity);
        try {
            JSONArray jsonArray = new JSONArray();
            JSONObject obj = new JSONObject();

            obj.put("DomainID", Constants.DOMAIN_ID);
            obj.put("ContactNumber", "");
            obj.put("IsRegister", true);
            obj.put("IsMobileRequest", true);
            obj.put("LastName", userPojo.getLastName());
            obj.put("FirstName", userPojo.getFirstName());
            obj.put("Password", userPojo.getPassword());
            obj.put("PrimaryEmail", userPojo.getPrimaryEmail());
            obj.put("PermittedTo", "Publisher");
            obj.put("BundleID", Constants.BUNDLE_ID);
            String emailParts[] = userPojo.getPrimaryEmail().split("@");
            obj.put("Username", emailParts[0]);
            obj.put("IsOTPFLow", true);
            jsonArray.put(obj);

            mProgressDialogUtil.show("Loading...");
            mApiEndPoint.setApiRequestParameters("UserService.AddUserForChecklistClaims", jsonArray);
            mApiEndPoint.fetchData(new APICallbackInterface() {
                @Override
                public void apiCallCompletion(JSONObject jsonObject) {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    if (jsonObject != null) {
                        RegistrationResponse registrationResponse = gson.fromJson(jsonObject.toString(), RegistrationResponse.class);
                        if (registrationResponse != null) {
                            if (registrationResponse.getResult() != null) {
                                String userID = registrationResponse.getResult().getUserID();
                                if (!TextUtils.isEmpty(userID)) {
                                    Routable.otpVerification(registrationActivity, userPojo.getPrimaryEmail());
                                    registrationActivity.finish();
                                }
                            }
                            if (registrationResponse.getError() != null) {
                                Toast.makeText(registrationActivity, registrationResponse.getError() + "", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    mProgressDialogUtil.hide();
                }

                @Override
                public void apiCallFailure(String error) {
                    mProgressDialogUtil.hide();
                    Toast.makeText(registrationActivity, "" + error, Toast.LENGTH_SHORT).show();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
            mProgressDialogUtil.hide();
        }
    }

    protected void editUserRegistration(RegistrationActivity registrationActivity, PeoplePojo userPojo) {
        mProgressDialogUtil = new ProgressDialogUtil(registrationActivity);
        try {
            JSONArray jsonArray = new JSONArray();
            JSONObject obj = new JSONObject();
            obj.put("DomainID", Constants.DOMAIN_ID);
            obj.put("DomainId", Constants.DOMAIN_ID);
            obj.put("ContactNumber", "");
            obj.put("BusinessAccountID", CommonUtils.getBuisnessAccountID(App.getContext()));
            obj.put("FirstName", userPojo.getFirstName());
            obj.put("LastName", userPojo.getLastName());
            obj.put("UserID", CommonUtils.getSession(App.getContext()));
            obj.put("UserId", CommonUtils.getSession(App.getContext()));
            String emailParts[] = userPojo.getPrimaryEmail().split("@");
            obj.put("Username", emailParts[0]);
            obj.put("IsRemoveThumbnail", false);
            jsonArray.put(obj);
            mProgressDialogUtil.show("Loading...");
            mApiEndPoint.setApiRequestParameters("UserService.Update", jsonArray);
            mApiEndPoint.fetchData(new APICallbackInterface() {
                @Override
                public void apiCallCompletion(JSONObject jsonObject) {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    if (jsonObject != null) {
                        EditPersonalInfoResponse editPersonalInfoResponse = gson.fromJson(jsonObject.toString(), EditPersonalInfoResponse.class);
                        Toast.makeText(registrationActivity, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    }
                    mProgressDialogUtil.hide();
                }

                @Override
                public void apiCallFailure(String error) {
                    mProgressDialogUtil.hide();
                    Toast.makeText(registrationActivity, "" + error, Toast.LENGTH_SHORT).show();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
            mProgressDialogUtil.hide();
        }

    }
}
