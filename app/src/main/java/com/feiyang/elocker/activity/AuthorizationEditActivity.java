package com.feiyang.elocker.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.feiyang.elocker.R;
import com.feiyang.elocker.model.Authorization;
import com.feiyang.elocker.model.Locker;
import com.feiyang.elocker.rest.AuthorizationRest;

import java.util.Calendar;
import java.util.TreeSet;

public class AuthorizationEditActivity extends AppCompatActivity implements View.OnClickListener,
        CheckBox.OnCheckedChangeListener, RadioGroup.OnCheckedChangeListener {

    private Authorization mAuthorization = new Authorization();
    private Locker mLocker;
    private LinearLayout mDailyStartTimeLayout, mDailyEndTimeLayout, mDayOptionLayout;
    private TextView mSerial, mLockerName, mStartTime, mEndTime, mDailyStartTime, mDailyEndTime;
    private EditText mToAccount, mDescription;
    private CheckBox mMonday, mTuesday, mWednesday, mThursday, mFriday, mSaturday, mSunday;
    /*保存选择的星期几集合，例如*/
    private TreeSet<Integer> mWeekDaySet = new TreeSet<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization_edit);
        initView();

        /*设置返回按钮*/
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.authorization);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mStartTime.setOnClickListener(this);
        mEndTime.setOnClickListener(this);
        mDailyStartTime.setOnClickListener(this);
        mDailyEndTime.setOnClickListener(this);

        mMonday.setOnCheckedChangeListener(this);
        mTuesday.setOnCheckedChangeListener(this);
        mWednesday.setOnCheckedChangeListener(this);
        mThursday.setOnCheckedChangeListener(this);
        mFriday.setOnCheckedChangeListener(this);
        mSaturday.setOnCheckedChangeListener(this);
        mSunday.setOnCheckedChangeListener(this);
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
    }

    /*响应返回键*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, LockerListActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        /*点击事件不再传递*/
        return true;
    }

    /*单选框监听*/
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
            /*设置一天具体哪个时间段可以执行*/
            case R.id.authorization_whole_day:
                mDailyStartTime.setVisibility(View.GONE);
                mDailyEndTimeLayout.setVisibility(View.GONE);
                mStartTime.setText("00:00:00");
                mEndTime.setText("23:59:00");
                break;
            case R.id.authorization_custom_time:
                mDailyStartTimeLayout.setVisibility(View.VISIBLE);
                mDailyEndTimeLayout.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    /*CheckBox 复选框事件监听*/
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
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
            default:
                break;
        }
    }

    /*时间选择控件监听*/
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
                        /*回调返回的monthOfYear比实际值小1*/
                        monthOfYear++;
                        String startDate = year + "-" + monthOfYear + "-" + dayOfMonth + " 00:00:00";
                        mStartTime.setText(startDate);
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
                        monthOfYear++;
                        String endDate = year + "-" + monthOfYear + "-" + dayOfMonth + " 23:59:00";
                        mEndTime.setText(endDate);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePicker.show();
                break;
            case R.id.authorization_start_time:
                timePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String startTime = hourOfDay + ":" + minute + ":00";
                        mDailyStartTime.setText(startTime);
                    }
                }, 0, 0, true);
                timePicker.show();
                break;
            case R.id.authorization_end_time:
                timePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String endTime = hourOfDay + ":" + minute + ":00";
                        mDailyEndTime.setText(endTime);
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
                String weekday = mWeekDaySet.toString().replace("[", "").replace("]", "");
                mAuthorization.setWeekDay(weekday);
                mAuthorization.setDailyStartTime(mDailyStartTime.getText().toString());
                mAuthorization.setDailyEndTime(mDailyEndTime.getText().toString());

                AuthorizationRest authorizationRest = new AuthorizationRest(this);
                authorizationRest.addAuthorization(mAuthorization);
                /*跳转到授权查看界面*/
                Intent intent = new Intent(this.getApplicationContext(), AuthorizationActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void initView() {
        mSerial = findViewById(R.id.authorization_serial);
        mLockerName = findViewById(R.id.authorization_locker_name);
        mToAccount = findViewById(R.id.authorization_list_to_account);
        mStartTime = findViewById(R.id.authorization_start_date);
        mEndTime = findViewById(R.id.authorization_end_date);
        mDailyStartTime = findViewById(R.id.authorization_start_time);
        mDailyEndTime = findViewById(R.id.authorization_end_time);
        mDescription = findViewById(R.id.authorization_description);
        mDailyStartTimeLayout = findViewById(R.id.authorization_start_time_layout);
        mDailyEndTimeLayout = findViewById(R.id.authorization_end_time_layout);
        mDayOptionLayout = findViewById(R.id.authrization_weekday_layout);
        Button confirmBtn = findViewById(R.id.authorization_confirm_btn);

        RadioGroup timeOption = (RadioGroup) findViewById(R.id.authorization_time_option);
        RadioGroup dayOption = (RadioGroup) findViewById(R.id.authorization_day_option);
        mMonday = findViewById(R.id.authorization_monday);
        mTuesday = findViewById(R.id.authorization_tuesday);
        mWednesday = findViewById(R.id.authorization_wednesday);
        mThursday = findViewById(R.id.authorization_thursday);
        mFriday = findViewById(R.id.authorization_friday);
        mSaturday = findViewById(R.id.authorization_saturday);
        mSunday = findViewById(R.id.authorization_sunday);

        /*设置默认参数*/
        mDailyStartTime.setText("00:00:00");
        mDailyEndTime.setText("23:59:00");
        for (int i = 1; i < 8; i++) {
            mWeekDaySet.add(i);
        }

        /*设置一周选择哪些天*/
        dayOption.setOnCheckedChangeListener(this);
        /*设置一天具体的时间范围*/
        timeOption.setOnCheckedChangeListener(this);
        confirmBtn.setOnClickListener(this);
    }
}
