package com.feiyang.elocker.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.feiyang.elocker.R;
import com.feiyang.elocker.fragment.FragmentChangePass;
import com.feiyang.elocker.fragment.FragmentSettingDashboard;
import com.feiyang.elocker.fragment.NavigationFragment;
import com.feiyang.elocker.util.LoginUtil;

public class SettingActivity extends AppCompatActivity implements FragmentSettingDashboard.OnSettingDashboadrListener {

    private FragmentManager mFragmentManager;
    private ActionBar mActionBar;
    private FragmentSettingDashboard mSettingDashboardFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*判断当前是否已经登录*/
        LoginUtil.validLogin(this);
        setContentView(R.layout.activity_setting);
        mFragmentManager = getSupportFragmentManager();
        /*设置返回按钮*/
        mActionBar = getSupportActionBar();

        //设置底部导航
        NavigationFragment navigation = NavigationFragment.newInstance(this, R.id.navigation_in_setting_activity);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        if (mSettingDashboardFragment == null) {
            mSettingDashboardFragment = new FragmentSettingDashboard();
        }
        fragmentTransaction.replace(R.id.setting_container, mSettingDashboardFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onMenuSelect(int menuViewId) {

 /*       if (mActionBar != null) {
            mActionBar.setHomeButtonEnabled(true);
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }*/

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        Fragment fragment;
        switch (menuViewId) {
            case R.id.fragment_setting_change_pass:
                fragment = new FragmentChangePass();
                transaction.replace(R.id.setting_container, fragment);
                transaction.commit();
                mActionBar.hide();
                break;
            case R.id.fragment_setting_setting:
                Toast.makeText(this, R.string.setting, Toast.LENGTH_LONG).show();
                break;
            case R.id.fragment_setting_update:
                Toast.makeText(this, R.string.update, Toast.LENGTH_LONG).show();
                break;
            case R.id.fragment_setting_feedback:
                Toast.makeText(this, R.string.feedback, Toast.LENGTH_LONG).show();
                break;
            case R.id.fragment_setting_about:
                Toast.makeText(this, R.string.about, Toast.LENGTH_LONG).show();
                break;
            case R.id.fragment_setting_help:
                Toast.makeText(this, R.string.help, Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }

    /*响应返回键*/
/*    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                FragmentTransaction transaction = mFragmentManager.beginTransaction();
                Fragment fragment = new FragmentSettingDashboard();
                transaction.replace(R.id.setting_fragment_container, fragment);
                transaction.commit();
                break;
            default:
                break;
        }
        *//*点击事件不再传递*//*
        return true;
    }*/

    /*返回至设置页面*/
    public void backToSettingDashboard() {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if (mSettingDashboardFragment == null) {
            mSettingDashboardFragment = new FragmentSettingDashboard();
        }
        transaction.replace(R.id.setting_container, mSettingDashboardFragment);
        transaction.commit();
    }
}
