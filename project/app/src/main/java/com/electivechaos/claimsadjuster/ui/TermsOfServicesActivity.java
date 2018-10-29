package com.electivechaos.claimsadjuster.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.utils.CommonUtils;

public class TermsOfServicesActivity extends AppCompatActivity {

    private CheckBox termsOfServices;
    private Button acceptBtn, dontAcceptBtn;
    private View parentLayoutForMessages;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_of_services);

        if(CommonUtils.getTermsAndConditions(this).equals("agree")){
            Intent intentSplash = new Intent(TermsOfServicesActivity.this, SplashScreenActivity.class);
            startActivity(intentSplash);
        }


        termsOfServices = findViewById(R.id.termsAndCondition);
        acceptBtn = findViewById(R.id.acceptBtn);
        dontAcceptBtn = findViewById(R.id.dontAcceptBtn);

        parentLayoutForMessages = findViewById(R.id.parentLayoutForMessages);
        toolbar = findViewById(R.id.toolbar);

        TextView txtTos = findViewById(R.id.txtTos);
        toolbar.setTitle("Terms Of Service");


        String s = getString(R.string.tos_string);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            txtTos.setText(Html.fromHtml(s, Html.FROM_HTML_MODE_COMPACT));
        } else {
            txtTos.setText(Html.fromHtml(s));
        }


        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    Intent intentSplash = new Intent(TermsOfServicesActivity.this, SplashScreenActivity.class);
                    startActivity(intentSplash);
                }else {
                    CommonUtils.showSnackbarMessage(getString(R.string.please_check_terms_of_services), true, true,parentLayoutForMessages, TermsOfServicesActivity.this);
                }
            }
        });

        dontAcceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertForDisagreement();
            }
        });

    }

    private boolean validate() {
        if(termsOfServices.isChecked()){
            CommonUtils.setTermsAndConditions("agree",TermsOfServicesActivity.this);
            return true;
        }else {
            CommonUtils.setTermsAndConditions("disagree",TermsOfServicesActivity.this);
            return false;
        }
    }


    private void showAlertForDisagreement(){
        AlertDialog.Builder builder = new AlertDialog.Builder(TermsOfServicesActivity.this);
        builder.setTitle(R.string.disagreement_title)
                .setMessage(R.string.disagreement_msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(Intent.ACTION_MAIN);
                        i.addCategory(Intent.CATEGORY_HOME);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);;
                        startActivity(i);
                        finish();
                    }
                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
        Button negativeButton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setTextColor(ContextCompat.getColor(TermsOfServicesActivity.this,R.color.colorPrimaryDark));
        Button positiveButton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setTextColor(ContextCompat.getColor(TermsOfServicesActivity.this,R.color.colorPrimaryDark));

    }

}