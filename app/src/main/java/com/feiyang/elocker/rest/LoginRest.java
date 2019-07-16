package com.feiyang.elocker.rest;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.feiyang.elocker.util.HttpsUtil;
import com.google.gson.JsonObject;
import okhttp3.Response;

import static com.feiyang.elocker.Constant.BASE_REQUEST_URL;
import static com.feiyang.elocker.Constant.MESSAGE_LOGIN_STATUS;

public class LoginRest extends Thread {

    private Handler mHandler;
    private String mUsername;
    private String mPassword;

    public LoginRest(Handler handler) {
        mHandler = handler;
    }

    /*
     * @param username 用户名
     * @param enc_pass 加密后的密码
     * @return void
     */
    public void login(String username, String enc_pass) {
        mUsername = username;
        mPassword = enc_pass;
        this.start();
    }

    @Override
    public void run() {
        String url = BASE_REQUEST_URL + "/login/login";
        JsonObject params = new JsonObject();
        params.addProperty("phoneNum", mUsername);
        params.addProperty("password", mPassword);

        Response response = HttpsUtil.post(url, params);
        Bundle data = new Bundle();
        Message message = new Message();
        if (response != null) {
            data.putInt("status", response.code());
            response.close();
        } else {
            data.putInt("status", -1);
        }
        message.setData(data);
        message.what = MESSAGE_LOGIN_STATUS;
        message.setTarget(mHandler);
        message.sendToTarget();
    }
}
