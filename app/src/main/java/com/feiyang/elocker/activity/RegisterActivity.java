package com.feiyang.elocker.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.feiyang.elocker.Constant;
import com.feiyang.elocker.R;
import com.feiyang.elocker.rest.UserRest;
import com.feiyang.elocker.util.TimerUtil;

import java.lang.ref.WeakReference;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mPhoneNum, mPassword1, mPassword2, mCode, mNickName, mEmail;
    private Button mGetCodeBtn, mSubmitBtn;
    private RegisterHandler mHandler;
    private TimerUtil timerUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
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
    }

    @Override
    public void onClick(View v) {
        Context context = v.getContext();
        switch (v.getId()) {
            case R.id.activity_register_get_code:
                String phoneNum = mPhoneNum.getText().toString();
                UserRest userRest = new UserRest(context, mHandler);
                userRest.getRegisterCode(phoneNum);

                mGetCodeBtn.setClickable(false);
                mGetCodeBtn.setBackgroundColor(getResources().getColor(R.color.colorGray, null));
                TimerUtil timerUtil = new TimerUtil(mGetCodeBtn, 120, 1);
                timerUtil.start();
                break;
            case R.id.activity_register_submit:
                String code = mCode.getText().toString();
            default:
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
            if (message.what == Constant.MESSAGE_GET_REGISTER_CODE_STATUS) {
                Context context = mRegisterActivity.get().getApplicationContext();
                switch (message.getData().getInt("status")) {
                    case 200:
                        Toast.makeText(context, R.string.get_register_code_success, Toast.LENGTH_LONG).show();
                        break;
                    case 610:
                    case -1:
                        Toast.makeText(context, R.string.invalid_phone_num, Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
