package com.feiyang.elocker.rest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.feiyang.elocker.Constant;
import com.feiyang.elocker.model.Authorization;
import com.feiyang.elocker.util.HttpsUtil;
import com.feiyang.elocker.util.MD5Util;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.Response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static com.feiyang.elocker.Constant.*;

public class AuthorizationRest extends Thread {
    private String mPhoneNum;
    private String mPassword;
    private String mApiKey;
    private Handler mHandler;
    private Authorization mAuthorization;
    private Task mTask;

    public AuthorizationRest(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Constant.PROPERTY_FILE_NAME, Context.MODE_PRIVATE);
        this.mPhoneNum = sp.getString("phoneNum", "");
        this.mPassword = sp.getString("password", "");
        this.mApiKey = sp.getString("apiKey", "");
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

    public void delAuthorizationById(Long id, String serial) {
        mAuthorization = new Authorization();
        mAuthorization.setId(id);
        mAuthorization.setSerial(serial);
        mTask = Task.DEL_AUTHORIZATION;
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
                    this.getAuthorizationListTask();
                    break;
                case DEL_AUTHORIZATION:
                    this.delAuthorizationByIdTask();
                    break;
                default:
                    break;
            }
        }
    }

    private void addAuthorizationTask() {
        String url = BASE_REQUEST_URL + "/authorization/add";
        String token = MD5Util.md5("/authorization/add" +
                MD5Util.md5(this.mPassword + this.mApiKey));

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put(APPID, this.mPhoneNum);
        headers.put(Constant.TOKEN, token);
        headers.put(Constant.APIKEY, this.mApiKey);

        JsonObject params = new JsonObject();
        params.addProperty("serial", mAuthorization.getSerial());
        params.addProperty("toAccount", mAuthorization.getToAccount());
        params.addProperty("startTime", mAuthorization.getStartTime());
        params.addProperty("endTime", mAuthorization.getEndTime());
        params.addProperty("description", mAuthorization.getDescription());
        params.addProperty("weekday", mAuthorization.getWeekDay());
        params.addProperty("dailyStartTime", mAuthorization.getDailyStartTime());
        params.addProperty("dailyEndTime", mAuthorization.getDailyEndTime());
        Response response = HttpsUtil.post(url, params, headers);
        if (response != null) {
            response.close();
        }
    }

    private void getAuthorizationListTask() {
        String token = MD5Util.md5("/authorization/get" +
                MD5Util.md5(this.mPassword + this.mApiKey));
        String url = BASE_REQUEST_URL + "/authorization/get";

        Bundle data = new Bundle();
        LinkedHashMap<String, List<Authorization>> authorizationsMap = new LinkedHashMap<String, List<Authorization>>();
        HashMap<String, String> headers = new HashMap<>();
        headers.put(APPID, this.mPhoneNum);
        headers.put(TOKEN, token);
        headers.put(APIKEY, this.mApiKey);
        Response response = HttpsUtil.get(url, headers);
        if (response != null) {
            if (response.isSuccessful()) {
                JsonParser jsonParser = new JsonParser();
                JsonObject responseData;
                try {
                    responseData = jsonParser.parse(response.body().string()).getAsJsonObject();
                    JsonArray authorizationArray = responseData.getAsJsonArray("authorizationList");
                    authorizationsMap = this.groupResultByLockerName(authorizationArray);
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

        data.putSerializable("authorizationList", (Serializable) authorizationsMap);
        Message message = new Message();
        message.what = MESSAGE_AUTHORIZATION_LIST;
        message.setData(data);
        message.setTarget(this.mHandler);
        message.sendToTarget();
    }

    private void delAuthorizationByIdTask() {
        String token = MD5Util.md5("/authorization/delete" +
                MD5Util.md5(this.mPassword + this.mApiKey));

        String url = BASE_REQUEST_URL + "/authorization/delete";
        JsonObject id = new JsonObject();
        id.addProperty("id", mAuthorization.getId());
        id.addProperty("serial", mAuthorization.getSerial());
        JsonArray ids = new JsonArray();
        ids.add(id);
        HashMap<String, String> headers = new HashMap<>();
        headers.put(APPID, this.mPhoneNum);
        headers.put(TOKEN, token);
        headers.put(APIKEY, mApiKey);
        JsonObject params = new JsonObject();
        params.add("ids", ids);

        Response response = HttpsUtil.post(url, params, headers);
        if (response != null) {
            response.close();
        }
    }

    private LinkedHashMap<String, List<Authorization>> groupResultByLockerName(JsonArray authorizationArray) {
        LinkedHashMap<String, List<Authorization>> authorizations = new LinkedHashMap<String, List<Authorization>>();

        for (int i = 0; i < authorizationArray.size(); i++) {
            Authorization authorization = new Authorization();
            JsonObject authObj = authorizationArray.get(i).getAsJsonObject();
            authorization.setId(authObj.get("id").getAsLong());
            authorization.setSerial(authObj.get("serial").getAsString());
            String lockerName = authObj.get("lockerDescription").getAsString();
            authorization.setLockerName(lockerName);
            authorization.setFromAccount(authObj.get("fromAccount").getAsString());
            authorization.setToAccount(authObj.get("toAccount").getAsString());
            authorization.setStartTime(authObj.get("startTime").getAsString());
            authorization.setEndTime(authObj.get("endTime").getAsString());
            authorization.setDescription(authObj.get("description").getAsString());
            authorization.setWeekDay(authObj.get("weekday").getAsString());
            authorization.setDailyStartTime(authObj.get("dailyStartTime").getAsString());
            authorization.setDailyEndTime(authObj.get("dailyEndTime").getAsString());

            /*已经有一个lockerName组*/
            if (authorizations.containsKey(lockerName)) {
                authorizations.get(lockerName).add(authorization);
            } else {
                /*该lockerName不存在，创建一个新组*/
                List<Authorization> authorizationList = new ArrayList<Authorization>();
                authorizationList.add(authorization);
                authorizations.put(lockerName, authorizationList);
            }
        }
        return authorizations;
    }

    private enum Task {
        GET_ALL_AUTHORIZATION, ADD_AUTHORIZATION, DEL_AUTHORIZATION
    }
}
