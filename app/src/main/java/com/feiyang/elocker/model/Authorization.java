package com.feiyang.elocker.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.feiyang.elocker.Constant.DATE_PATTERN;

public class Authorization implements Serializable {
    private Long id;
    private String serial;
    private String lockerName;
    private String fromAccount;
    private String toAccount;
    private String startTime;
    private String endTime;
    private String description;
    private String weekday;
    private String dailyStartTime;
    private String dailyEndTime;

    public Authorization() {
        setDefault();
    }

    private void setDefault() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);

        this.startTime = sdf.format(calendar.getTime());
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        this.endTime = sdf.format(calendar.getTime());

        this.description = "Authorization";
        this.weekday = "1,2,3,4,5,6,7";
        this.dailyStartTime = "00:00:00";
        this.dailyEndTime = "23:59:00";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(String fromAccount) {
        this.fromAccount = fromAccount;
    }

    public String getToAccount() {
        return toAccount;
    }

    public void setToAccount(String toAccount) {
        this.toAccount = toAccount;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {

        this.description = description != null ? description : "";
    }

    public String getWeekDay() {
        return weekday;
    }

    public void setWeekDay(String weekday) {
        this.weekday = weekday;
    }

    public String getDailyStartTime() {
        return dailyStartTime;
    }

    public void setDailyStartTime(String dailyStartTime) {
        this.dailyStartTime = dailyStartTime;
    }

    public String getDailyEndTime() {
        return dailyEndTime;
    }

    public void setDailyEndTime(String dailyEndTime) {
        this.dailyEndTime = dailyEndTime;
    }

    public String getLockerName() {
        return lockerName;
    }

    public void setLockerName(String lockerName) {
        this.lockerName = lockerName;
    }
}
