package com.feiyang.elocker.rest;

import android.content.Context;
import android.content.SharedPreferences;
import com.feiyang.elocker.Constant;
import com.feiyang.elocker.model.OperationLog;
import com.feiyang.elocker.util.HttpsUtil;
import com.feiyang.elocker.util.MD5Util;
import com.google.gson.JsonObject;
import okhttp3.Response;

import java.util.HashMap;

import static com.feiyang.elocker.Constant.BASE_REQUEST_URL;

public class OperationLogRest extends Thread {

    private String mPhoneNum;
    private String mPassword;
    private OperationLog mOperationLog;
    private String mApiKey;

    public OperationLogRest(Context context, OperationLog operationLog) {
        super();
        SharedPreferences sp = context.getSharedPreferences(Constant.PROPERTY_FILE_NAME, Context.MODE_PRIVATE);
        this.mPhoneNum = sp.getString("phoneNum", "");
        this.mPassword = sp.getString("password", "");
        this.mApiKey = sp.getString("apiKey", "");
        this.mOperationLog = operationLog;
    }

    public void addOperationLog() {
        this.start();
    }

    @Override
    public void run() {
        String token = MD5Util.md5("/log/add" +
                MD5Util.md5(this.mPassword + this.mApiKey));
        String url = BASE_REQUEST_URL + "/log/add";

        HashMap<String, String> headers = new HashMap<>();
        headers.put(Constant.APPID, this.mPhoneNum);
        headers.put(Constant.APIKEY, this.mApiKey);
        headers.put(Constant.TOKEN, token);
        JsonObject body = new JsonObject();
        body.addProperty("serial", mOperationLog.getSerial());
        body.addProperty("operation", mOperationLog.getOperation().toString());
        body.addProperty("sTime", mOperationLog.getsTime());
        body.addProperty("description", mOperationLog.getDescription());
        Response response = HttpsUtil.post(url, body, headers);
        if (response != null)
            response.close();
    }
}
