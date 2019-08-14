package com.feiyang.elocker.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.feiyang.elocker.R;
import com.feiyang.elocker.activity.*;
import com.feiyang.elocker.util.BottomNavigationViewHelper;


public class NavigationFragment extends Fragment {
    private BottomNavigationView mNavigation;
    private ActionBar mActionBar;
    /*底部菜单*/
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Context context = NavigationFragment.this.getContext();
            Intent intent;
            switch (item.getItemId()) {
                case R.id.navigation_unlock:
                    intent = new Intent(context, UnlockActivity.class);
                    break;
                case R.id.navigation_lockers:
                    intent = new Intent(context, LockerListActivity.class);
                    break;
                case R.id.navigation_authorization:
                    intent = new Intent(context, AuthorizationActivity.class);
                    break;
                case R.id.navigation_setting:
                    intent = new Intent(context, SettingActivity.class);
                    break;
                case R.id.navigation_log:
                    intent = new Intent(context, LogActivity.class);
                    break;
                default:
                    return false;
            }
            startActivity(intent);
            NavigationFragment.this.getActivity().overridePendingTransition(0, 0);
            return true;
        }
    };

    public NavigationFragment() {
        // Required empty public constructor
    }

    /*
     * @param activity
     * @param naviResId  Activity xml中的 navigation resource id
     * @return com.feiyang.elocker.fragment.NavigationFragment
     */
    public static NavigationFragment newInstance(AppCompatActivity activity, int naviResId) {
        NavigationFragment navigationFragment = new NavigationFragment();
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(naviResId, navigationFragment);
        transaction.commit();
        return navigationFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation, container, false);
        mNavigation = (BottomNavigationView) view.findViewById(R.id.navigation);
        // 取消菜单切换动画
        BottomNavigationViewHelper.disableShiftMode(mNavigation);
        //设置选中
        setSelectItem();
        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /*切换选项卡时切换选中状态*/
    private void setSelectItem() {
        FragmentActivity activity = this.getActivity();
        if (activity instanceof UnlockActivity) {
            mNavigation.setSelectedItemId(R.id.navigation_unlock);
        } else if (activity instanceof LockerListActivity) {
            mNavigation.setSelectedItemId(R.id.navigation_lockers);
        } else if (activity instanceof AuthorizationActivity) {
            mNavigation.setSelectedItemId(R.id.navigation_authorization);
        } else if (activity instanceof SettingActivity) {
            mNavigation.setSelectedItemId(R.id.navigation_setting);
        } else if (activity instanceof LogActivity) {
            mNavigation.setSelectedItemId(R.id.navigation_log);
        }
    }
}
