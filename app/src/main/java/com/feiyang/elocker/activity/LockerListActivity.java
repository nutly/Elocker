package com.feiyang.elocker.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.feiyang.elocker.Constant;
import com.feiyang.elocker.R;
import com.feiyang.elocker.adpter.LockerRecyclerViewAdapter;
import com.feiyang.elocker.fragment.NavigationFragment;
import com.feiyang.elocker.model.Locker;
import com.feiyang.elocker.rest.LockerRest;
import com.feiyang.elocker.scanner.Scanner;
import com.feiyang.elocker.util.LoginUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.feiyang.elocker.Constant.MESSAGE_lOCKER_LIST;

public class LockerListActivity extends AppCompatActivity {

    private Handler mHandler;
    private List<Locker> mLockers = new ArrayList<Locker>();
    private LockerRecyclerViewAdapter mViewAdapter;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*判断当前是否已经登录*/
        LoginUtil.validLogin(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.lockers);

        setContentView(R.layout.activity_locker_list);
        mHandler = new LockerHandler(this);
        mRecyclerView = findViewById(R.id.locker_list);
        /*获取当前登录用户*/
        SharedPreferences sp = this.getSharedPreferences(Constant.PROPERTY_FILE_NAME, MODE_PRIVATE);
        String phoneNum = sp.getString("phoneNum", "");
        mViewAdapter = new LockerRecyclerViewAdapter(phoneNum, mLockers);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));
        mRecyclerView.setAdapter(mViewAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this.getApplicationContext(), DividerItemDecoration.VERTICAL));

        //设置底部导航
        NavigationFragment.newInstance(this, R.id.navigation_in_locker_list);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mHandler != null) {
            LockerRest lockerRest = new LockerRest(this);
            lockerRest.getAllLocker(mHandler);
        }
    }

    /*选项菜单*/
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

    private static class LockerHandler extends Handler {
        private final WeakReference<LockerListActivity> mLockerListActivity;

        public LockerHandler(LockerListActivity lockerListActivity) {
            this.mLockerListActivity = new WeakReference<LockerListActivity>(lockerListActivity);
        }

        @Override
        public void handleMessage(Message message) {
            LockerListActivity lockerListActivity = this.mLockerListActivity.get();
            if (message.what == MESSAGE_lOCKER_LIST) {
                Bundle data = message.getData();
                switch (data.getInt("status")) {
                    case 200:
                        lockerListActivity.mLockers.clear();
                        lockerListActivity.mLockers.addAll((List) data.getSerializable("lockerList"));
                        lockerListActivity.mViewAdapter.notifyDataSetChanged();
                        break;
                    case 401:
                        LoginUtil.returnToLogin(lockerListActivity.getApplicationContext());
                        break;
                    case 404:
                        Toast.makeText(lockerListActivity.getApplicationContext(), R.string.network_error, Toast.LENGTH_LONG).show();
                        break;
                    case 614:
                        Toast.makeText(lockerListActivity.getApplicationContext(), R.string.multi_login, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(lockerListActivity.getApplicationContext(), LoginActivity.class);
                        lockerListActivity.startActivity(intent);
                        break;
                    case -1:
                        Toast.makeText(lockerListActivity.getApplicationContext(), R.string.internal_error, Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
