package com.feiyang.elocker.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static com.feiyang.elocker.Constant.DATE_PATTERN;

public class OperationLog {
    private String phoneNum;
    private String serial;
    private Operation operation;
    /*字符串类型的时间 YYYY-MM-DD HH-MM-SS*/
    private String sTime;
    private String description;

    public OperationLog() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        sdf.setTimeZone(TimeZone.getDefault());
        this.sTime = sdf.format(new Date());
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public String getsTime() {
        return sTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public enum Operation {
        Open("Open"),
        Lock("Lock"),
        Add_Locker("Add_Locker"),
        Delete_Locker("Delete_Locker"),
        Modify_Locker("Modify_Locker"),
        Modify_Authorization("Modify_Authorization"),
        Add_Authorization("Add_Authorization"),
        Delete_Authorization("Delete_Authorization"),
        Login("Login"),
        Login_Out("Login_Out");

        private String description;

        Operation(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return this.description;
        }
    }
}
