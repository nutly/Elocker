package com.feiyang.elocker.util;

import android.support.annotation.NonNull;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
    /*  对字符串进行hash
     * @param data
     * @return java.lang.String
     */
    @NonNull
    public static String md5(String data) {
        return md5Byte(data.getBytes());
    }

    /* 对16进制字符串进行hash
     * @param hexData 16进制数据
     * @return  16 进制字符串
     */
    @NonNull
    public static String md5Hex(String hexData) {
        hexData = hexData.toUpperCase();
        char[] hexChars = hexData.toCharArray();
        int length = hexData.length() / 2;
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            bytes[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return md5Byte(bytes);
    }

    private static String md5Byte(byte[] data) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");

        } catch (NoSuchAlgorithmException e) {
            Log.e("MD5Util", "No MD5 Algorithm");
            return "un_encryption_data";
        }
        byte[] hashData = digest.digest(data);
        StringBuilder hexResult = new StringBuilder();
        for (byte b : hashData) {
            if ((b & 0xFF) < 0x10) {
                hexResult.append("0");
            }
            hexResult.append(Integer.toHexString(b & 0xFF));
        }
        return hexResult.toString();
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
}
