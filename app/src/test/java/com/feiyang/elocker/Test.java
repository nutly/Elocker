package com.feiyang.elocker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Test {
    public static void main(String[] args) {
        SimpleDateFormat sdf = new SimpleDateFormat(Constant.DATE_PATTERN);
        Calendar calendar = Calendar.getInstance();
        System.out.println(sdf.format(calendar.getTime()));
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        System.out.println(sdf.format(calendar.getTime()));
        Calendar calendar1 = Calendar.getInstance();
    }
}