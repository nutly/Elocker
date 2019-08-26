package com.feiyang.elocker.rest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.feiyang.elocker.Constant;
import com.feiyang.elocker.model.OperationLog;
import com.feiyang.elocker.util.HttpsUtil;
import com.feiyang.elocker.util.MD5Util;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.Response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.feiyang.elocker.Constant.BASE_REQUEST_URL;

public class LogRest extends Thread {

    private String mPhoneNum;
    private String mPassword;
    private OperationLog mLog;
    private String mApiKey;
    private short taskId;
    private final static short ADD_LOG = 1;
    private final static short GET_LOG = 2;

    private String mSerial;
    private String mStartTime;
    private String mEndTime;
    private int mPage;
    private Handler mHandler;

    public LogRest(Context context) {
        super();
        SharedPreferences sp = context.getSharedPreferences(Constant.PROPERTY_FILE_NAME, Context.MODE_PRIVATE);
        this.mPhoneNum = sp.getString("phoneNum", "");
        this.mPassword = sp.getString("password", "");
        this.mApiKey = sp.getString("apiKey", "");
    }

    public void addLog(OperationLog log) {
        mLog = log;
        taskId = ADD_LOG;
        this.start();
    }

    public void getLog(String startTime, String endTime, int page, Handler handler) {
        this.getLog(null, startTime, endTime, page, handler);
    }

    /*
     * @param serial
     * @param startTime
     * @param endTime
     * @param page
     * @param handler
     * @return void
     */
    public void getLog(String serial, String startTime, String endTime, int page, Handler handler) {
        this.mSerial = serial;
        this.mStartTime = startTime;
        this.mEndTime = endTime;
        this.mPage = page;
        this.mHandler = handler;
        this.taskId = GET_LOG;
        this.start();
    }

    @Override
    public void run() {
        switch (taskId) {
            case ADD_LOG:
                this.addLogTask();
                break;
            case GET_LOG:
                this.getLogTask();
                break;
            default:
                break;
        }
    }

    private void addLogTask() {
        String token = MD5Util.md5("/log/add" +
                MD5Util.md5(this.mPassword + this.mApiKey));
        String url = BASE_REQUEST_URL + "/log/add";

        HashMap<String, String> headers = new HashMap<>();
        headers.put(Constant.APPID, this.mPhoneNum);
        headers.put(Constant.APIKEY, this.mApiKey);
        headers.put(Constant.TOKEN, token);
        JsonObject body = new JsonObject();
        body.addProperty("serial", mLog.getSerial());
        body.addProperty("operation", mLog.getOperation().toString());
        body.addProperty("sTime", mLog.getsTime());
        body.addProperty("description", mLog.getDescription());
        Response response = HttpsUtil.post(url, body, headers);
        if (response != null)
            response.close();
    }

    private void getLogTask() {
        String token = MD5Util.md5("/log/get" +
                MD5Util.md5(this.mPassword + this.mApiKey));
        String url = BASE_REQUEST_URL + "/log/get?page=" + mPage + "&pageSize=" + Constant.LOG_PAGE_SIZE;
        if (this.mSerial != null) {
            url = url + "&serial=" + this.mSerial;
        }
        if (this.mStartTime != null) {
            url = url + "&startTime=" + this.mStartTime;
        }
        if (this.mEndTime != null) {
            url = url + "&endTime=" + this.mEndTime;
        }

        HashMap<String, String> headers = new HashMap<>();
        headers.put(Constant.APPID, this.mPhoneNum);
        headers.put(Constant.APIKEY, this.mApiKey);
        headers.put(Constant.TOKEN, token);
        Response response = HttpsUtil.get(url, headers);

        Bundle data = new Bundle();
        if (response != null) {
            if (response.isSuccessful()) {
                JsonParser jsonParser = new JsonParser();
                JsonObject responseData = null;
                try {
                    responseData = jsonParser.parse(response.body().string()).getAsJsonObject();
                    JsonArray logArray = responseData.getAsJsonArray("logs");
                    List<OperationLog> logs = new ArrayList<>();
                    for (int i = 0; i < logArray.size(); i++) {
                        OperationLog log = new OperationLog();
                        JsonObject logObject = logArray.get(i).getAsJsonObject();
                        log.setSerial(logObject.get("serial").getAsString());
                        OperationLog.Operation operation = OperationLog.Operation
                                .from(logObject.get("operation").getAsString());
                        log.setOperation(operation);
                        log.setsTime(logObject.get("sTime").getAsString());
                        log.setDescription(logObject.get("description").getAsString());
                        log.setPhoneNum(logObject.get("phoneNum").getAsString());
                        logs.add(log);
                    }
                    data.putSerializable("logs", (Serializable) logs);
                    data.putInt("status", 200);
                } catch (Exception e) {
                    Log.e("LogRest", "Failed to parse response log data");
                    data.putInt("status", -1);
                }
            } else {
                data.putInt("status", response.code());
            }
            response.close();
        } else {
            data.putInt("status", 404);
        }

        Message message = new Message();
        message.what = Constant.MESSAGE_GET_LOG;
        message.setTarget(mHandler);
        message.setData(data);
        message.sendToTarget();
    }
}
