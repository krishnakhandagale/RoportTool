package com.electivechaos.claimsadjuster.modules.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProvider;

import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.application.App;
import com.electivechaos.claimsadjuster.router.Routable;
import com.electivechaos.claimsadjuster.utils.CommonUtils;
import com.electivechaos.claimsadjuster.utils.KeyboardUtils;

public class LoginActivity extends AppCompatActivity {
    private EditText mTextEmail, mTextPassword;
    private AppCompatButton mBtnLogin;
    private TextView mTxtTermsOfServices, mTxtPrivacyPolicy, mTxtSignUp;
    private LoginViewModel mLoginViewModel;
    private View mParentLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mLoginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        if (CommonUtils.getUserDefaultValues(App.getContext()) != null && !TextUtils.isEmpty(CommonUtils.getUserDefaultValues(App.getContext()).get("userId")) && CommonUtils.getTermsAndConditions(this).equals("agree")) {
            Routable.mainTabActivity(LoginActivity.this);
            finish();
        } else if ((TextUtils.isEmpty(CommonUtils.getTermsAndConditions(this)) || CommonUtils.getTermsAndConditions(this).equals("disagree")) && (CommonUtils.getUserDefaultValues(App.getContext()) != null && !TextUtils.isEmpty(CommonUtils.getUserDefaultValues(App.getContext()).get("userId")))) {
            Routable.tosScreen(LoginActivity.this);
            finish();
        }
        initiateViews();
        clickListener();
    }


    private void initiateViews() {
        mTextEmail = findViewById(R.id.textEmail);
        mTextPassword = findViewById(R.id.textPassword);
        mBtnLogin = findViewById(R.id.btnLogin);
        mTxtSignUp = findViewById(R.id.txtSignUp);
        mTxtTermsOfServices = findViewById(R.id.txtTermsOfServices);
//        mTxtPrivacyPolicy = findViewById(R.id.txtPrivacyPolicy);
        mParentLayout = findViewById(R.id.parentLayout);
    }

    private void clickListener() {
        tosClick();
        privacyPolicyClick();
        registerClick();
        loginClick();
    }

    private void registerClick() {
        mTxtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Routable.signUp(LoginActivity.this);
            }
        });
    }

    private void privacyPolicyClick() {
//        mTxtPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Routable.privacyPolicy(LoginActivity.this);
//            }
//        });
    }

    private void tosClick() {
        mTxtTermsOfServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Routable.termsOfService(LoginActivity.this);
            }
        });
    }

    private void loginClick() {
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KeyboardUtils.hideSoftInput(LoginActivity.this);
                validateAndLogin();
            }
        });
    }

    private void validateAndLogin() {
        String mEmailId = mTextEmail.getText().toString();
        String mPassword = mTextPassword.getText().toString();
        if (TextUtils.isEmpty(mEmailId) || TextUtils.isEmpty(mPassword)) {
            CommonUtils.showSnackbarMessage(getResources().getString(R.string.enter_valid_email), true, true, mParentLayout, LoginActivity.this);
        } else {
            mLoginViewModel.loginUser(mEmailId, mPassword, LoginActivity.this);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        appExitAlert("YES");
    }

    private void appExitAlert(String confirmBtnName) {
//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
//        LayoutInflater inflater = this.getLayoutInflater();
//        View dialogView = inflater.inflate(R.layout.custom_alert_dialog, null);
//        final TextView txtDone = dialogView.findViewById(R.id.txtDone);
//        final TextView txtCancel = dialogView.findViewById(R.id.txtCancel);
//        final TextView txtMessage = dialogView.findViewById(R.id.txtAlertMessage);
//
//        txtDone.setText(confirmBtnName);
//        dialogBuilder.setView(dialogView);
//        dialogBuilder.setCancelable(true);
//        AlertDialog dialog = dialogBuilder.show();
//
//        txtCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//                CommonUtils.hideKeyboard(LoginActivity.this);
//            }
//        });
//
//        txtDone.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                LoginActivity.this.finish();
//                CommonUtils.hideKeyboard(LoginActivity.this);
//                dialog.dismiss();
//
//            }
//        });
//
//        dialog.setCancelable(false);
//        Window window = dialog.getWindow();
//        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        CommonUtils.unlockOrientation(LoginActivity.this);
    }
}

