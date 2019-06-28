package com.feiyang.elocker;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static com.feiyang.elocker.Constant.DATE_PATTERN;


public class Test {
    public static void main(String[] args) throws IOException {
        TimeZone tz = TimeZone.getDefault();
        Date date = new Date();
        SimpleDateFormat sf = new SimpleDateFormat(DATE_PATTERN);
        String time = sf.format(date);
        sf.setTimeZone(TimeZone.getDefault());
        System.out.println(sf.getTimeZone());
    }

    public enum Operation {
        Open("Open1"), Close("Close1");
        private String description;

        Operation(String description) {

            this.description = description;

        }

        public static Operation from(String value) {
            Operation operation;
            try {
                operation = Operation.valueOf(value);
            } catch (Exception e) {
                operation = Operation.Close;
            }
            return operation;
        }

        @Override
        public String toString() {
            return this.description;
        }
    }

}