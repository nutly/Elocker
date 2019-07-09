package com.feiyang.elocker.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.*;
import com.feiyang.elocker.R;
import com.feiyang.elocker.model.Authorization;
import com.feiyang.elocker.model.Locker;
import com.feiyang.elocker.rest.AuthorizationRest;

import java.util.Calendar;
import java.util.TreeSet;

public class AuthorizationEditActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private Authorization mAuthorization = new Authorization();
    private Locker mLocker;
    private LinearLayout mStartTimeLayout, mEndTimeLayout, mDayOptionLayout;
    private TextView mSerial, mLockerName, mStartDate, mEndDate, mStartTime, mEndTime;
    private EditText mToAccount, mDescription;
    private CheckBox mMonday, mTuesday, mWednesday, mThursday, mFriday, mSaturday, mSunday;
    /*保存选择的星期几集合，例如*/
    private TreeSet<Integer> mWeekDaySet = new TreeSet<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization_edit);
        initView();

        mStartDate.setOnClickListener(this);
        mEndDate.setOnClickListener(this);
        mStartTime.setOnClickListener(this);
        mEndTime.setOnClickListener(this);

    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = this.getIntent();
        Bundle data = intent.getExtras();
        if (data != null && data.containsKey("locker")) {
            mLocker = (Locker) data.getSerializable("locker");
            mSerial.setText(mLocker.getSerial());
            mLockerName.setText(mLocker.getDescription());
        }

        mMonday.setOnClickListener(this);
        mTuesday.setOnClickListener(this);
        mWednesday.setOnClickListener(this);
        mThursday.setOnClickListener(this);
        mFriday.setOnClickListener(this);
        mSaturday.setOnClickListener(this);
        mSunday.setOnClickListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            /*设置一周哪些天可以执行*/
            case R.id.authorization_every_day:
                mDayOptionLayout.setVisibility(View.GONE);
                for (int i = 1; i < 8; i++)
                    mWeekDaySet.add(i);
                break;
            case R.id.authorization_custom_day:
                for (int i = 1; i < 8; i++)
                    mWeekDaySet.add(i);
                mMonday.setChecked(true);
                mTuesday.setChecked(true);
                mWednesday.setChecked(true);
                mThursday.setChecked(true);
                mFriday.setChecked(true);
                mSaturday.setChecked(true);
                mSunday.setChecked(true);
                mDayOptionLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.authorization_monday:
                if (mMonday.isChecked())
                    mWeekDaySet.add(1);
                else
                    mWeekDaySet.remove(1);
                break;
            case R.id.authorization_tuesday:
                if (mTuesday.isChecked())
                    mWeekDaySet.add(2);
                else
                    mWeekDaySet.remove(2);
                break;
            case R.id.authorization_wednesday:
                if (mWednesday.isChecked())
                    mWeekDaySet.add(3);
                else
                    mWeekDaySet.remove(3);
                break;
            case R.id.authorization_thursday:
                if (mThursday.isChecked())
                    mWeekDaySet.add(4);
                else
                    mWeekDaySet.remove(4);
                break;
            case R.id.authorization_friday:
                if (mFriday.isChecked())
                    mWeekDaySet.add(5);
                else
                    mWeekDaySet.remove(5);
                break;
            case R.id.authorization_saturday:
                if (mSaturday.isChecked())
                    mWeekDaySet.add(6);
                else
                    mWeekDaySet.remove(6);
                break;
            case R.id.authorization_sunday:
                if (mSunday.isChecked())
                    mWeekDaySet.add(7);
                else
                    mWeekDaySet.remove(7);
                break;
            /*设置一天具体哪个时间段可以执行*/
            case R.id.authorization_whole_day:
                mStartTimeLayout.setVisibility(View.GONE);
                mEndTimeLayout.setVisibility(View.GONE);
                mStartTime.setText("00:00:00");
                mEndTime.setText("23:59:00");
                break;
            case R.id.authorization_custom_time:
                mStartTimeLayout.setVisibility(View.VISIBLE);
                mEndTimeLayout.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    /*处理时间选择*/
    @Override
    public void onClick(View v) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePicker;
        final TimePickerDialog timePicker;
        switch (v.getId()) {
            case R.id.authorization_start_date:
                datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        String startDate = year + "-" + monthOfYear + "-" + dayOfMonth + " 00:00:00";
                        mStartDate.setText(startDate);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePicker.show();
                break;
            case R.id.authorization_end_date:
                calendar.add(Calendar.DAY_OF_MONTH, 7);
                datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        String endDate = year + "-" + monthOfYear + "-" + dayOfMonth + " 23:59:00";
                        mEndDate.setText(endDate);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePicker.show();
                break;
            case R.id.authorization_start_time:
                timePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String startTime = hourOfDay + ":" + minute + ":00";
                        mStartTime.setText(startTime);
                    }
                }, 0, 0, true);
                timePicker.show();
                break;
            case R.id.authorization_end_time:
                timePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String endTime = hourOfDay + ":" + minute + ":00";
                        mEndTime.setText(endTime);
                    }
                }, 23, 59, true);
                timePicker.show();
                break;
            /*提交*/
            case R.id.authorization_confirm_btn:
                mAuthorization.setSerial(mLocker.getSerial());
                mAuthorization.setFromAccount(mLocker.getPhoneNum());
                mAuthorization.setToAccount(mToAccount.getText().toString());
                mAuthorization.setStartTime(mStartTime.getText().toString());
                mAuthorization.setEndTime(mEndTime.getText().toString());
                mAuthorization.setDescription(mDescription.getText().toString());
                String weekDay = mWeekDaySet.toString().replace("[", "").replace("]", "");
                mAuthorization.setWeekDay(weekDay);
                mAuthorization.setDailyStartTime(mStartTime.getText().toString());
                mAuthorization.setDailyEndTime(mEndTime.getText().toString());

                AuthorizationRest authorizationRest = new AuthorizationRest();
                authorizationRest.addAuthorization(mAuthorization);
                break;
            default:
                break;
        }
    }

    private void initView() {
        mSerial = (TextView) findViewById(R.id.authorization_serial);
        mLockerName = (TextView) findViewById(R.id.authorization_locker_name);
        mToAccount = (EditText) findViewById(R.id.authorization_list_to_account);
        mStartDate = (TextView) findViewById(R.id.authorization_start_date);
        mEndDate = (TextView) findViewById(R.id.authorization_end_date);
        mStartTime = (TextView) findViewById(R.id.authorization_start_time);
        mEndTime = (TextView) findViewById(R.id.authorization_end_time);
        mDescription = (EditText) findViewById(R.id.authorization_description);
        mStartTimeLayout = (LinearLayout) findViewById(R.id.authorization_start_time_layout);
        mEndTimeLayout = (LinearLayout) findViewById(R.id.authorization_end_time_layout);
        mDayOptionLayout = (LinearLayout) findViewById(R.id.authrization_weekday_layout);
        Button confirmBtn = (Button) findViewById(R.id.authorization_confirm_btn);

        RadioGroup timeOption = (RadioGroup) findViewById(R.id.authorization_time_option);
        RadioGroup dayOption = (RadioGroup) findViewById(R.id.authorization_day_option);
        mMonday = (CheckBox) findViewById(R.id.authorization_monday);
        mTuesday = (CheckBox) findViewById(R.id.authorization_tuesday);
        mWednesday = (CheckBox) findViewById(R.id.authorization_wednesday);
        mThursday = (CheckBox) findViewById(R.id.authorization_thursday);
        mFriday = (CheckBox) findViewById(R.id.authorization_friday);
        mSaturday = (CheckBox) findViewById(R.id.authorization_saturday);
        mSunday = (CheckBox) findViewById(R.id.authorization_sunday);

        /*设置一周选择哪些天*/
        dayOption.setOnCheckedChangeListener(this);
        /*设置一天具体的时间范围*/
        timeOption.setOnCheckedChangeListener(this);
        confirmBtn.setOnClickListener(this);
    }

}
