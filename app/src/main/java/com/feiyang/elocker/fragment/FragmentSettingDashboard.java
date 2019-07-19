package com.feiyang.elocker.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import com.feiyang.elocker.R;


public class FragmentSettingDashboard extends Fragment implements View.OnClickListener {

    private OnSettingDashboadrListener mListener;

    public FragmentSettingDashboard() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        RelativeLayout account = view.findViewById(R.id.fragment_setting_account);
        RelativeLayout change_passwd = view.findViewById(R.id.fragment_setting_change_pass);
        RelativeLayout setting = view.findViewById(R.id.fragment_setting_setting);
        RelativeLayout update = view.findViewById(R.id.fragment_setting_update);
        RelativeLayout feedback = view.findViewById(R.id.fragment_setting_feedback);
        RelativeLayout about = view.findViewById(R.id.fragment_setting_about);
        RelativeLayout help = view.findViewById(R.id.fragment_setting_help);
        Button loginout_btn = view.findViewById(R.id.login_out_btn);
        if (this.getContext() instanceof OnSettingDashboadrListener) {
            mListener = (OnSettingDashboadrListener) this.getContext();
        }
        account.setOnClickListener(this);
        change_passwd.setOnClickListener(this);
        setting.setOnClickListener(this);
        update.setOnClickListener(this);
        feedback.setOnClickListener(this);
        about.setOnClickListener(this);
        help.setOnClickListener(this);
        loginout_btn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            mListener.onMenuSelect(v.getId());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnSettingDashboadrListener {
        void onMenuSelect(int menuViewId);
    }
}
