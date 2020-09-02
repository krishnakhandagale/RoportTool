package com.electivechaos.claimsadjuster.modules.login;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.utils.CommonApiCall;
import com.electivechaos.claimsadjuster.utils.CommonUtils;

import java.util.ArrayList;

import static android.view.KeyEvent.KEYCODE_DEL;

public class OTPVerificationActivity extends AppCompatActivity implements OTPEditTextListener {

    private OTPEditText[] otpETs = new OTPEditText[6];
    private AppCompatButton mBtnVerifyOtp;
    private View mParentLayout;
    private CommonApiCall mCommonApiCall = CommonApiCall.getInstance(this);
    private ProgressDialog mProgressDialog;
    private String mEmailId;
    private TextView mTxtResendOTP;
    private ImageView mImgBack;
    private OTPVerificationViewModel mOtpVerificationViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification);
        if (getIntent().getExtras() != null) {
            mEmailId = getIntent().getExtras().getString("emailId");
        }
        initiateViews();
        mOtpVerificationViewModel = new ViewModelProvider(this).get(OTPVerificationViewModel.class);

        btnVerifyOTPClick();
        txtResentOTPClick();

        otpETs[0].addListener(this);
        otpETs[1].addListener(this);
        otpETs[2].addListener(this);
        otpETs[3].addListener(this);
        otpETs[4].addListener(this);
        otpETs[5].addListener(this);

    }

    private void txtResentOTPClick() {
        mTxtResendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendOTP();
            }
        });
    }


    private void pasteOTP() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData pData = clipboard.getPrimaryClip();
        if (pData != null) {
            ClipData.Item item = pData.getItemAt(0);
            if (!item.getText().toString().matches("\\d+(?:\\.\\d+)?") || item.getText().length() != 6) {
                return;
            }
            int temp, digit, count = 5, num = Integer.parseInt(item.getText().toString());
            temp = num;
            while (num > 0) {
                num = num / 10;
            }

            while (temp > 0) {
                digit = temp % 10;
                otpETs[count].setText(String.valueOf(digit));
                System.out.println("Digit at place " + count + " is: " + digit);
                temp = temp / 10;
                count--;
            }

        }
    }

    private void btnVerifyOTPClick() {

        mBtnVerifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String digitOne, digitTwo, digitThree, digitFour, digitFive, digitSix;
                digitOne = otpETs[0].getText().toString();
                digitTwo = otpETs[1].getText().toString();
                digitThree = otpETs[2].getText().toString();
                digitFour = otpETs[3].getText().toString();
                digitFive = otpETs[4].getText().toString();
                digitSix = otpETs[5].getText().toString();
                if (checkDigit(digitOne) && checkDigit(digitTwo) && checkDigit(digitThree) && checkDigit(digitFour) && checkDigit(digitFive) && checkDigit(digitSix)) {
                    verifyOTP(digitOne + digitTwo + digitThree + digitFour + digitFive + digitSix);
                } else {
                    CommonUtils.hideKeyboard(OTPVerificationActivity.this);
                    CommonUtils.showSnackbarMessage("Enter six digit valid OTP", true, true, mParentLayout, OTPVerificationActivity.this);

                }
            }
        });
    }

    private boolean checkDigit(String digit) {
        if (!TextUtils.isEmpty(digit) && !TextUtils.isEmpty(digit.trim())) {
            return true;
        } else {
            return false;
        }
    }

    private void initiateViews() {
        otpETs[0] = findViewById(R.id.otpET1);
        otpETs[1] = findViewById(R.id.otpET2);
        otpETs[2] = findViewById(R.id.otpET3);
        otpETs[3] = findViewById(R.id.otpET4);
        otpETs[4] = findViewById(R.id.otpET5);
        otpETs[5] = findViewById(R.id.otpET6);
        mBtnVerifyOtp = findViewById(R.id.btnOtpVerify);
        mParentLayout = findViewById(R.id.parentLayout);
        mTxtResendOTP = findViewById(R.id.txtResendOTP);
        mImgBack = findViewById(R.id.imgBack);

        setListeners();
        imgBackClick();
    }

    private void imgBackClick() {
        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void setListeners() {
        otpETs[0].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (otpETs[0].length() == 1) {
                    otpETs[0].clearFocus();
                    otpETs[1].requestFocus();
                    otpETs[1].setCursorVisible(true);

                }
            }
        });

        otpETs[1].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (otpETs[1].length() == 1) {
                    otpETs[1].clearFocus();
                    otpETs[2].requestFocus();
                    otpETs[2].setCursorVisible(true);

                }
            }
        });

        otpETs[2].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (otpETs[2].length() == 1) {
                    otpETs[2].clearFocus();
                    otpETs[3].requestFocus();
                    otpETs[3].setCursorVisible(true);

                }
            }
        });

        otpETs[3].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (otpETs[3].length() == 1) {
                    otpETs[3].clearFocus();
                    otpETs[4].requestFocus();
                    otpETs[4].setCursorVisible(true);

                }
            }
        });

        otpETs[4].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (otpETs[4].length() == 1) {
                    otpETs[4].clearFocus();
                    otpETs[5].requestFocus();
                    otpETs[5].setCursorVisible(true);

                }
            }
        });


    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (keyCode == 7 || keyCode == 8 ||
                keyCode == 9 || keyCode == 10 ||
                keyCode == 11 || keyCode == 12 ||
                keyCode == 13 || keyCode == 14 ||
                keyCode == 15 || keyCode == 16 ||
                keyCode == 67) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KEYCODE_DEL) {
                    int index = checkWhoHasFocus();
                    if (index != 123) {
                        if (Helpers.rS(otpETs[index]).equals("")) {
                            if (index != 0) {
                                otpETs[index - 1].requestFocus();
                            }
                        } else {
                            return super.dispatchKeyEvent(event);
                        }
                    }
                } else {
                    int index = checkWhoHasFocus();
                    if (index != 123) {
                        if (Helpers.rS(otpETs[index]).equals("")) {
                            return super.dispatchKeyEvent(event);
                        } else {
                            if (index != 5) {
                                otpETs[index + 1].requestFocus();
                            }
                        }
                    }
                    return super.dispatchKeyEvent(event);
                }
            }
        } else {
            return super.dispatchKeyEvent(event);
        }
        return true;
    }

    private int checkWhoHasFocus() {
        for (int i = 0; i < otpETs.length; i++) {
            EditText tempET = otpETs[i];
            if (tempET.hasFocus()) {
                return i;
            }
        }
        return 123;
    }

    private void verifyOTP(String otp) {
        ArrayList paramList = new ArrayList();
        paramList.add(otp);
        paramList.add(mEmailId);
        mOtpVerificationViewModel.verifyOTP(OTPVerificationActivity.this, paramList);

    }

    private void resendOTP() {
        ArrayList paramList = new ArrayList();
        paramList.add(mEmailId);
        mOtpVerificationViewModel.resendOTP(OTPVerificationActivity.this, paramList);
    }

    @Override
    public void onUpdate() {
        pasteOTP();
    }
}

