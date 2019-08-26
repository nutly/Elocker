package com.feiyang.elocker.activity;

import android.content.Context;
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
import com.feiyang.elocker.adpter.AuthorizationRecyclerViewAdapter;
import com.feiyang.elocker.fragment.NavigationFragment;
import com.feiyang.elocker.model.Authorization;
import com.feiyang.elocker.rest.AuthorizationRest;
import com.feiyang.elocker.scanner.Scanner;
import com.feiyang.elocker.util.LoginUtil;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static com.feiyang.elocker.Constant.MESSAGE_AUTHORIZATION_LIST;

public class AuthorizationActivity extends AppCompatActivity {

    private Handler mHandler;
    private LinkedHashMap<String, List<Authorization>> mAuthorizationsMap = new LinkedHashMap<String, List<Authorization>>();
    private AuthorizationRecyclerViewAdapter mAdapter;
    private RecyclerView mRecycleView;
    private String mPhoneNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*判断当前是否已经登录*/
        LoginUtil.validLogin(this);

        setContentView(R.layout.activity_authorization);
        //设置底部导航
        NavigationFragment navigation = NavigationFragment.newInstance(this, R.id.navigation_in_authorization);

        /*记录当前用户*/
        SharedPreferences sp = this.getSharedPreferences(Constant.PROPERTY_FILE_NAME, MODE_PRIVATE);
        mPhoneNum = sp.getString("phoneNum", "");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.authorization);
        mHandler = new AuthorizationHandler(this);
        mRecycleView = findViewById(R.id.authorization_list);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));
        mAdapter = new AuthorizationRecyclerViewAdapter(mPhoneNum, mAuthorizationsMap);
        mRecycleView.setAdapter(mAdapter);
        mRecycleView.addItemDecoration(new DividerItemDecoration(this.getApplicationContext(), DividerItemDecoration.VERTICAL));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mHandler != null) {
            AuthorizationRest authorizationRest = new AuthorizationRest(this);
            authorizationRest.getAllAuthorization(mHandler);
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

    private static class AuthorizationHandler extends Handler {
        private final WeakReference<AuthorizationActivity> mAuthorizationActivity;

        public AuthorizationHandler(AuthorizationActivity authorizationActivity) {
            this.mAuthorizationActivity = new WeakReference<AuthorizationActivity>(authorizationActivity);
        }

        @Override
        public void handleMessage(Message message) {
            Bundle data = message.getData();
            AuthorizationActivity authorizationActivity = this.mAuthorizationActivity.get();
            Context context = authorizationActivity.getApplicationContext();
            if (message.what == MESSAGE_AUTHORIZATION_LIST) {
                switch (data.getInt("status")) {
                    case 200:
                        authorizationActivity.mAuthorizationsMap.clear();
                        authorizationActivity.mAuthorizationsMap.putAll(
                                (HashMap<String, List<Authorization>>) data.getSerializable("authorizationList"));
                        authorizationActivity.mAdapter.notifyDataSetChanged();
                        authorizationActivity.mAdapter.initSpreadState();
                        break;
                    case 401:
                        LoginUtil.returnToLogin(context);
                        break;
                    case 404:
                        Toast.makeText(context, R.string.network_error, Toast.LENGTH_LONG).show();
                        break;
                    case 614:
                        Toast.makeText(context, R.string.multi_login, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(context, LoginActivity.class);
                        authorizationActivity.startActivity(intent);
                        break;
                    case -1:
                        Toast.makeText(context, R.string.internal_error, Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
