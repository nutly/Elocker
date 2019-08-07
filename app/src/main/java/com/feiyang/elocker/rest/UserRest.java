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
    /*验证码,用于新增用户时使用*/
    private String mCode;

    public UserRest(Context context, Handler handler) {
        mHandler = handler;
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.PROPERTY_FILE_NAME, Context.MODE_PRIVATE);
        mPhoneNum = sharedPreferences.getString("phoneNum", "");
        mEncryptPassword = sharedPreferences.getString("password", "");
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

    public void getRegisterCode(String phoneNum) {
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
                this.getRegisterCodeTask();
                break;
            case ADD_USER:
                this.addUserTask();
                break;
            default:
                break;
        }
    }

    private void getUserTask() {
        String sign = MD5Util.md5("/user/get" + mEncryptPassword);
        String url = BASE_REQUEST_URL + "/user/get?appid=" + mPhoneNum + "&sign=" + sign;
        Bundle data = new Bundle();
        Response response = HttpsUtil.get(url);
        if (response != null) {
            if (response.isSuccessful()) {
                JsonParser jsonParser = new JsonParser();
                JsonObject responseData = null;
                try {
                    User user = new User();
                    responseData = jsonParser.parse(response.body().string()).getAsJsonObject().get("user").getAsJsonObject();
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
        String sign = MD5Util.md5("/user/changePassword" + mEncryptPassword);

        JsonObject params = new JsonObject();
        params.addProperty("appid", mPhoneNum);
        params.addProperty("sign", sign);
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

    private void getRegisterCodeTask() {
        Message message = new Message();
        Bundle data = new Bundle();

        String url = BASE_REQUEST_URL + "/user/fetchCodeForRegister";
        JsonObject params = new JsonObject();
        params.addProperty("appid", mUser.getPhoneNum());
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

    private enum Task {
        CHANGE_PASSWORD, GET_USER_BY_PHONE, GET_REGISTER_CODE, ADD_USER
    }
}
