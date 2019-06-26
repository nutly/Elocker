package com.feiyang.elocker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.feiyang.elocker.adpter.ELFragmentPagerAdapter;
import com.feiyang.elocker.fragment.AuthorityFragment;
import com.feiyang.elocker.fragment.LockerFragment;
import com.feiyang.elocker.fragment.SettingFragment;
import com.feiyang.elocker.fragment.UnlockFragment;
import com.feiyang.elocker.model.Locker;
import com.feiyang.elocker.util.BottomNavigationViewHelper;

import java.util.ArrayList;
import java.util.List;

import static com.feiyang.elocker.Constant.*;

public class MainActivity extends AppCompatActivity implements LockerFragment.OnLockerFragmentInteractionListener, AuthorityFragment.OnAuthorityFragmentInteractionListener, SettingFragment.OnSettingFragmentInteractionListener, UnlockFragment.OnUnlockFragmentInteractionListener {

    private BottomNavigationView mNavigation;
    private List<Fragment> mFragmentList;
    private ViewPager mViewPager;
    private AlertDialog lockerMenus; //锁菜单

    /*底部菜单*/
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_unlock:
                    mViewPager.setCurrentItem(UNLOCK_FRAGMENT, true);
                    Log.i("Unlock", "Visit Unlock Fragment");
                    return true;
                case R.id.navigation_lockers: {
                    mViewPager.setCurrentItem(LOCKER_FRAGMENT, true);
                    Log.i("Lockers", "Visit Lockers Fragment");
                    return true;
                }
                case R.id.navigation_authority:
                    mViewPager.setCurrentItem(AUTH_FRAGMENT, true);
                    Log.i("Authority", "Visit Authority Fragment");
                    return true;
                case R.id.navigation_setting:
                    mViewPager.setCurrentItem(SETTING_FRAGMENT, true);
                    Log.i("Setting", "Visit Setting Fragment");
                    return true;
            }
            return false;
        }
    };

    /*生成首页*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //加载主页视图
        setContentView(R.layout.activity_main);
        // 底部导航菜单
        mNavigation = (BottomNavigationView) findViewById(R.id.navigation);
        // 取消菜单切换动画
        BottomNavigationViewHelper.disableShiftMode(mNavigation);
        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //初始化fragment
        mFragmentList = new ArrayList<Fragment>();
        mFragmentList.add(UnlockFragment.newInstance());
        mFragmentList.add(LockerFragment.newInstance());
        mFragmentList.add(AuthorityFragment.newInstance(2));
        mFragmentList.add(SettingFragment.newInstance(3));

        //初始化viewpager
        mViewPager = (ViewPager) findViewById(R.id.fragment_container);
        ELFragmentPagerAdapter eLFragmentPagerAdapter = new ELFragmentPagerAdapter(this.getSupportFragmentManager(), this, mFragmentList);
        mViewPager.setAdapter(eLFragmentPagerAdapter);


    }

    @Override
    public void onLockerFragmentInteraction(Locker locker) {
        Bundle data = new Bundle();
        data.putSerializable("locker", locker);
        UnlockFragment unlockFg = (UnlockFragment) mFragmentList.get(UNLOCK_FRAGMENT);
        unlockFg.setArguments(data);
        mViewPager.setCurrentItem(UNLOCK_FRAGMENT, true);
        mNavigation.getMenu().getItem(UNLOCK_FRAGMENT).setChecked(true);
    }

    @Override
    public void onUnlockFragmentInteraction(Locker locker) {

        Log.i("onUnlockFragmentInter", locker.toString());
    }

    public void demo(View view) {
        Toast.makeText(MainActivity.this, "点击事件结果", Toast.LENGTH_SHORT).show();
    }


    /**
     * 钥匙列表菜单
     *
     * @param view
     */
    @SuppressLint("ResourceType")
    public void showList(View view) {
/*        final String[] items = {"查看", "编辑", "删除", "创建快捷方式","通过手机号分享","通过链接分享","转移锁"};
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("柜锁");
        alertBuilder.setMessage("这是什么东东，拿来看一下");
       *//* alertBuilder.setIcon(R.id.menu_icon);*//*
        alertBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    Intent intent = new Intent(MainActivity.this, QueryActivity.class);
                    startActivity(intent);
                }
                if(i==6){
                    Intent intent = new Intent(MainActivity.this, EfficientTimeActivity.class);
                    startActivity(intent);
                }

            *//*Toast.makeText(MainActivity.this, items[i], Toast.LENGTH_SHORT).show();
            alertDialog1.dismiss();*//*
            }
        });
        lockerMenus = alertBuilder.create();
        lockerMenus.show();*/

        //实例化布局
        View view1 = LayoutInflater.from(this).inflate(R.layout.locker_menus, null);
        //找到并对自定义布局中的控件进行操作的示例
        LinearLayout account = (LinearLayout) view1.findViewById(R.id.menus_item);
        /*account.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });*/
        //创建对话框
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle("柜锁");//设置标题
        dialog.setView(view1);//添加布局

        dialog.show();
    }

    /*选项菜单*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.elmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onAuthorityFragmentInteraction(Locker locker) {

    }

    @Override
    public void onSettingFragmentInteraction(Locker locker) {

    }

}
