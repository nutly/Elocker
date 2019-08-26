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

    public void setsTime(String sTime) {
        this.sTime = sTime;
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
        Modify_Locker("Modify_Locker"),
        Delete_Locker("Delete_Locker"),
        Add_Locker("Add_Locker"),
        Transfer_Locker("Transfer_Locker"),
        Modify_Authorization("Modify_Authorization"),
        Delete_Authorization("Delete_Authorization"),
        Add_Authorization("Add_Authorization"),
        Login("Login"),
        Change_Password("Change_Password"),
        Reset_Password("Reset_Password"),
        Add_User("Add_User"),
        Get_Verification_Code("Get_Verification_Code"),
        Unknown("Unknow");

        private String description;

        Operation(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return this.description;
        }

        public static Operation from(String value) {
            Operation operation;
            try {
                operation = Operation.valueOf(value);
            } catch (Exception e) {
                operation = Operation.Unknown;
            }
            return operation;
        }
    }
}
