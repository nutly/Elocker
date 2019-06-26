package com.feiyang.elocker;

import com.feiyang.elocker.util.HttpsUtil;
import okhttp3.Response;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        Response response = HttpsUtil.get("https://180.166.27.198/locker/get?appid=15851841387&sign=be3dcf339039565d1026eaca6b0f93d8");
        System.out.println(response.body().string());
    }
}