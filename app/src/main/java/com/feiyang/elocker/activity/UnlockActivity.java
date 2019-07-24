package com.feiyang.elocker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.feiyang.elocker.R;
import com.feiyang.elocker.fragment.NavigationFragment;
import com.feiyang.elocker.model.Locker;
import com.feiyang.elocker.model.OperationLog;
import com.feiyang.elocker.rest.OperationLogRest;
import com.feiyang.elocker.scanner.Scanner;
import com.feiyang.elocker.util.LoginUtil;

public class UnlockActivity extends AppCompatActivity {

    private TextView mLockerDescriptionTv;
    private TextView mLastOpenDateTv;
    private TextView mToggleTimesTv;
    private static Locker mLocker;

    /*生成首页*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        /*判断当前是否已经登录*/
        LoginUtil.validLogin(this);
        setContentView(R.layout.activity_unlock);
        ImageButton lockerToggleBtn = (ImageButton) findViewById(R.id.locker_toggle_btn);
        mLockerDescriptionTv = findViewById(R.id.locker_description_unlock);
        mLastOpenDateTv = findViewById(R.id.last_unlock_date);
        mToggleTimesTv = findViewById(R.id.toggle_times);

        lockerToggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLocker();
            }
        });

        //设置顶部ActionBar系统栏和底部导航栏
        NavigationFragment.newInstance(this, R.id.navigation_in_unlock_activity);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.unlock);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = this.getIntent();
        Bundle data = intent.getExtras();
        if (data != null && data.containsKey("locker")) {
            mLocker = (Locker) data.getSerializable("locker");
        }
        if (mLocker != null) {
            mLockerDescriptionTv.setText(mLocker.getDescription());
            mLastOpenDateTv.setText(mLocker.getLastOpenTime());
            mToggleTimesTv.setText(String.valueOf(mLocker.getToggleTimes()));
        }
    }

    /*创建选项菜单*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /*响应选项菜单*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.scanner) {
            Scanner.startScan(this);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Scanner.handleResult(this, requestCode, resultCode, data);
    }

    private void openLocker() {
        if (mLocker != null) {
            Toast.makeText(this, R.string.open_locker_now, Toast.LENGTH_SHORT).show();

            /*上传日志*/
            OperationLog operationLog = new OperationLog();
            operationLog.setSerial(mLocker.getSerial());
            operationLog.setPhoneNum(mLocker.getPhoneNum());
            operationLog.setOperation(OperationLog.Operation.Open);
            operationLog.setDescription("Open Locker");

            OperationLogRest operationLogRest = new OperationLogRest(this, operationLog);
            operationLogRest.addOperationLog();

            mLocker.setToggleTimes(mLocker.getToggleTimes() + 1);
            mLastOpenDateTv.setText(operationLog.getsTime());
            mToggleTimesTv.setText(String.valueOf(mLocker.getToggleTimes()));
        } else {
            Toast.makeText(this, R.string.selec_locker, Toast.LENGTH_SHORT).show();
        }
    }
}
