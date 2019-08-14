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
import android.widget.TextView;
import android.widget.Toast;
import com.feiyang.elocker.Constant;
import com.feiyang.elocker.R;
import com.feiyang.elocker.rest.UserRest;
import com.feiyang.elocker.util.LoginUtil;
import com.feiyang.elocker.util.TimerUtil;

import java.lang.ref.WeakReference;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentChangePhoneNum extends Fragment implements View.OnClickListener
        , View.OnFocusChangeListener {

    private EditText mNewPhoneNum, mPassword, mCode;
    private TextView mPhoneNum;
    private Button mGetCodeBtn;
    private ChangeMobileHandler mHandler;
    private TimerUtil mTimerUtil;

    public FragmentChangePhoneNum() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        /*判断当前是否已经登录*/
        LoginUtil.validLogin(this.getContext());

        View view = inflater.inflate(R.layout.fragment_change_phonenum, container, false);
        mPhoneNum = view.findViewById(R.id.change_phonenum_mobile);
        mPassword = view.findViewById(R.id.change_phonenum_password);
        mNewPhoneNum = view.findViewById(R.id.change_phonenum_new_mobile);
        mCode = view.findViewById(R.id.change_phonenum_code);
        mGetCodeBtn = view.findViewById(R.id.change_phonenum_get_code);
        Button submitBtn = view.findViewById(R.id.change_phonenum_submit_btn);
        mNewPhoneNum.setFocusable(true);

        mGetCodeBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
        mHandler = new ChangeMobileHandler(this);
        SharedPreferences sp = getContext().getSharedPreferences(Constant.PROPERTY_FILE_NAME, Context.MODE_PRIVATE);
        mPhoneNum.setText(sp.getString("phoneNum", ""));
        return view;
    }

    @Override
    public void onClick(View v) {
        UserRest userRest = new UserRest(v.getContext(), mHandler);
        switch (v.getId()) {
            case R.id.change_phonenum_get_code:
                String phoneNum = mPhoneNum.getText().toString();
                if (phoneNum == null || phoneNum.equals("")
                        || phoneNum.length() < 6 || this.mHandler == null) {
                    mPhoneNum.setBackground(this.getActivity().getDrawable(R.drawable.inputbox_red));
                    Toast.makeText(v.getContext(), R.string.phone_num_format_error, Toast.LENGTH_SHORT).show();
                    return;
                }
                userRest.getCode(phoneNum);
                mGetCodeBtn.setClickable(false);
                mGetCodeBtn.setBackgroundColor(this.getActivity().getColor(R.color.colorGray));
                mTimerUtil = new TimerUtil(mGetCodeBtn, 120, 1);
                mTimerUtil.start();
                break;
            case R.id.change_phonenum_submit_btn:
                break;
            default:
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus)
            return;
        EditText editText = this.getActivity().findViewById(v.getId());
        String value = editText.getText().toString() != null ?
                editText.getText().toString() : "";
        Context context = v.getContext();
        switch (v.getId()) {
            case R.id.change_phonenum_password:
                if (value.trim().length() <= Constant.MIN_PASSWORD_LENGTH) {
                    editText.setBackground(this.getActivity().getDrawable(R.drawable.inputbox_red));
                    Toast.makeText(context, R.string.password_format_error, Toast.LENGTH_SHORT).show();
                } else
                    editText.setBackground(this.getActivity().getDrawable(R.drawable.inputbox_white));
                break;
            case R.id.activity_register_code:
                if (value.length() != Constant.VERIFICATION_CODE_LENGTH) {
                    Toast.makeText(context, R.string.code_length_error, Toast.LENGTH_SHORT).show();
                    editText.setBackground(this.getActivity().getDrawable(R.drawable.inputbox_red));
                } else
                    editText.setBackground(this.getActivity().getDrawable(R.drawable.inputbox_white));
                break;
            default:
                break;
        }
    }

    private boolean isInputValid() {
        if (mPassword.getText().toString() == null ||
                mPassword.getText().toString().length() <= Constant.MIN_PASSWORD_LENGTH) {
            mPassword.setBackground(this.getActivity().getDrawable(R.drawable.inputbox_red));
            Toast.makeText(mPassword.getContext(), R.string.password_format_error, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mCode.getText().toString().length() != Constant.VERIFICATION_CODE_LENGTH) {
            Toast.makeText(mCode.getContext(), R.string.code_length_error, Toast.LENGTH_SHORT).show();
            mCode.setBackground(this.getActivity().getDrawable(R.drawable.inputbox_red));
            return false;
        }
        try {
            Integer.parseInt(mCode.getText().toString());
        } catch (Exception e) {
            Toast.makeText(mCode.getContext(), R.string.code_format_error, Toast.LENGTH_SHORT).show();
            mCode.setBackground(this.getActivity().getDrawable(R.drawable.inputbox_red));
            return false;
        }
        return true;
    }

    private static class ChangeMobileHandler extends Handler {

        private final WeakReference<FragmentChangePhoneNum> mFragment;

        public ChangeMobileHandler(FragmentChangePhoneNum fragment) {
            this.mFragment = new WeakReference<FragmentChangePhoneNum>(fragment);
        }

        @Override
        public void handleMessage(Message message) {
            Context context = mFragment.get().getContext();
            if (message.what == Constant.MESSAGE_GET_RESET_PASS_CODE_STATUS) {
                switch (message.getData().getInt("status")) {
                    case 200:
                        Toast.makeText(context, R.string.get_register_code_success, Toast.LENGTH_LONG).show();
                        break;
                    case 610:
                        Toast.makeText(context, R.string.get_verification_code_failed, Toast.LENGTH_LONG).show();
                        break;
                    case -1:
                        Toast.makeText(context, R.string.network_error, Toast.LENGTH_LONG).show();
                        mFragment.get().mTimerUtil.cancel();
                        mFragment.get().mTimerUtil.onFinish();
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
