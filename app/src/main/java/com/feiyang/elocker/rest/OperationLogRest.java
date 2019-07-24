package com.feiyang.elocker.rest;

import android.content.Context;
import android.content.SharedPreferences;
import com.feiyang.elocker.Constant;
import com.feiyang.elocker.model.OperationLog;
import com.feiyang.elocker.util.HttpsUtil;
import com.feiyang.elocker.util.MD5Util;
import com.google.gson.JsonObject;
import okhttp3.Response;

import static com.feiyang.elocker.Constant.BASE_REQUEST_URL;

public class OperationLogRest extends Thread {

    private String mPhoneNum;
    private String mPassword;
    private OperationLog mOperationLog;

    public OperationLogRest(Context context, OperationLog operationLog) {
        super();
        SharedPreferences sp = context.getSharedPreferences(Constant.PROPERTY_FILE_NAME, Context.MODE_PRIVATE);
        this.mPhoneNum = sp.getString("phoneNum", "");
        this.mPassword = sp.getString("password", "");
        this.mOperationLog = operationLog;
    }

    public void addOperationLog() {
        this.start();
    }

    @Override
    public void run() {
        String sign = MD5Util.md5("/log/add" + this.mPassword);
        String url = BASE_REQUEST_URL + "/log/add";
        JsonObject body = new JsonObject();
        body.addProperty("appid", mPhoneNum);
        body.addProperty("sign", sign);
        body.addProperty("serial", mOperationLog.getSerial());
        body.addProperty("operation", mOperationLog.getOperation().toString());
        body.addProperty("sTime", mOperationLog.getsTime());
        body.addProperty("description", mOperationLog.getDescription());
        Response response = HttpsUtil.post(url, body);
        if (response != null)
            response.close();
    }
}
