package com.feiyang.elocker.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.feiyang.elocker.Constant;
import com.feiyang.elocker.R;
import com.feiyang.elocker.adpter.LogRecyclerViewAdapter;
import com.feiyang.elocker.fragment.NavigationFragment;
import com.feiyang.elocker.model.OperationLog;
import com.feiyang.elocker.rest.LogRest;
import com.feiyang.elocker.util.LoginUtil;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LogActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mLastDay, mLastWeek, mLastMonth, mCustom;
    private RecyclerView mRecyclerView;
    private LogRecyclerViewAdapter mViewAdapter;
    private LogHandler mHandler;
    private List<OperationLog> mLogs = new ArrayList<>();
    private String mSerial, mStartTime, mEndTime;
    private int mPage = 0;

    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        int mLastVisibleItemPosition;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                mLastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            }

            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                if (!recyclerView.canScrollVertically(1) && mViewAdapter.hasMoreData) {
                    mPage++;
                    loadMoreData();
                }
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = this.getIntent();
        if (intent.hasExtra("serial")) {
            this.mSerial = intent.getStringExtra("serial");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*判断当前是否已经登录*/
        LoginUtil.validLogin(this);
        setContentView(R.layout.activity_log);
        mViewAdapter = new LogRecyclerViewAdapter(mLogs);
        mHandler = new LogHandler(this);
        setView();

    }

    private void setView() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.log);
        NavigationFragment.newInstance(this, R.id.navigation_in_log_list);
        mRecyclerView = findViewById(R.id.log_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));
        mRecyclerView.setAdapter(mViewAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this.getApplicationContext(), DividerItemDecoration.VERTICAL));
        mRecyclerView.addOnScrollListener(mOnScrollListener);

        mLastDay = findViewById(R.id.navigation_last_day);
        mLastWeek = findViewById(R.id.navigation_last_week_);
        mLastMonth = findViewById(R.id.navigation_last_month);
        mCustom = findViewById(R.id.navigation_custom);

        mLastDay.setOnClickListener(this);
        mLastWeek.setOnClickListener(this);
        mLastMonth.setOnClickListener(this);
        mCustom.setOnClickListener(this);
    }

    private void loadMoreData() {
        LogRest logRest = new LogRest(this);
        logRest.getLog(mSerial, mStartTime, mEndTime, mPage, mHandler);
    }

    @Override
    public void onClick(View v) {
        Calendar calendar = Calendar.getInstance();
        Date now = new Date();
        calendar.setTime(now);
        mLogs.clear();
        mPage = 0;
        switch (v.getId()) {
            case R.id.navigation_last_day:
                setSelected(R.id.navigation_last_day);
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                mStartTime = new SimpleDateFormat(Constant.DATE_PATTERN).format(calendar.getTime());
                mEndTime = new SimpleDateFormat(Constant.DATE_PATTERN).format(now);
                loadMoreData();
                break;
            case R.id.navigation_last_week_:
                setSelected(R.id.navigation_last_week_);
                calendar.add(Calendar.DAY_OF_MONTH, -7);
                mStartTime = new SimpleDateFormat(Constant.DATE_PATTERN).format(calendar.getTime());
                mEndTime = new SimpleDateFormat(Constant.DATE_PATTERN).format(now);
                loadMoreData();
                break;
            case R.id.navigation_last_month:
                setSelected(R.id.navigation_last_month);
                calendar.add(Calendar.MONTH, -1);
                mStartTime = new SimpleDateFormat(Constant.DATE_PATTERN).format(calendar.getTime());
                mEndTime = new SimpleDateFormat(Constant.DATE_PATTERN).format(now);
                loadMoreData();
                break;
            case R.id.navigation_custom:
                setSelected(R.id.navigation_custom);
                final CustomLogSearch customLogSearch = new CustomLogSearch(LogActivity.this);
                AlertDialog dialog = new AlertDialog.Builder(LogActivity.this)
                        .setView(customLogSearch.getView())
                        .setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mStartTime = customLogSearch.mCustomStartTime.getText().toString();
                                mEndTime = customLogSearch.mCustomEndTime.getText().toString();
                                loadMoreData();
                            }
                        })
                        .create();
                dialog.show();
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getColor(R.color.black));
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setAllCaps(false);
                break;
            default:
                break;
        }
    }

    private void setSelected(int resId) {
        TextView[] tvs = {mLastDay, mLastWeek, mLastMonth, mCustom};
        for (TextView tv : tvs) {
            if (tv.getId() == resId) {
                tv.setTextColor(getColor(R.color.colorLightBlue));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            } else {
                tv.setTextColor(getColor(R.color.black));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            }
        }
    }

    private class CustomLogSearch implements View.OnClickListener {
        public EditText mCustomStartTime, mCustomEndTime;
        private View mView;
        private Context context;

        public CustomLogSearch(Context context) {
            this.context = context;
        }

        public View getView() {
            mView = View.inflate(context, R.layout.custom_log_search, null);
            mCustomStartTime = mView.findViewById(R.id.log_search_start_date);
            mCustomEndTime = mView.findViewById(R.id.log_search_end_date);

            /*设置初始值*/
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            SimpleDateFormat sdf = new SimpleDateFormat(Constant.DATE_PATTERN);
            mCustomEndTime.setText(sdf.format(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_MONTH, -3);
            mCustomStartTime.setText(sdf.format(calendar.getTime()));

            mCustomStartTime.setOnClickListener(this);
            mCustomEndTime.setOnClickListener(this);

            return mView;
        }

        @Override
        public void onClick(View v) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            switch (v.getId()) {
                case R.id.log_search_start_date:
                    new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear,
                                              int dayOfMonth) {
                            /*回调返回的monthOfYear比实际值小1*/
                            monthOfYear++;
                            String dateTime = year + "-" + monthOfYear + "-" + dayOfMonth + " 00:00:00";
                            mCustomStartTime.setText(dateTime);
                        }
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH - 3)).show();
                    break;
                case R.id.log_search_end_date:
                    new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear,
                                              int dayOfMonth) {
                            /*回调返回的monthOfYear比实际值小1*/
                            monthOfYear++;
                            String dateTime = year + "-" + monthOfYear + "-" + dayOfMonth + " 23:59:59";
                            mCustomEndTime.setText(dateTime);
                        }
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                    break;
                default:
                    break;
            }
        }
    }

    private static class LogHandler extends Handler {
        private final WeakReference<LogActivity> mLogActivity;

        public LogHandler(LogActivity logActivity) {
            this.mLogActivity = new WeakReference<LogActivity>(logActivity);
        }

        @Override
        public void handleMessage(Message message) {
            if (message.what == Constant.MESSAGE_GET_LOG) {
                LogActivity logActivity = mLogActivity.get();
                switch (message.getData().getInt("status")) {
                    case 200:
                        List<OperationLog> logs = (List<OperationLog>) message.getData().getSerializable("logs");
                        if (logs.size() < Constant.LOG_PAGE_SIZE) {
                            logActivity.mViewAdapter.hasMoreData = false;
                        } else {
                            logActivity.mViewAdapter.hasMoreData = true;
                        }
                        logActivity.mLogs.addAll(logs);
                        logActivity.mViewAdapter.notifyDataSetChanged();
                        break;
                    case 401:
                        LoginUtil.returnToLogin(mLogActivity.get().getApplicationContext());
                        break;
                    case 404:
                        Toast.makeText(logActivity, R.string.network_error, Toast.LENGTH_LONG).show();
                        break;
                    case 614:
                        Toast.makeText(logActivity, R.string.multi_login, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(logActivity.getApplicationContext(), LoginActivity.class);
                        logActivity.startActivity(intent);
                        break;
                    case -1:
                        Toast.makeText(logActivity, R.string.internal_error, Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
