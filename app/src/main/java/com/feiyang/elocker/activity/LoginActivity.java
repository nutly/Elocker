package com.feiyang.elocker.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.feiyang.elocker.R;
import com.feiyang.elocker.rest.LoginRest;
import com.feiyang.elocker.util.LoginUtil;
import com.feiyang.elocker.util.MD5Util;

import java.lang.ref.WeakReference;

import static com.feiyang.elocker.Constant.MESSAGE_LOGIN_STATUS;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mUserName, mPassword;
    private String mPhoneNum, mEncryptPassword;
    private LoginHandler mLoginHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUserName = (EditText) findViewById(R.id.activity_login_user_name);
        mPassword = (EditText) findViewById(R.id.activity_login_passwd);
        mLoginHandler = new LoginHandler(this);
        Button loginBtn = (Button) findViewById(R.id.activity_login_login_btn);
        loginBtn.setOnClickListener(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.login);
    }

    @Override
    public void onClick(View v) {
        Context context = v.getContext();
        mPhoneNum = mUserName.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        if (mPhoneNum == null || mPhoneNum.equals("")) {
            Toast.makeText(context, R.string.empty_username, Toast.LENGTH_SHORT).show();
            return;
        }
        if (password == null || password.equals("")) {
            Toast.makeText(context, R.string.empty_passwd, Toast.LENGTH_SHORT).show();
            return;
        }
        mEncryptPassword = MD5Util.md5(mPhoneNum + MD5Util.md5(password));

        LoginRest loginRest = new LoginRest(mLoginHandler);
        loginRest.login(mPhoneNum, mEncryptPassword);
        Toast.makeText(this, R.string.login_now, Toast.LENGTH_SHORT).show();
    }

    private static class LoginHandler extends Handler {
        private final WeakReference<LoginActivity> mLoginActivity;

        public LoginHandler(LoginActivity loginActivity) {
            this.mLoginActivity = new WeakReference<LoginActivity>(loginActivity);
        }

        @Override
        public void handleMessage(Message message) {
            LoginActivity loginActivity = mLoginActivity.get();
            if (message.what == MESSAGE_LOGIN_STATUS) {
                Context context = loginActivity.getApplicationContext();
                Bundle data = message.getData();
                int status = data.getInt("status");
                switch (status) {
                    /*登录成功*/
                    case 200:
                        Intent intent = new Intent(context, UnlockActivity.class);
                        LoginUtil.saveLoginInfo(context, loginActivity.mPhoneNum, loginActivity.mEncryptPassword);
                        loginActivity.startActivity(intent);
                        loginActivity.finish();
                        break;
                    /*用户名或者密码错误*/
                    case 601:
                        Toast.makeText(context, R.string.incorrect_login_info, Toast.LENGTH_LONG).show();
                        break;
                    /*错误次数过多*/
                    case 602:
                        Toast.makeText(context, R.string.login_too_frequently, Toast.LENGTH_LONG).show();
                        break;
                    /*网络连接错误*/
                    case -1:
                        Toast.makeText(context, R.string.network_error, Toast.LENGTH_LONG).show();
                        break;
                    /*程序运行错误*/
                    case 500:
                        Toast.makeText(context, R.string.internal_error, Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }
            }
        }
    }
}

