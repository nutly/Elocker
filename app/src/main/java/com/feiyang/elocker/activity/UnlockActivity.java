package com.feiyang.elocker.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.feiyang.elocker.R;
import com.feiyang.elocker.fragment.NavigationFragment;
import com.feiyang.elocker.model.Locker;
import com.feiyang.elocker.model.OperationLog;
import com.feiyang.elocker.rest.OperationLogRest;

public class UnlockActivity extends AppCompatActivity implements NavigationFragment.OnNavigationFragmentInteractionListener {

    private TextView mLockerDescriptionTv;
    private TextView mLastOpenDateTv;
    private TextView mToggleTimesTv;
    private Locker mLocker;

    /*生成首页*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
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

        //设置底部导航
        NavigationFragment navigation = NavigationFragment.newInstance(this, R.id.navigation_in_unlock_activity);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = this.getIntent();
        Bundle data = intent.getExtras();
        if (data != null && data.containsKey("locker")) {
            mLocker = (Locker) data.getSerializable("locker");
            mLockerDescriptionTv.setText(mLocker.getDescription());
            mLastOpenDateTv.setText(mLocker.getLastOpenTime());
            mToggleTimesTv.setText(String.valueOf(mLocker.getToggleTimes()));
        }
    }

    private void openLocker() {
        if (mLocker != null) {
            Toast.makeText(this, "正在开锁……", Toast.LENGTH_SHORT).show();

            /*上传日志*/
            OperationLog operationLog = new OperationLog();
            operationLog.setSerial(mLocker.getSerial());
            operationLog.setPhoneNum(mLocker.getPhoneNum());
            operationLog.setOperation(OperationLog.Operation.Open);
            operationLog.setDescription("Open Locker");

            OperationLogRest operationLogRest = new OperationLogRest(operationLog);
            operationLogRest.addOperationLog();

            mLocker.setToggleTimes(mLocker.getToggleTimes() + 1);
            mLastOpenDateTv.setText(operationLog.getsTime());
            mToggleTimesTv.setText(String.valueOf(mLocker.getToggleTimes()));
        } else {
            Toast.makeText(this, "请至\"钥匙\"页面选择一把锁", Toast.LENGTH_SHORT).show();
        }
    }


    /*选项菜单*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //TODO 扫一扫识别二维码
        return true;
    }

    @Override
    public void onNavigationFragmentInteraction(Uri uri) {
        //Interaction with nvationgation fragment
    }
}
