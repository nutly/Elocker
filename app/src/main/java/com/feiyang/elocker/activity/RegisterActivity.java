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
import com.feiyang.elocker.model.User;
import com.feiyang.elocker.rest.UserRest;
import com.feiyang.elocker.util.MD5Util;
import com.feiyang.elocker.util.TimerUtil;

import java.lang.ref.WeakReference;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener,
        View.OnFocusChangeListener {

    private EditText mPhoneNum, mPassword1, mPassword2, mCode, mNickName, mEmail;
    private Button mGetCodeBtn, mSubmitBtn;
    private RegisterHandler mHandler;
    private TimerUtil mTimerUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        /*设置返回按钮*/
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.register);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mHandler = new RegisterHandler(this);
        mPhoneNum = findViewById(R.id.activity_register_phone_num);
        mPassword1 = findViewById(R.id.activity_register_pass1);
        mPassword2 = findViewById(R.id.activity_register_pass2);
        mCode = findViewById(R.id.activity_register_code);
        mNickName = findViewById(R.id.activity_register_username);
        mEmail = findViewById(R.id.activity_register_email);
        mGetCodeBtn = findViewById(R.id.activity_register_get_code);
        mSubmitBtn = findViewById(R.id.activity_register_submit);

        mGetCodeBtn.setOnClickListener(this);
        mSubmitBtn.setOnClickListener(this);
        mPhoneNum.setOnFocusChangeListener(this);
        mPassword1.setOnFocusChangeListener(this);
        mPassword2.setOnFocusChangeListener(this);
        mCode.setOnFocusChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        Context context = v.getContext();
        String phoneNum = mPhoneNum.getText().toString();
        UserRest userRest = new UserRest(context, mHandler);
        switch (v.getId()) {
            case R.id.activity_register_get_code:
                if (phoneNum == null || phoneNum.equals("")
                        || phoneNum.length() < Constant.MIN_PHONE_NUM_LENGTH || this.mHandler == null) {
                    mPhoneNum.setBackground(getDrawable(R.drawable.inputbox_red));
                    Toast.makeText(context, R.string.phone_num_format_error, Toast.LENGTH_SHORT).show();
                    return;
                }
                userRest.getCode(phoneNum);
                mGetCodeBtn.setClickable(false);
                mGetCodeBtn.setBackgroundColor(getColor(R.color.colorGray));
                mTimerUtil = new TimerUtil(mGetCodeBtn, 120, 1);
                mTimerUtil.start();
                break;
            case R.id.activity_register_submit:
                if (isInputValid()) {
                    User user = new User();
                    user.setPhoneNum(phoneNum);
                    String orgin_pass = mPassword1.getText().toString().trim();
                    user.setPassword(MD5Util.md5(phoneNum + MD5Util.md5(orgin_pass)));
                    user.setEmail(mEmail.getText().toString().trim());
                    user.setUserName(mNickName.getText().toString().trim());
                    userRest.addUser(user, mCode.getText().toString());
                }
                break;
            default:
                break;
        }
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
            case R.id.activity_register_phone_num:
                if (value.length() < Constant.MIN_PHONE_NUM_LENGTH
                        || value.length() > Constant.MAX_PHONE_NUM_LENGTH) {
                    editText.setBackground(getDrawable(R.drawable.inputbox_red));
                    Toast.makeText(context, R.string.phone_num_format_error, Toast.LENGTH_SHORT).show();
                } else
                    editText.setBackground(getDrawable(R.drawable.inputbox_white));
                break;
            case R.id.activity_register_pass1:
                if (value.trim().length() <= Constant.MIN_PASSWORD_LENGTH) {
                    editText.setBackground(getDrawable(R.drawable.inputbox_red));
                    Toast.makeText(context, R.string.password_format_error, Toast.LENGTH_SHORT).show();
                } else
                    editText.setBackground(getDrawable(R.drawable.inputbox_white));
                break;
            case R.id.activity_register_pass2:
                String pass1 = mPassword1.getText().toString() != null ?
                        mPassword1.getText().toString() : "";
                if (!value.trim().equals(pass1)) {
                    editText.setBackground(getDrawable(R.drawable.inputbox_red));
                    Toast.makeText(context, R.string.password_mismatch, Toast.LENGTH_SHORT).show();
                } else
                    editText.setBackground(getDrawable(R.drawable.inputbox_white));
                break;
            case R.id.activity_register_code:
                if (value.length() != Constant.VERIFICATION_CODE_LENGTH) {
                    Toast.makeText(context, R.string.code_length_error, Toast.LENGTH_SHORT).show();
                    editText.setBackground(getDrawable(R.drawable.inputbox_red));
                } else
                    editText.setBackground(getDrawable(R.drawable.inputbox_white));
                break;
        }
    }

    private static class RegisterHandler extends Handler {
        private final WeakReference<RegisterActivity> mRegisterActivity;

        public RegisterHandler(RegisterActivity registerActivity) {
            this.mRegisterActivity = new WeakReference<RegisterActivity>(registerActivity);
        }

        @Override
        public void handleMessage(Message message) {
            Context context = mRegisterActivity.get().getApplicationContext();
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
                        mRegisterActivity.get().mTimerUtil.cancel();
                        mRegisterActivity.get().mTimerUtil.onFinish();
                        break;
                    default:
                        break;
                }
            } else if (message.what == Constant.MESSAGE_USER_REGISTER_STATUS) {
                switch (message.getData().getInt("status")) {
                    case 200:
                        Toast.makeText(context, R.string.register_success, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, LoginActivity.class);
                        context.startActivity(intent);
                        break;
                    case 609:
                        Toast.makeText(context, R.string.incorrect_verification_code, Toast.LENGTH_LONG).show();
                        break;
                    case 612:
                        Toast.makeText(context, R.string.user_duplicate, Toast.LENGTH_LONG).show();
                        break;
                    case 500:
                        Toast.makeText(context, R.string.internal_error, Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private boolean isInputValid() {
        if (mPhoneNum.getText().toString() == null ||
                mPhoneNum.getText().toString().length() <= Constant.MIN_PHONE_NUM_LENGTH ||
                mPhoneNum.getText().toString().length() > Constant.MAX_PHONE_NUM_LENGTH) {
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
        if (mCode.getText().toString().length() != Constant.VERIFICATION_CODE_LENGTH) {
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
}
