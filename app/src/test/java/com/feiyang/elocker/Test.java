package com.feiyang.elocker;

public class Test {
    public static void main(String[] args) {

        System.out.println(isPowerLow("e6ba10"));
    }

    private static boolean isPowerLow(String rCode) {
        boolean isPowerLow = false;
        int status;
        try {
            status = Integer.parseInt(rCode.substring(5, 6));
        } catch (Exception e) {
            status = 4;
        }
        if (status == 0) {
            isPowerLow = true;
        } else if (status == 1) {
        }
        return isPowerLow;
    }

    private static byte[] hexStringToBytes(String hex) {
        hex = hex.toUpperCase();
        int length = hex.length() / 2;
        byte[] b = new byte[length];
        char[] hc = hex.toCharArray();
        for (int i = 0; i < length; i++) {
            b[i] = (byte) ((charToByte(hc[2 * i]) & 0xFF) << 4 |
                    charToByte(hc[2 * i + 1]) & 0xFF);
        }
        return b;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
}