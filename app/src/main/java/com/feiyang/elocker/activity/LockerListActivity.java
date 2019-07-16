package com.feiyang.elocker.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;
import com.feiyang.elocker.R;
import com.feiyang.elocker.adpter.LockerRecyclerViewAdapter;
import com.feiyang.elocker.fragment.NavigationFragment;
import com.feiyang.elocker.model.Locker;
import com.feiyang.elocker.rest.LockerRest;
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

        setContentView(R.layout.activity_locker_list);
        mHandler = new LockerHandler(this);
        mRecyclerView = findViewById(R.id.locker_list);
        mViewAdapter = new LockerRecyclerViewAdapter(mLockers);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));
        mRecyclerView.setAdapter(mViewAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this.getApplicationContext(), DividerItemDecoration.VERTICAL));

        //设置底部导航
        NavigationFragment navigation = NavigationFragment.newInstance(this, R.id.navigation_in_locker_list);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mHandler != null) {
            LockerRest lockerRest = new LockerRest();
            lockerRest.getAllLocker(mHandler);
        }
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
                if (data.containsKey("error")) {
                    Toast.makeText(lockerListActivity.getApplicationContext(), R.string.network_error, Toast.LENGTH_LONG).show();
                } else {
                    lockerListActivity.mLockers.clear();
                    lockerListActivity.mLockers.addAll((List) data.getSerializable("lockerList"));
                    lockerListActivity.mViewAdapter.notifyDataSetChanged();
                }
            }
        }
    }
}
