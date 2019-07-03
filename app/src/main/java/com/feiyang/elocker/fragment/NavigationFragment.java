package com.feiyang.elocker.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.feiyang.elocker.R;
import com.feiyang.elocker.activity.AuthorizationActivity;
import com.feiyang.elocker.activity.LockerListActivity;
import com.feiyang.elocker.activity.SettingActivity;
import com.feiyang.elocker.activity.UnlockActivity;
import com.feiyang.elocker.util.BottomNavigationViewHelper;


public class NavigationFragment extends Fragment {
    private OnNavigationFragmentInteractionListener mListener;
    private BottomNavigationView mNavigation;
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
                case R.id.navigation_authority:
                    intent = new Intent(context, AuthorizationActivity.class);
                    break;
                case R.id.navigation_setting:
                    intent = new Intent(context, SettingActivity.class);
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
        transaction.add(naviResId, navigationFragment);
        transaction.commit();
       /* Bundle args = new Bundle();

        navigationFragment.setArguments(args);*/
        return navigationFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("NavigationFragment", "onCreatView in NavigationFragment");
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
        if (context instanceof OnNavigationFragmentInteractionListener) {
            mListener = (OnNavigationFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void setSelectItem() {
        FragmentActivity activity = this.getActivity();
        if (activity instanceof UnlockActivity) {
            mNavigation.setSelectedItemId(R.id.navigation_unlock);
        } else if (activity instanceof LockerListActivity) {
            mNavigation.setSelectedItemId(R.id.navigation_lockers);
        }
    }

    public interface OnNavigationFragmentInteractionListener {
        // TODO: Update argument type and name
        void onNavigationFragmentInteraction(Uri uri);
    }
}
