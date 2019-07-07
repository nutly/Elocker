package com.feiyang.elocker.rest;

import com.feiyang.elocker.model.OperationLog;
import com.feiyang.elocker.util.HttpsUtil;
import com.feiyang.elocker.util.MD5Util;
import com.google.gson.JsonObject;
import okhttp3.Response;

import static com.feiyang.elocker.Constant.BASE_REQUEST_URL;

public class OperationLogRest extends Thread {

    private String appid;
    private String enc_pass;
    private OperationLog mOperationLog;

    public OperationLogRest(OperationLog operationLog) {
        super();
        //TODO 更换成直接从配置文件获取当前登录的用户名和加密后的密码
        this.appid = "15851841387";
        this.enc_pass = MD5Util.md5(this.appid + MD5Util.md5("12345"));
        this.mOperationLog = operationLog;
    }

    public void addOperationLog() {
        this.start();
    }

    @Override
    public void run() {
        String sign = MD5Util.md5("/log/add" + this.enc_pass);
        String url = BASE_REQUEST_URL + "/log/add";
        JsonObject body = new JsonObject();
        body.addProperty("appid", mOperationLog.getPhoneNum());
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
