package com.feiyang.elocker.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.feiyang.elocker.Constant;
import com.feiyang.elocker.R;
import com.feiyang.elocker.rest.UserRest;
import com.feiyang.elocker.util.MD5Util;
import com.feiyang.elocker.util.TimerUtil;

import java.lang.ref.WeakReference;

public class ForgetPassActivity extends AppCompatActivity implements View.OnFocusChangeListener,
        View.OnClickListener {

    private EditText mPhoneNum, mPassword1, mPassword2, mCode;
    private Button mGetCodeBtn, mSubmitBtn;
    private ForgetPassHandler mHandler;
    private TimerUtil mTimerUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass);

        /*设置返回按钮*/
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.reset_pass);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mPhoneNum = findViewById(R.id.forget_pass_phone_num);
        mPassword1 = findViewById(R.id.forget_pass_password1);
        mPassword2 = findViewById(R.id.forget_pass_password2);
        mCode = findViewById(R.id.forget_pass_code);
        mGetCodeBtn = findViewById(R.id.forget_pass_get_code);
        mSubmitBtn = findViewById(R.id.forget_pass_submit_btn);
        mHandler = new ForgetPassHandler(this);

        mPhoneNum.setOnFocusChangeListener(this);
        mPassword1.setOnFocusChangeListener(this);
        mPassword2.setOnFocusChangeListener(this);
        mCode.setOnFocusChangeListener(this);

        mGetCodeBtn.setOnClickListener(this);
        mSubmitBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Context context = v.getContext();
        String phoneNum = mPhoneNum.getText().toString();
        UserRest userRest = new UserRest(context, mHandler);
        switch (v.getId()) {
            case R.id.forget_pass_get_code:
                if (phoneNum == null || phoneNum.equals("")
                        || phoneNum.length() < 6 || this.mHandler == null) {
                    mPhoneNum.setBackground(getDrawable(R.drawable.inputbox_red));
                    Toast.makeText(context, R.string.phone_num_format_error, Toast.LENGTH_SHORT).show();
                    return;
                }
                userRest.getCode(phoneNum.trim());
                mGetCodeBtn.setClickable(false);
                mGetCodeBtn.setBackgroundColor(getColor(R.color.colorGray));
                mTimerUtil = new TimerUtil(mGetCodeBtn, 30, 1);
                mTimerUtil.start();
                break;
            case R.id.forget_pass_submit_btn:
                if (isInputValid()) {
                    String password = mPassword1.getText().toString().trim();
                    String code = mCode.getText().toString().trim();
                    String enc_pass = MD5Util.md5(phoneNum.trim() + MD5Util.md5(password));
                    userRest.resetPassword(phoneNum.trim(), enc_pass, code);
                }
                break;
            default:
                break;
        }
    }

    /*响应返回按钮*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            this.onDestroy();
        }
        return true;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus)
            return;
        EditText editText = findViewById(v.getId());
        String value = editText.getText().toString() != null ?
                editText.getText().toString() : "";
        Context context = v.getContext();
        switch (v.getId()) {
            case R.id.forget_pass_phone_num:
                if (value.length() < 6 || value.length() > 15) {
                    editText.setBackground(getDrawable(R.drawable.inputbox_red));
                    Toast.makeText(context, R.string.phone_num_format_error, Toast.LENGTH_SHORT).show();
                } else
                    editText.setBackground(getDrawable(R.drawable.inputbox_white));
                break;
            case R.id.forget_pass_password1:
                if (value.trim().length() <= 6) {
                    editText.setBackground(getDrawable(R.drawable.inputbox_red));
                    Toast.makeText(context, R.string.password_format_error, Toast.LENGTH_SHORT).show();
                } else
                    editText.setBackground(getDrawable(R.drawable.inputbox_white));
                break;
            case R.id.forget_pass_password2:
                String pass1 = mPassword1.getText().toString() != null ?
                        mPassword1.getText().toString() : "";
                if (!value.trim().equals(pass1)) {
                    editText.setBackground(getDrawable(R.drawable.inputbox_red));
                    Toast.makeText(context, R.string.password_mismatch, Toast.LENGTH_SHORT).show();
                } else
                    editText.setBackground(getDrawable(R.drawable.inputbox_white));
                break;
            case R.id.forget_pass_code:
                if (value.length() != 6) {
                    Toast.makeText(context, R.string.code_length_error, Toast.LENGTH_SHORT).show();
                    editText.setBackground(getDrawable(R.drawable.inputbox_red));
                } else
                    editText.setBackground(getDrawable(R.drawable.inputbox_white));
                break;
        }
    }

    private boolean isInputValid() {
        if (mPhoneNum.getText().toString() == null ||
                mPhoneNum.getText().toString().length() <= 6 ||
                mPhoneNum.getText().toString().length() > 15) {
            mPhoneNum.setBackground(getDrawable(R.drawable.inputbox_red));
            Toast.makeText(mPhoneNum.getContext(), R.string.phone_num_format_error, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mPassword1.getText().toString() == null || mPassword1.getText().toString().length() <= 6) {
            mPassword1.setBackground(getDrawable(R.drawable.inputbox_red));
            Toast.makeText(mPassword1.getContext(), R.string.password_format_error, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!mPassword2.getText().toString().trim().equals(mPassword1.getText().toString().trim())) {
            mPassword2.setBackground(getDrawable(R.drawable.inputbox_red));
            Toast.makeText(mPassword2.getContext(), R.string.password_mismatch, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mCode.getText().toString().length() != 6) {
            Toast.makeText(mCode.getContext(), R.string.code_length_error, Toast.LENGTH_SHORT).show();
            mCode.setBackground(getDrawable(R.drawable.inputbox_red));
            return false;
        }
        try {
            Integer.parseInt(mCode.getText().toString());
        } catch (Exception e) {
            Toast.makeText(mCode.getContext(), R.string.code_format_error, Toast.LENGTH_SHORT).show();
            mCode.setBackground(getDrawable(R.drawable.inputbox_red));
            return false;
        }
        return true;
    }

    private static class ForgetPassHandler extends Handler {
        private final WeakReference<ForgetPassActivity> mForgetPassActivity;

        public ForgetPassHandler(ForgetPassActivity activity) {
            this.mForgetPassActivity = new WeakReference<ForgetPassActivity>(activity);
        }

        @Override
        public void handleMessage(Message message) {
            Context context = mForgetPassActivity.get().getApplicationContext();
            if (message.what == Constant.MESSAGE_GET_REGISTER_CODE_STATUS) {
                switch (message.getData().getInt("status")) {
                    case 200:
                        Toast.makeText(context, R.string.get_register_code_success, Toast.LENGTH_LONG).show();
                        break;
                    case 610:
                        Toast.makeText(context, R.string.get_verification_code_failed, Toast.LENGTH_LONG).show();
                        break;
                    case -1:
                        Toast.makeText(context, R.string.network_error, Toast.LENGTH_LONG).show();
                        mForgetPassActivity.get().mTimerUtil.cancel();
                        mForgetPassActivity.get().mTimerUtil.onFinish();
                        break;
                    default:
                        break;
                }
            } else if (message.what == Constant.MESSAGE_RESET_PASS_STATUS) {
                switch (message.getData().getInt("status")) {
                    case 200:
                        Toast.makeText(context, R.string.reset_pass_success, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, LoginActivity.class);
                        mForgetPassActivity.get().startActivity(intent);
                        break;
                    case 606:
                        Toast.makeText(context, R.string.invalid_phone_num, Toast.LENGTH_LONG).show();
                        break;
                    case 609:
                        Toast.makeText(context, R.string.incorrect_verification_code, Toast.LENGTH_LONG).show();
                        break;
                    case 613:
                        Toast.makeText(context, R.string.user_not_exsist, Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
