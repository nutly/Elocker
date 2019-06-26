package com.feiyang.elocker.util;

import android.support.annotation.NonNull;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
    @NonNull
    public static String md5(String data) {
        String md5Data = "aa123456789012345678901234567890";
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(data.getBytes());
            StringBuilder result = new StringBuilder();
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result.append(temp);
            }
            md5Data = result.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.e("MD5Util", "Failed to hash for " + data);
        }
        return md5Data;
    }
}
