package com.feiyang.elocker.activity;

import android.net.Uri;
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
import com.feiyang.elocker.data.LockerData;
import com.feiyang.elocker.fragment.NavigationFragment;
import com.feiyang.elocker.model.Locker;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.feiyang.elocker.Constant.MESSAGE_lOCKER_LIST;

public class LockerListActivity extends AppCompatActivity implements NavigationFragment.OnNavigationFragmentInteractionListener {

    private Handler mHandler;
    private List<Locker> mLockers = new ArrayList<Locker>();
    private LockerRecyclerViewAdapter mViewAdapter;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
            LockerData lockerData = new LockerData(mHandler);
            lockerData.getAllLocker();
        }
    }

    @Override
    public void onNavigationFragmentInteraction(Uri uri) {

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
