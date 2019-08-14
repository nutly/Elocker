package com.feiyang.elocker.rest;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.feiyang.elocker.Constant;
import com.feiyang.elocker.util.HttpsUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.Response;

import java.util.HashMap;
import java.util.Locale;

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
        HashMap<String, String> headers = new HashMap<>();
        String userAgent = Build.VERSION.RELEASE + "#" + Build.VERSION.BASE_OS +
                "#" + Build.MODEL + "#" + Build.BRAND + "#" + Locale.getDefault().getLanguage();
        headers.put(Constant.USERAGENT, userAgent);
        headers.put(Constant.APPVERSION, Constant.CURRENT_RELEASE);
        JsonObject params = new JsonObject();
        params.addProperty("phoneNum", mUsername);
        params.addProperty("password", mPassword);

        Response response = HttpsUtil.post(url, params, headers);
        Bundle data = new Bundle();
        Message message = new Message();
        if (response != null) {
            data.putInt("status", response.code());
            if (response.isSuccessful()) {
                JsonParser jsonParser = new JsonParser();
                try {
                    JsonObject responseData = jsonParser.parse(response.body().string()).getAsJsonObject();
                    data.putString("apiKey", responseData.get("apiKey").getAsString());
                } catch (Exception e) {
                    Log.e("LoginRest", "Failed to parse apiKey from response");
                    data.putInt("status", -1);
                }
            }
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
