package com.feiyang.elocker.rest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.feiyang.elocker.Constant;
import com.feiyang.elocker.util.HttpsUtil;
import com.feiyang.elocker.util.MD5Util;
import com.google.gson.JsonObject;
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

    @Override
    public void run() {
        switch (mTask) {
            case CHANGE_PASSWORD:
                this.changePassTask();
                break;
            case RESET_PASSWORD:
                this.resetPasswordTask();
                break;
            default:
                break;
        }
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

    private void resetPasswordTask() {

    }

    private enum Task {
        CHANGE_PASSWORD, RESET_PASSWORD
    }
}
