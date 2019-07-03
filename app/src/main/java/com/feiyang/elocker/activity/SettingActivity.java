package com.feiyang.elocker.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.feiyang.elocker.R;
import com.feiyang.elocker.fragment.NavigationFragment;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //设置底部导航
        NavigationFragment navigation = NavigationFragment.newInstance(this, R.id.navigation_in_locker_list);
    }
}
