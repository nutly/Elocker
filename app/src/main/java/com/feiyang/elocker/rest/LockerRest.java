package com.feiyang.elocker.rest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.feiyang.elocker.Constant;
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

public class LockerRest extends Thread {

    private String mPassword;
    private String mPhoneNum;
    private Handler mHandler;
    private Locker mLocker;
    private Task mTask;
    private String toAccount;

    public LockerRest(Context context) {
        super();
        this.mLocker = new Locker();
        SharedPreferences sp = context.getSharedPreferences(Constant.PROPERTY_FILE_NAME, Context.MODE_PRIVATE);
        this.mPhoneNum = sp.getString("phoneNum", "");
        this.mPassword = sp.getString("password", "");
    }

    public void getAllLocker(Handler handler) {
        mTask = Task.GET_ALL_lOCKER;
        mHandler = handler;
        /*开启子进程*/
        this.start();
    }

    public void getLockerBySerial(Handler handler, String serial) {
        mLocker.setSerial(serial);
        mHandler = handler;
        mTask = Task.GET_LOCKER_BY_SERIAL;
        this.start();
    }

    public void updateLockerDescription(Locker locker) {
        mLocker = locker;
        mTask = Task.MODIFY_LOCKER_DESCRIPTION;
        this.start();
    }

    public void transferLocker(String fromAccount, String serial, String toAccount) {
        mLocker.setSerial(serial);
        mLocker.setPhoneNum(fromAccount);
        this.toAccount = toAccount;
        mTask = Task.TRANSFER_LOCKER;
        this.start();
    }

    public void addLocker(Handler handler, String serial, String description) {
        mLocker.setSerial(serial);
        mLocker.setDescription(description);
        mHandler = handler;
        mTask = Task.ADD_LOCKER;
        this.start();
    }

    public void delLocker(String serial) {
        mLocker.setSerial(serial);
        mTask = Task.DELETE_LOCKER;
        this.start();
    }

    @Override
    public void run() {
        if (mTask != null) {
            switch (mTask) {
                case GET_ALL_lOCKER:
                    mLocker.setSerial(null);
                    getLockerTask();
                    break;
                case GET_LOCKER_BY_SERIAL:
                    getLockerTask();
                    break;
                case ADD_LOCKER:
                    addLockerTask();
                    break;
                case MODIFY_LOCKER_DESCRIPTION:
                    modifyLockerDescriptionTask();
                    break;
                case TRANSFER_LOCKER:
                    transferLockerTask();
                    break;
                case DELETE_LOCKER:
                    deleteLockerTask();
                    break;
                default:
                    break;
            }
        }
    }

    private void getLockerTask() {
        String sign = MD5Util.md5("/locker/get" + this.mPassword);
        String url = BASE_REQUEST_URL + "/locker/get?appid=" + this.mPhoneNum + "&sign=" + sign;
        if (mLocker.getSerial() != null && !mLocker.getSerial().equals("")) {
            url = url + "&serial=" + mLocker.getSerial();
        }

        List<Locker> lockers = new ArrayList<Locker>();
        Message message = new Message();
        Bundle data = new Bundle();

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
                        locker.setToggleTimes(lockerObject.get("toggleTimes").getAsInt());
                        lockers.add(locker);
                    }
                } catch (IOException e) {
                    Log.e("LockerRest", "Failed to parse https response data");
                    data.putInt("error", -1);
                }
            } else {
                data.putInt("error", -1);
            }
            response.close();
        } else {
            data.putInt("error", -1);
        }
        data.putSerializable("lockerList", (Serializable) lockers);
        message.what = MESSAGE_lOCKER_LIST;
        message.setData(data);
        message.setTarget(mHandler);
        message.sendToTarget();
    }

    private void modifyLockerDescriptionTask() {
        String sign = MD5Util.md5("/locker/update" + this.mPassword);
        String url = BASE_REQUEST_URL + "/locker/update";

        String serial = mLocker.getSerial();
        String description = mLocker.getDescription();
        if (serial != null && description != null) {
            JsonObject params = new JsonObject();
            params.addProperty("appid", this.mPhoneNum);
            params.addProperty("sign", sign);
            params.addProperty("serial", serial);
            params.addProperty("description", description);
            Response response = HttpsUtil.post(url, params);
            if (response != null) {
                response.close();
            }
        }
    }

    private void transferLockerTask() {
        String sign = MD5Util.md5("/locker/transfer" + this.mPassword);
        String url = BASE_REQUEST_URL + "/locker/transfer";

        if (mLocker.getSerial() != null && mLocker.getPhoneNum() != null && toAccount != null) {
            JsonObject params = new JsonObject();
            params.addProperty("appid", this.mPhoneNum);
            params.addProperty("sign", sign);
            params.addProperty("serial", mLocker.getSerial());
            params.addProperty("toAccount", toAccount);
            Response response = HttpsUtil.post(url, params);
            if (response != null) {
                response.close();
            }
        }
    }

    private void addLockerTask() {
        String sign = MD5Util.md5("/locker/add" + this.mPassword);
        String url = BASE_REQUEST_URL + "/locker/add";
        if (mLocker.getSerial() != null) {
            JsonObject params = new JsonObject();
            params.addProperty("appid", mPhoneNum);
            params.addProperty("sign", sign);
            params.addProperty("serial", mLocker.getSerial());
            params.addProperty("description", mLocker.getDescription());

            Response response = HttpsUtil.post(url, params);
            Bundle data = new Bundle();
            if (response != null) {
                data.putInt("status", response.code());
                response.close();
            } else {
                data.putInt("status", -1);
            }
            Message message = new Message();
            message.what = Constant.MESSAGE_ADD_LOCKER_STATUS;
            message.setData(data);
            message.setTarget(mHandler);
            message.sendToTarget();
        }
    }

    private void deleteLockerTask() {
        String sign = MD5Util.md5("/locker/delete" + this.mPassword);
        String url = BASE_REQUEST_URL + "/locker/delete";

        String serial = mLocker.getSerial();
        if (serial != null) {
            JsonObject params = new JsonObject();
            params.addProperty("appid", this.mPhoneNum);
            params.addProperty("sign", sign);
            JsonArray serials = new JsonArray();
            serials.add(mLocker.getSerial());
            params.add("lockerSerials", serials);

            Response response = HttpsUtil.post(url, params);
            if (response != null) {
                response.close();
            }
        }
    }

    private enum Task {
        GET_ALL_lOCKER, GET_LOCKER_BY_SERIAL, MODIFY_LOCKER_DESCRIPTION,
        TRANSFER_LOCKER, DELETE_LOCKER, ADD_LOCKER
    }
}
