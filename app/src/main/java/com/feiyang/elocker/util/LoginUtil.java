package com.feiyang.elocker.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;
import com.feiyang.elocker.Constant;
import com.feiyang.elocker.R;
import com.feiyang.elocker.activity.LoginActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LoginUtil {
    public static SharedPreferences sharedPreferences;

    public static void validLogin(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(Constant.PROPERTY_FILE_NAME, Context.MODE_PRIVATE);
        }
        if (sharedPreferences.contains("phoneNum") &&
                sharedPreferences.contains("password") &&
                sharedPreferences.contains("apiKey")) {
            String timeStr = sharedPreferences.getString("createTime", null);
            SimpleDateFormat sdf = new SimpleDateFormat(Constant.DATE_PATTERN);
            if (timeStr != null) {
                try {
                    Date date = sdf.parse(timeStr);
                    Calendar expireTime = Calendar.getInstance();
                    expireTime.setTime(date);
                    expireTime.add(Calendar.DAY_OF_MONTH, Constant.LOGIN_EXPIRED_DAYS);
                    if (Calendar.getInstance().before(expireTime)) {
                        return;
                    }
                } catch (Exception e) {
                    Log.e("LoginUtil", "Invalid time string pattern");
                }
            }
        }
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    public static void saveLoginInfo(Context context, String phoneNum, String password, String apiKey) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(Constant.PROPERTY_FILE_NAME, Context.MODE_PRIVATE);
        }
        String time = new SimpleDateFormat(Constant.DATE_PATTERN).format(new Date());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("phoneNum", phoneNum);
        editor.putString("password", password);
        editor.putString("apiKey", apiKey);
        editor.putString("createTime", time);
        editor.commit();
    }

    public static void returnToLogin(Context context) {
        Toast.makeText(context, R.string.un_authorized_request, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }
}
