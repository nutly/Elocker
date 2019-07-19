package com.feiyang.elocker.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.feiyang.elocker.Constant;
import com.feiyang.elocker.R;
import com.feiyang.elocker.activity.SettingActivity;
import com.feiyang.elocker.rest.UserRest;

import java.lang.ref.WeakReference;

import static com.feiyang.elocker.Constant.MESSAGE_CHANGE_PASS_STATUS;

public class FragmentChangePass extends Fragment implements View.OnClickListener {

    private EditText mOldPass, mNewPass, mNewpass1, mEmail;
    private ChangePassHandler mHandler;

    public FragmentChangePass() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_change_pass, container, false);
        mOldPass = view.findViewById(R.id.change_pass_old_pass);
        mNewPass = view.findViewById(R.id.change_pass_new_pass);
        mNewpass1 = view.findViewById(R.id.change_pass_new_pass1);
        mHandler = new ChangePassHandler(this);
        Button submit_btn = (Button) view.findViewById(R.id.change_pass_btn);

        submit_btn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change_pass_btn:
                Context context = v.getContext();
                String old_pass = mOldPass.getText().toString().trim();
                String new_pass = mNewPass.getText().toString().trim();
                String new_pass1 = mNewpass1.getText().toString().trim();
                if (!new_pass.equals(new_pass1)) {
                    Toast.makeText(context, R.string.password_mismatch, Toast.LENGTH_LONG).show();
                    return;
                }
                UserRest userRest = new UserRest(context, mHandler);
                userRest.changePassword(old_pass, new_pass);
                break;
            default:
                break;
        }
    }

    private static class ChangePassHandler extends Handler {

        private final WeakReference<FragmentChangePass> mFragment;

        public ChangePassHandler(FragmentChangePass fragment) {
            this.mFragment = new WeakReference<FragmentChangePass>(fragment);
        }

        @Override
        public void handleMessage(Message message) {
            if (message.what == MESSAGE_CHANGE_PASS_STATUS) {
                Bundle data = message.getData();
                Context context = mFragment.get().getContext();
                switch (data.getInt("status")) {
                    case 200:
                        Toast.makeText(context, R.string.change_passwd_success, Toast.LENGTH_SHORT).show();
                        SettingActivity activity = (SettingActivity) mFragment.get().getActivity();

                        /*修改本地缓存密码*/
                        SharedPreferences sp = context.getSharedPreferences(Constant.PROPERTY_FILE_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("password", data.getString("password"));
                        editor.commit();
                        activity.backToSettingDashboard();
                        mFragment.get().onDestroy();
                        break;
                    case 601:
                        Toast.makeText(context, R.string.incorrect_login_info, Toast.LENGTH_LONG).show();
                        break;
                    case 500:
                        Toast.makeText(context, R.string.internal_error, Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
