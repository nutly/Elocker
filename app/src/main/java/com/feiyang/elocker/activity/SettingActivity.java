package com.feiyang.elocker.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.feiyang.elocker.Constant;
import com.feiyang.elocker.R;
import com.feiyang.elocker.fragment.*;
import com.feiyang.elocker.scanner.Scanner;
import com.feiyang.elocker.util.LoginUtil;

import java.util.HashMap;

public class SettingActivity extends AppCompatActivity implements FragmentSettingDashboard.OnSettingDashboadrListener {

    private FragmentManager mFragmentManager;
    private HashMap<String, Fragment> mFragments = new HashMap<String, Fragment>();
    private ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*判断当前是否已经登录*/
        LoginUtil.validLogin(this);
        setContentView(R.layout.activity_setting);
        mFragmentManager = getSupportFragmentManager();
        mActionBar = getSupportActionBar();
        mActionBar.setTitle(R.string.setting);
        /*初始化帧*/
        mFragments.put("dashboard", new FragmentSettingDashboard());
        mFragments.put("change_pass", new FragmentChangePass());
        mFragments.put("account", new FragmentAccount());
        mFragments.put("change_phonenum", new FragmentChangePhoneNum());
        mFragments.put("help", new FragmentHelp());
        mFragments.put("about", new FragmentAbout());
        mFragments.put("feedback", new FragmentFeedback());

        //设置底部导航
        NavigationFragment.newInstance(this, R.id.navigation_in_setting_activity);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.setting_container, mFragments.get("dashboard"));
        fragmentTransaction.commit();
    }

    @Override
    public void onMenuSelect(int menuViewId) {

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        switch (menuViewId) {
            case R.id.fragment_setting_account:
                transaction.replace(R.id.setting_container, mFragments.get("account"));
                transaction.commit();
                mActionBar.setTitle(R.string.cu_account);
                mActionBar.setDisplayHomeAsUpEnabled(true);
                break;
            case R.id.fragment_setting_change_pass:
                transaction.replace(R.id.setting_container, mFragments.get("change_pass"));
                transaction.commit();
                mActionBar.setTitle(R.string.change_passwd);
                mActionBar.setDisplayHomeAsUpEnabled(true);
                break;
            case R.id.fragment_setting_change_phonenum:
                transaction.replace(R.id.setting_container, mFragments.get("change_phonenum"));
                transaction.commit();
                mActionBar.setTitle(R.string.change_moblie);
                mActionBar.setDisplayHomeAsUpEnabled(true);
                break;
            case R.id.fragment_setting_update:
                Toast.makeText(this, R.string.update, Toast.LENGTH_LONG).show();
                break;
            case R.id.fragment_setting_feedback:
                transaction.replace(R.id.setting_container, mFragments.get("feedback"));
                transaction.commit();
                mActionBar.setTitle(R.string.feedback);
                mActionBar.setDisplayHomeAsUpEnabled(true);
                break;
            case R.id.fragment_setting_about:
                transaction.replace(R.id.setting_container, mFragments.get("about"));
                transaction.commit();
                mActionBar.setTitle(R.string.about);
                mActionBar.setDisplayHomeAsUpEnabled(true);
                break;
            case R.id.fragment_setting_help:
                transaction.replace(R.id.setting_container, mFragments.get("help"));
                transaction.commit();
                mActionBar.setTitle(R.string.help);
                mActionBar.setDisplayHomeAsUpEnabled(true);
                break;
            case R.id.login_out_btn:
                SharedPreferences sp = this.getSharedPreferences(Constant.PROPERTY_FILE_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.commit();

                /*跳转到登录页面*/
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home) {
            backToSettingDashboard();
        } else if (item.getItemId() == R.id.scanner) {
            Scanner.startScan(this);
        }
        return true;
    }

    /*选项菜单*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /*返回至设置页面*/
    public void backToSettingDashboard() {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.setting_container, mFragments.get("dashboard"));
        transaction.commit();
        mActionBar.setTitle(R.string.setting);
        mActionBar.setDisplayHomeAsUpEnabled(false);
    }
}
