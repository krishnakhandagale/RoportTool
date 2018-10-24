package com.electivechaos.claimsadjuster.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.electivechaos.claimsadjuster.Constants;
import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.gson_response.RegistrationResponse;
import com.electivechaos.claimsadjuster.utils.CommonUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {

    private View parentLayoutForMessages;
    private RequestQueue requestQueue;
    private static final Object REGISTRATION_REQUEST_TAG = "REGISTRATION_REQUEST_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        if(!TextUtils.isEmpty(CommonUtils.getSession(getApplicationContext()))){
            Intent intent = new Intent(RegistrationActivity.this, MainTabsActivity.class);
            startActivity(intent);
            finish();
        }

        parentLayoutForMessages = findViewById(R.id.parentLayoutForMessages);

        final EditText reportByEditText = findViewById(R.id.reportBy);
        final EditText emailIdEditText = findViewById(R.id.emailId);
        final EditText tokenEditText = findViewById(R.id.token);


        Button registerBtn = findViewById(R.id.btnRegister);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 String reportByString = reportByEditText.getText().toString();
                 String emailIdString = emailIdEditText.getText().toString();
                 String tokenString = tokenEditText.getText().toString().trim();

                if(validate(reportByString, emailIdString)){
                    CommonUtils.setEmailId(emailIdString, RegistrationActivity.this);
                    CommonUtils.setReportByField(reportByString, RegistrationActivity.this);

                    try {
                        sendTokenEmailId(tokenString,emailIdString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

//                    Intent i = new Intent(RegistrationActivity.this,MainTabsActivity.class);
//                    startActivity(i);
//                    finish();
                }
            }
        });


    }

    private boolean validate(String reportBy, String emailId) {
        if(reportBy.trim().isEmpty()){
            CommonUtils.showSnackbarMessage(getString(R.string.please_enter_report_by), true, true,parentLayoutForMessages, RegistrationActivity.this);
            return false;
        }
        else if(emailId.trim().isEmpty()) {
            CommonUtils.showSnackbarMessage(getString(R.string.please_enter_email_id), true, true,parentLayoutForMessages, RegistrationActivity.this);
            return false;
        }else if (!validateEmailId(emailId)){
            CommonUtils.showSnackbarMessage(getString(R.string.please_enter_valid_email_id), true, true,parentLayoutForMessages, RegistrationActivity.this);
            return false;
        }
        else {
            return true;
        }
    }

    private boolean validateEmailId(String emailId) {
         final Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailId);
        return matcher.find();
    }


    public void sendTokenEmailId(final String token, final String emailId) throws JSONException {

//       requestQueue = Volley.newRequestQueue(this,new HurlStack(null,CommonUtils.getSocketFactory(this)));
        requestQueue = Volley.newRequestQueue(this);

        JSONObject obj = new JSONObject();
        obj.put("EmailId",emailId);
        obj.put("Token",token);
        obj.put("DeviceType","android");
        obj.put("DomainID","electivechaos");
        JSONArray jsonArray = new JSONArray();

        jsonArray.put(obj);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", 1);
        jsonObject.put("method", "MobileApiServices.Register");
        jsonObject.put("params", jsonArray);

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                Constants.SERVICE_URL,jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Gson gson =  new GsonBuilder().setPrettyPrinting().create();
                        RegistrationResponse registrationResponse =  gson.fromJson(response.toString(), RegistrationResponse.class);

                        if(TextUtils.isEmpty(registrationResponse.getError())){
                            if(registrationResponse.getResult() != null){
                                if(registrationResponse.getResult().getAppID().isEmpty()){
                                    Toast.makeText(RegistrationActivity.this, "Please enter valid Token & Email.",Toast.LENGTH_SHORT).show();
                                }else {
                                    CommonUtils.setSession(registrationResponse.getResult().getAppID(), RegistrationActivity.this);
                                    Toast.makeText(RegistrationActivity.this, "Registered successfully.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegistrationActivity.this, MainTabsActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }else{
                            Toast.makeText(RegistrationActivity.this,registrationResponse.getError(),Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if(CommonUtils.getRequestError(volleyError)!= null){
                    Toast.makeText(RegistrationActivity.this,CommonUtils.getRequestError(volleyError),Toast.LENGTH_SHORT).show();
                }

            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        jsonObjectRequest.setTag(REGISTRATION_REQUEST_TAG);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonObjectRequest);

    }


}
