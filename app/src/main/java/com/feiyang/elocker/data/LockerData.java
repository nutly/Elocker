package com.feiyang.elocker.data;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.feiyang.elocker.model.Locker;
import com.feiyang.elocker.util.HttpsUtil;
import com.feiyang.elocker.util.MD5Util;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.Response;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.feiyang.elocker.Constant.BASE_REQUEST_URL;
import static com.feiyang.elocker.Constant.MESSAGE_lOCKER_LIST;

public class LockerData extends Thread {

    private String enc_pass;
    private String appid;
    private String sign;
    private String url;
    private Handler mHandler;

    public LockerData(Handler handler) {
        super();
        this.mHandler = handler;
        //TODO 更换成直接从配置文件获取当前登录的用户名和加密后的密码
        this.appid = "15851841387";
        this.enc_pass = MD5Util.md5(this.appid + MD5Util.md5("12345"));
    }

    @Override
    public void run() {
        List<Locker> lockers = new ArrayList<Locker>();
        Response response = HttpsUtil.get(this.url);
        if (response != null && response.isSuccessful()) {
            JsonParser jsonParser = new JsonParser();
            JsonObject responseData = null;
            try {
                responseData = jsonParser.parse(response.body().string()).getAsJsonObject();
                JsonArray lockerArray = responseData.getAsJsonArray("lockerList");
                for (int i = 0; i < lockerArray.size(); i++) {
                    Locker locker = new Locker();
                    JsonObject lockerObject = lockerArray.get(i).getAsJsonObject();
                    locker.setSerial(lockerObject.get("serial").getAsString());
                    locker.setPhoneNum(lockerObject.get("phoneNum").getAsString());
                    locker.setDescription(lockerObject.get("description").getAsString());
                    locker.setCreateTime(lockerObject.get("createTime").getAsString());
                    locker.setLastOpenTime(lockerObject.get("lastOpenTime").getAsString());
                    locker.setHwType(lockerObject.get("hwType").getAsString());
                    lockers.add(locker);
                }
            } catch (IOException e) {
                Log.e("LockerData", "Failed to parse https response data");
            }
        }
        Bundle data = new Bundle();
        data.putSerializable("lockerList", (Serializable) lockers);
        Message message = new Message();
        message.what = MESSAGE_lOCKER_LIST;
        message.setData(data);
        message.setTarget(mHandler);
        message.sendToTarget();
    }

    public void getLockerBySerial(String serial) {
        this.sign = MD5Util.md5("/locker/get" + this.enc_pass);
        this.url = BASE_REQUEST_URL + "/locker/get?appid=" + this.appid + "&sign=" + sign;

        if (serial != null && !serial.equals("")) {
            url = url + "&serial=" + serial;
        }
        /*开始子进程*/
        this.start();
    }

    public void getAllLocker() {
        getLockerBySerial(null);
    }

}
