package com.feiyang.elocker.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.feiyang.elocker.Constant;
import com.feiyang.elocker.activity.LoginActivity;

public class LoginUtil {
    public static SharedPreferences sharedPreferences;

    public static void validLogin(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(Constant.PROPERTY_FILE_NAME, Context.MODE_PRIVATE);
        }
        if (!sharedPreferences.contains("phoneNum") ||
                !sharedPreferences.contains("password") ||
                !sharedPreferences.contains("apiKey")) {
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
        }
    }

    public static void saveLoginInfo(Context context, String phoneNum, String password, String apiKey) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(Constant.PROPERTY_FILE_NAME, Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("phoneNum", phoneNum);
        editor.putString("password", password);
        editor.putString("apiKey", apiKey);
        editor.commit();
    }

    public static void clearLoginInfo(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(Constant.PROPERTY_FILE_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.commit();
        }
    }
}
