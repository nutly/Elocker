package com.feiyang.elocker.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import com.feiyang.elocker.R;
import com.feiyang.elocker.fragment.NavigationFragment;
import com.feiyang.elocker.util.LoginUtil;

public class LogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        /*判断当前是否已经登录*/
        LoginUtil.validLogin(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.log);
        //设置底部导航
        NavigationFragment.newInstance(this, R.id.navigation_in_log_list);
    }
}
