package com.electivechaos.claimsadjuster.modules.registration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

import com.android.volley.RequestQueue;
import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.application.App;
import com.electivechaos.claimsadjuster.models.PeoplePojo;
import com.electivechaos.claimsadjuster.models.UserProfileDetailsResult;
import com.electivechaos.claimsadjuster.modules.login.LoginActivity;
import com.electivechaos.claimsadjuster.router.Routable;
import com.electivechaos.claimsadjuster.utils.CommonApiCall;
import com.electivechaos.claimsadjuster.utils.CommonUtils;
import com.google.android.material.textfield.TextInputEditText;


import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    CommonApiCall commonApiCall = CommonApiCall.getInstance(this);
    private ImageView mImageProfile, mImgPassword;
    private TextInputEditText mTxtFirstName, mTxtLastName, mTxtEmail, mTxtPassword, mTxtContactNo, mTxtConfirmPassword, mTxtCompany;
    private TextView mTxtNext, mLabelNickName;
    private RequestQueue mRequestQueue;
    private String username, lastname, mFirstNAme, mEmailId, mPassword, mConfirmPassword, mConyactNo, mUserId, mCompanyName, mNickName;
    private ProgressDialog mProgressDialog;
    private boolean mIsEdit = false;
    private TextView mBtnDone, mBtnCancel;
    private LinearLayout mLlPassword, mLlConfirmPassword;
    private Bundle bundle;
    private UserProfileDetailsResult mUserProfileDetailsResult;
    private int REQUEST_PERSONAL_INFO_UPDATE = 102;
    private RegistrationViewModel mRegistrationViewModel;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        bundle = getIntent().getExtras();
        if (bundle != null) {
            mIsEdit = bundle.getBoolean("mIsEdit");
            mUserProfileDetailsResult = bundle.getParcelable("profilePojo");
        }
        initiateViews();
        mRegistrationViewModel = new ViewModelProvider(this).get(RegistrationViewModel.class);

        if (mIsEdit) {
            mTxtFirstName.setText(mUserProfileDetailsResult.getFirstName());
            mTxtLastName.setText(mUserProfileDetailsResult.getLastName());
            mTxtEmail.setText(mUserProfileDetailsResult.getEmailId());
            mTxtEmail.setEnabled(false);

        } else if (!TextUtils.isEmpty(CommonUtils.getSession(this))) {
            Routable.loginScreen(RegistrationActivity.this);
        }
    }

    private boolean inputValidations() {
        boolean status = false;
        if (TextUtils.isEmpty(mTxtFirstName.getText().toString()) || TextUtils.isEmpty(mTxtFirstName.getText().toString().trim())) {
            mTxtFirstName.setError("Please enter mFirstNAme");
            status = false;
        } else if (TextUtils.isEmpty(mTxtLastName.getText().toString()) || TextUtils.isEmpty(mTxtLastName.getText().toString().trim())) {
            mTxtLastName.setError("Please enter lastname");
            status = false;
        } else if (TextUtils.isEmpty(mTxtEmail.getText().toString()) || TextUtils.isEmpty(mTxtEmail.getText().toString().trim()) || !validateEmailId(mTxtEmail.getText().toString())) {
            mTxtEmail.setError("Please enter valid email address");
            status = false;
        } else if ((TextUtils.isEmpty(mTxtPassword.getText().toString()) && TextUtils.isEmpty(mTxtPassword.getText().toString().trim())) || mTxtPassword.getText().toString().length() < 8) {
            mTxtPassword.setError("Password must be at least 8 characters");
            status = false;
        } else if (!(mTxtPassword.getText().toString()).equals(mTxtConfirmPassword.getText().toString())) {
            mTxtConfirmPassword.setError("Password doesn't match");
            status = false;
        } else {
            return true;
        }

        return status;
    }

    private boolean validateEmailId(String mEmailId) {
        if (!TextUtils.isEmpty(mEmailId) && !TextUtils.isEmpty(mEmailId.trim())) {
            String email = mEmailId.trim();
            String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
            if (email.matches(emailPattern)) {
                return true;
            } else {
                Toast.makeText(App.getContext(), "Enter valid email address.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return false;
    }


    private void getEditextValues() {
        mFirstNAme = mTxtFirstName.getText().toString();
        lastname = mTxtLastName.getText().toString();
        username = mFirstNAme + " " + lastname;
        mEmailId = mTxtEmail.getText().toString();
        mPassword = mTxtPassword.getText().toString();
        mConfirmPassword = mTxtConfirmPassword.getText().toString();
        mCompanyName = mTxtCompany.getText().toString();
    }

    private void initiateViews() {
        if (mIsEdit) {
            mTxtFirstName = findViewById(R.id.txtFirstName);
            mTxtLastName = findViewById(R.id.textLastName);
            mTxtEmail = findViewById(R.id.textEmail);
            mTxtCompany = findViewById(R.id.textCompany);
            mBtnDone = findViewById(R.id.btnDone);
            mBtnCancel = findViewById(R.id.btnCancel);
            mTxtPassword = findViewById(R.id.textPassword);
            mTxtConfirmPassword = findViewById(R.id.textConfirmPassword);
            mImgPassword = findViewById(R.id.imgPassword);
            mLlPassword = findViewById(R.id.llPassword);
            mLlConfirmPassword = findViewById(R.id.llConfirmPassword);
            mLlPassword.setVisibility(View.GONE);
            mLlConfirmPassword.setVisibility(View.GONE);
        } else {
            mTxtFirstName = findViewById(R.id.txtFirstName);
            mTxtLastName = findViewById(R.id.textLastName);
            mTxtEmail = findViewById(R.id.textEmail);
            mTxtCompany = findViewById(R.id.textCompany);
            mBtnDone = findViewById(R.id.btnDone);
            mBtnCancel = findViewById(R.id.btnCancel);
            mTxtPassword = findViewById(R.id.textPassword);
            mTxtConfirmPassword = findViewById(R.id.textConfirmPassword);
            mImgPassword = findViewById(R.id.imgPassword);
            mLlPassword = findViewById(R.id.llPassword);
            mLlConfirmPassword = findViewById(R.id.llConfirmPassword);
        }

        mImageProfile = findViewById(R.id.imageProfile);
        mBtnDone.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnDone: {
                getEditextValues();
                inputValidations();
                if (inputValidations()) {
                    PeoplePojo userPojo = new PeoplePojo();
                    userPojo.setFirstName(mFirstNAme);
                    userPojo.setLastName(lastname);
                    userPojo.setPassword(mPassword);
                    userPojo.setPrimaryEmail(mEmailId);
                    if (!mIsEdit) {
                        mRegistrationViewModel.registerUser(RegistrationActivity.this, userPojo);
                    } else {
                        mRegistrationViewModel.editUserRegistration(RegistrationActivity.this, userPojo);
                    }
                }
            }
            break;
            case R.id.btnCancel: {
                if (!mIsEdit) {
                    Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                    intent.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    finish();
                }
            }
            v.setBackgroundColor(getResources().getColor(R.color.grayLight));
            break;

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!mIsEdit) {
            Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
            intent.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            finish();
        }
    }
}
