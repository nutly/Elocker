package com.feiyang.elocker.rest;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.feiyang.elocker.model.Authorization;
import com.feiyang.elocker.util.HttpsUtil;
import com.feiyang.elocker.util.MD5Util;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.Response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.feiyang.elocker.Constant.BASE_REQUEST_URL;
import static com.feiyang.elocker.Constant.MESSAGE_AUTHORIZATION_LIST;

public class AuthorizationRest extends Thread {
    private String appid;
    private String enc_pass;
    private Handler mHandler;
    private Authorization mAuthorization;
    private Task mTask;

    public AuthorizationRest() {
        this.appid = "15851841387";
        this.enc_pass = MD5Util.md5(this.appid + MD5Util.md5("12345"));
    }

    public void addAuthorization(Authorization authorization) {
        this.mAuthorization = authorization;
        this.mTask = Task.ADD_AUTHORIZATION;
        this.start();
    }

    public void getAllAuthorization(Handler handler) {
        mHandler = handler;
        mTask = Task.GET_ALL_AUTHORIZATION;
        this.start();
    }

    @Override
    public void run() {
        if (mTask != null) {
            switch (mTask) {
                case ADD_AUTHORIZATION:
                    this.addAuthorizationTask();
                    break;
                case GET_ALL_AUTHORIZATION:

                default:
                    break;
            }
        }
    }

    private void addAuthorizationTask() {
        String url = BASE_REQUEST_URL + "/authorization/add";
        String sign = MD5Util.md5("/authorization/add" + this.enc_pass);

        JsonObject params = new JsonObject();
        params.addProperty("appid", this.appid);
        params.addProperty("sign", sign);
        params.addProperty("serial", mAuthorization.getSerial());
        params.addProperty("toAccount", mAuthorization.getToAccount());
        params.addProperty("startTime", mAuthorization.getStartTime());
        params.addProperty("endTime", mAuthorization.getEndTime());
        params.addProperty("description", mAuthorization.getDescription());
        params.addProperty("weekday", mAuthorization.getWeekDay());
        params.addProperty("dailyStartTime", mAuthorization.getDailyStartTime());
        params.addProperty("dailyEndTime", mAuthorization.getDailyEndTime());
        Response response = HttpsUtil.post(url, params);
        if (response != null) {
            response.close();
        }
    }

    private void getAuthorizationListTask() {
        String url = BASE_REQUEST_URL + "/authorization/get";
        String sign = MD5Util.md5("/authorization/get" + this.enc_pass);

        Bundle data = new Bundle();
        Response response = HttpsUtil.get(url);
        List<Authorization> authorizations = new ArrayList<Authorization>();
        if (response != null) {
            if (response.isSuccessful()) {
                JsonParser jsonParser = new JsonParser();
                JsonObject responseData = new JsonObject();
                try {
                    responseData = jsonParser.parse(response.body().string()).getAsJsonObject();
                    JsonArray authorizationArray = responseData.getAsJsonArray("authorizationList");
                    for (int i = 0; i < authorizationArray.size(); i++) {
                        Authorization authorization = new Authorization();
                        JsonObject authObj = authorizationArray.get(i).getAsJsonObject();
                        authorization.setId(authObj.get("id").getAsLong());
                        authorization.setSerial(authObj.get("serial").getAsString());
                        authorization.setFromAccount(authObj.get("fromAccount").getAsString());
                        authorization.setToAccount(authObj.get("toAccount").getAsString());
                        authorization.setStartTime(authObj.get("startTime").getAsString());
                        authorization.setEndTime(authObj.get("endTime").getAsString());
                        authorization.setDescription(authObj.get("description").getAsString());
                        authorization.setWeekDay(authObj.get("weekday").getAsString());
                        authorization.setDailyStartTime(authObj.get("dailyStartTime").getAsString());
                        authorization.setDailyEndTime(authObj.get("dailyEndTime").getAsString());
                        authorizations.add(authorization);
                    }
                } catch (Exception e) {
                    Log.e("AuthorizationRest", "Failed to parse authorization list from response");
                    data.putInt("error", -1);
                }
            } else {
                data.putInt("error", -1);
            }
            response.close();
        } else {
            data.putInt("error", -1);
        }

        data.putSerializable("authorizationList", (Serializable) authorizations);
        Message message = new Message();
        message.what = MESSAGE_AUTHORIZATION_LIST;
        message.setData(data);
        message.setTarget(this.mHandler);
        message.sendToTarget();
    }

    private enum Task {
        GET_ALL_AUTHORIZATION, ADD_AUTHORIZATION
    }
}
