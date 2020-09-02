package com.electivechaos.claimsadjuster.router;

import android.content.Context;
import android.content.Intent;

import com.electivechaos.claimsadjuster.modules.login.LoginActivity;
import com.electivechaos.claimsadjuster.modules.login.OTPVerificationActivity;
import com.electivechaos.claimsadjuster.modules.registration.RegistrationActivity;
import com.electivechaos.claimsadjuster.ui.MainTabsActivity;
import com.electivechaos.claimsadjuster.ui.TermsOfServicesActivity;


public class Routable {

    public static void signUp(Context context) {
        Intent intent = new Intent(context, RegistrationActivity.class);
        context.startActivity(intent);
    }

    public static void otpVerification(Context context, String emailId) {
        Intent intent = new Intent(context, OTPVerificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("emailId", emailId);
        context.startActivity(intent);
    }

    public static void loginScreen(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    public static void mainTabActivity(LoginActivity loginActivity) {
        Intent intent = new Intent(loginActivity, MainTabsActivity.class);
        loginActivity.startActivity(intent);
        loginActivity.finish();
    }
    public static void tosScreen(LoginActivity loginActivity) {
        Intent intent = new Intent(loginActivity, TermsOfServicesActivity.class);
        loginActivity.startActivity(intent);
        loginActivity.finish();
    }
}
