package com.feiyang.elocker.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.feiyang.elocker.Constant;
import com.feiyang.elocker.R;
import com.feiyang.elocker.model.User;
import com.feiyang.elocker.rest.UserRest;

import java.lang.ref.WeakReference;

public class FragmentAccount extends Fragment {

    private TextView mPhoneNum, mLastLoginTime, mCreateTime, mIp, mEmail;
    private AccountHandler mHandler;

    public FragmentAccount() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        mPhoneNum = view.findViewById(R.id.fragment_account_phone);
        mLastLoginTime = view.findViewById(R.id.fragment_account_last_login);
        mCreateTime = view.findViewById(R.id.fragment_account_create_time);
        mIp = view.findViewById(R.id.fragment_account_ip);
        mEmail = view.findViewById(R.id.fragment_account_email);
        mHandler = new AccountHandler(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        UserRest userRest = new UserRest(this.getContext(), mHandler);
        userRest.getUser();
    }

    private static class AccountHandler extends Handler {
        private final WeakReference<FragmentAccount> mFragment;

        public AccountHandler(FragmentAccount fragmentAccount) {
            this.mFragment = new WeakReference<FragmentAccount>(fragmentAccount);
        }

        @Override
        public void handleMessage(Message message) {
            if (message.what == Constant.MESSAGE_ACCOUNT) {
                Bundle data = message.getData();
                FragmentAccount fragmentAccount = mFragment.get();
                int status = data.getInt("status");
                if (status == -1) {
                    Toast.makeText(fragmentAccount.getContext(), R.string.network_error, Toast.LENGTH_LONG).show();
                } else {
                    User user = (User) data.getSerializable("user");
                    fragmentAccount.mPhoneNum.setText(user.getPhoneNum());
                    fragmentAccount.mEmail.setText(user.getEmail());
                    fragmentAccount.mCreateTime.setText(user.getCreateTime());
                    fragmentAccount.mLastLoginTime.setText(user.getLastLoginTime());
                    fragmentAccount.mIp.setText(user.getLastLoginIp());
                }
            }
        }
    }
}
