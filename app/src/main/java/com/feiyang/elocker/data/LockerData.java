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
    private Handler mHandler;
    private String mSerial;
    private Task mTask;

    public LockerData(Handler handler) {
        super();
        this.mHandler = handler;
        //TODO 更换成直接从配置文件获取当前登录的用户名和加密后的密码
        this.appid = "15851841387";
        this.enc_pass = MD5Util.md5(this.appid + MD5Util.md5("12345"));
    }

    public void getAllLocker() {
        mTask = Task.GET_ALL_lOCKER;
        /*开启子进程*/
        this.start();
    }

    public void getLockerBySerial(String serial) {
        mSerial = serial;
        mTask = Task.GET_LOCKER_BY_SERIAL;
        this.start();
    }

    @Override
    public void run() {
        if (mTask != null) {
            switch (mTask) {
                case GET_ALL_lOCKER:
                    mSerial = null;
                    getLockerTask();
                    break;
                case GET_LOCKER_BY_SERIAL:
                    getLockerTask();
                    break;
                default:
                    break;
            }
        }
    }

    private void getLockerTask() {
        String sign = MD5Util.md5("/locker/get" + this.enc_pass);
        String url = BASE_REQUEST_URL + "/locker/get?appid=" + this.appid + "&sign=" + sign;
        if (mSerial != null && !mSerial.equals("")) {
            url = url + "&serial=" + mSerial;
        }

        List<Locker> lockers = new ArrayList<Locker>();
        Response response = HttpsUtil.get(url);
        if (response != null) {
            if (response.isSuccessful()) {
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
            response.close();
        }
        Bundle data = new Bundle();
        data.putSerializable("lockerList", (Serializable) lockers);
        Message message = new Message();
        message.what = MESSAGE_lOCKER_LIST;
        message.setData(data);
        message.setTarget(mHandler);
        message.sendToTarget();
    }

    private enum Task {
        GET_ALL_lOCKER, GET_LOCKER_BY_SERIAL
    }
}
