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
import com.feiyang.elocker.adpter.AuthorizationRecyclerViewAdapter;
import com.feiyang.elocker.fragment.NavigationFragment;
import com.feiyang.elocker.model.Authorization;
import com.feiyang.elocker.rest.AuthorizationRest;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static com.feiyang.elocker.Constant.MESSAGE_AUTHORIZATION_LIST;

public class AuthorizationActivity extends AppCompatActivity implements NavigationFragment.OnNavigationFragmentInteractionListener {

    private Handler mHandler;
    private LinkedHashMap<String, List<Authorization>> mAuthorizationsMap = new LinkedHashMap<String, List<Authorization>>();
    private AuthorizationRecyclerViewAdapter mAdapter;
    private RecyclerView mRecycleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);
        //设置底部导航
        NavigationFragment navigation = NavigationFragment.newInstance(this, R.id.navigation_in_authorization);

        mHandler = new AuthorizationHandler(this);
        mRecycleView = (RecyclerView) findViewById(R.id.authorization_list);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));
        mAdapter = new AuthorizationRecyclerViewAdapter(mAuthorizationsMap);
        mRecycleView.setAdapter(mAdapter);
        mRecycleView.addItemDecoration(new DividerItemDecoration(this.getApplicationContext(), DividerItemDecoration.VERTICAL));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mHandler != null) {
            AuthorizationRest authorizationRest = new AuthorizationRest();
            authorizationRest.getAllAuthorization(mHandler);
        }
    }

    @Override
    public void onNavigationFragmentInteraction(Uri uri) {
        //Interaction with nvationgation fragment
    }

    private static class AuthorizationHandler extends Handler {
        private final WeakReference<AuthorizationActivity> mAuthorizationActivity;

        public AuthorizationHandler(AuthorizationActivity authorizationActivity) {
            this.mAuthorizationActivity = new WeakReference<AuthorizationActivity>(authorizationActivity);
        }

        @Override
        public void handleMessage(Message message) {
            AuthorizationActivity authorizationActivity = this.mAuthorizationActivity.get();
            if (message.what == MESSAGE_AUTHORIZATION_LIST) {
                Bundle data = message.getData();
                if (data.containsKey("error")) {
                    Toast.makeText(authorizationActivity.getApplicationContext(), R.string.network_error, Toast.LENGTH_LONG).show();
                } else {
                    authorizationActivity.mAuthorizationsMap.clear();
                    authorizationActivity.mAuthorizationsMap.putAll(
                            (HashMap<String, List<Authorization>>) data.getSerializable("authorizationList"));
                    authorizationActivity.mAdapter.notifyDataSetChanged();
                    authorizationActivity.mAdapter.initSpreadState();
                }
            }
        }
    }
}
