package com.feiyang.elocker.rest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.feiyang.elocker.Constant;
import com.feiyang.elocker.model.User;
import com.feiyang.elocker.util.HttpsUtil;
import com.feiyang.elocker.util.MD5Util;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.Response;

import java.util.HashMap;

import static com.feiyang.elocker.Constant.BASE_REQUEST_URL;
import static com.feiyang.elocker.Constant.MESSAGE_CHANGE_PASS_STATUS;

public class UserRest extends Thread {

    private String mPhoneNum;
    private String mEncryptPassword;
    private String mOldPassword;
    private String mNewPassword;
    private Handler mHandler;
    private Task mTask;
    private User mUser;
    /*验证码*/
    private String mCode;
    private String mApiKey;

    public UserRest(Context context, Handler handler) {
        mHandler = handler;
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.PROPERTY_FILE_NAME, Context.MODE_PRIVATE);
        mPhoneNum = sharedPreferences.getString("phoneNum", "");
        mEncryptPassword = sharedPreferences.getString("password", "");
        mApiKey = sharedPreferences.getString("apiKey", "");
    }

    public void changePassword(String oldPassword, String newPassword) {
        mOldPassword = MD5Util.md5(mPhoneNum + MD5Util.md5(oldPassword));
        mNewPassword = MD5Util.md5(mPhoneNum + MD5Util.md5(newPassword));
        mTask = Task.CHANGE_PASSWORD;
        this.start();
    }

    public void getUser() {
        mTask = Task.GET_USER_BY_PHONE;
        this.start();
    }

    public void getCode(String phoneNum) {
        mUser = new User();
        mUser.setPhoneNum(phoneNum);
        mTask = Task.GET_REGISTER_CODE;
        this.start();
    }

    public void addUser(User user, String code) {
        mUser = user;
        mCode = code;
        mTask = Task.ADD_USER;
        this.start();
    }

    public void resetPassword(String phoneNum, String password, String code) {
        this.mPhoneNum = phoneNum;
        this.mEncryptPassword = password;
        this.mCode = code;
        this.mTask = Task.RESET_PASSWORD;
        this.start();
    }

    @Override
    public void run() {
        switch (mTask) {
            case CHANGE_PASSWORD:
                this.changePassTask();
                break;
            case GET_USER_BY_PHONE:
                this.getUserTask();
                break;
            case GET_REGISTER_CODE:
                this.getCodeTask();
                break;
            case ADD_USER:
                this.addUserTask();
                break;
            case RESET_PASSWORD:
                this.resetPasswordTask();
                break;
            default:
                break;
        }
    }

    private void getUserTask() {
        String token = MD5Util.md5("/user/get" +
                MD5Util.md5(mEncryptPassword + this.mApiKey));
        String url = BASE_REQUEST_URL + "/user/get";
        Bundle data = new Bundle();

        HashMap<String, String> headers = new HashMap<>();
        headers.put(Constant.APPID, this.mPhoneNum);
        headers.put(Constant.APIKEY, this.mApiKey);
        headers.put(Constant.TOKEN, token);
        Response response = HttpsUtil.get(url, headers);
        if (response != null) {
            if (response.isSuccessful()) {
                JsonParser jsonParser = new JsonParser();
                try {
                    User user = new User();
                    JsonObject responseData = jsonParser.parse(response.body().string()).getAsJsonObject().get("user").getAsJsonObject();
                    user.setPhoneNum(this.mPhoneNum);
                    user.setUserName(responseData.get("userName").getAsString());
                    user.setCreateTime(responseData.get("createTime").getAsString());
                    user.setEmail(responseData.get("email").getAsString());
                    user.setLastLoginIp(responseData.get("lastLoginIp").getAsString());
                    user.setLastLoginTime(responseData.get("lastLoginTime").getAsString());
                    data.putSerializable("user", user);
                    data.putInt("status", 200);
                } catch (Exception e) {
                    Log.e("UserRest", "Failed to parse response");
                    data.putInt("status", -1);
                }
            } else {
                data.putInt("status", response.code());
            }
            response.close();
        } else {
            data.putInt("status", -1);
        }
        Message message = new Message();
        message.what = Constant.MESSAGE_ACCOUNT;
        message.setData(data);
        message.setTarget(mHandler);
        message.sendToTarget();
    }

    private void changePassTask() {
        String url = BASE_REQUEST_URL + "/user/changePassword";
        String token = MD5Util.md5("/user/changePassword" +
                MD5Util.md5(mEncryptPassword + this.mApiKey));

        HashMap<String, String> headers = new HashMap<>();
        headers.put(Constant.APPID, this.mPhoneNum);
        headers.put(Constant.APIKEY, this.mApiKey);
        headers.put(Constant.TOKEN, token);

        JsonObject params = new JsonObject();
        params.addProperty("oldpass", mOldPassword);
        params.addProperty("newpass", mNewPassword);
        Bundle data = new Bundle();
        Message message = new Message();

        Response response = HttpsUtil.post(url, params);
        if (response != null) {
            data.putInt("status", response.code());
            data.putString("password", mNewPassword);
            response.close();
        } else {
            data.putInt("status", -1);
        }
        message.what = MESSAGE_CHANGE_PASS_STATUS;
        message.setData(data);
        message.setTarget(mHandler);
        message.sendToTarget();
    }

    private void getCodeTask() {
        Message message = new Message();
        Bundle data = new Bundle();

        String url = BASE_REQUEST_URL + "/user/getCode";
        JsonObject params = new JsonObject();
        params.addProperty("phoneNum", mUser.getPhoneNum());
        Response response = HttpsUtil.post(url, params);
        if (response != null) {
            data.putInt("status", response.code());
            response.close();
        } else {
            data.putInt("status", -1);
        }
        message.what = Constant.MESSAGE_GET_REGISTER_CODE_STATUS;
        message.setTarget(mHandler);
        message.setData(data);
        message.sendToTarget();
    }

    private void addUserTask() {
        String url = BASE_REQUEST_URL + "/user/add";

        JsonObject params = new JsonObject();
        params.addProperty("phoneNum", mUser.getPhoneNum());
        params.addProperty("userName", mUser.getUserName());
        params.addProperty("password", mUser.getPassword());
        params.addProperty("email", mUser.getEmail());
        params.addProperty("code", mCode);

        Message message = new Message();
        Bundle data = new Bundle();
        Response response = HttpsUtil.post(url, params);
        if (response != null) {
            data.putInt("status", response.code());
            response.close();
        } else {
            data.putInt("status", -1);
        }
        message.what = Constant.MESSAGE_USER_REGISTER_STATUS;
        message.setData(data);
        message.setTarget(mHandler);
        message.sendToTarget();
    }

    private void resetPasswordTask() {
        String url = BASE_REQUEST_URL + "/user/resetPassword";
        JsonObject params = new JsonObject();
        params.addProperty("phoneNum", mPhoneNum);
        params.addProperty("password", mEncryptPassword);
        params.addProperty("code", mCode);

        Response response = HttpsUtil.post(url, params);
        Bundle data = new Bundle();
        Message message = new Message();
        if (response != null) {
            data.putInt("status", response.code());
            response.close();
        } else {
            data.putInt("status", -1);
        }
        message.what = Constant.MESSAGE_RESET_PASS_STATUS;
        message.setData(data);
        message.setTarget(mHandler);
        message.sendToTarget();
    }

    private enum Task {
        CHANGE_PASSWORD, RESET_PASSWORD, GET_USER_BY_PHONE, GET_REGISTER_CODE, ADD_USER
    }
}
