package com.feiyang.elocker.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.feiyang.elocker.R;
import com.feiyang.elocker.data.OperationLogData;
import com.feiyang.elocker.model.Locker;
import com.feiyang.elocker.model.OperationLog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static com.feiyang.elocker.Constant.DATE_PATTERN;

public class UnlockFragment extends Fragment {

    private OnUnlockFragmentInteractionListener mListener;
    private TextView mLockerDescriptionTv;
    private TextView mLastOpenDateTv;
    private TextView mToggleTimesTv;
    private boolean mIsViewCreated;
    private Locker mLocker;

    public UnlockFragment() {
        // Required empty public constructor
    }

    public static UnlockFragment newInstance() {
        UnlockFragment fragment = new UnlockFragment();
        return fragment;
    }

    /**
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && mIsViewCreated) {
            updateView();
        }
    }

    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_unlock, container, false);
        ImageButton lockerToggleBtn = (ImageButton) view.findViewById(R.id.locker_toggle_btn);
        mLockerDescriptionTv = view.findViewById(R.id.locker_description_unlock);
        mLastOpenDateTv = view.findViewById(R.id.last_unlock_date);
        mToggleTimesTv = view.findViewById(R.id.toggle_times);

        lockerToggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLocker(v);
            }
        });
        mIsViewCreated = true;
        updateView();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnUnlockFragmentInteractionListener) {
            mListener = (OnUnlockFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        Log.i("##########", "Unlocker Fragment onAttach");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("UnlockerFragment", "Unlocker Fragment onResume");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mIsViewCreated = false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        Log.i("UnlockerFragment", "Unlocker Fragment onDetach");
    }

    private void updateView() {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("locker")) {
            mLocker = (Locker) bundle.getSerializable("locker");
            if (mLocker != null) {
                mLockerDescriptionTv.setText(mLocker.getDescription());
                mLastOpenDateTv.setText(mLocker.getLastOpenTime());
                mToggleTimesTv.setText(String.valueOf(mLocker.getToggleTimes()));
                Log.i("UnlockFragment", "Update unlockfragment ui");
            }
        }
    }

    private void openLocker(View v) {
        if (mLocker != null) {
            Toast.makeText(v.getContext(), "正在开锁……", Toast.LENGTH_SHORT).show();

            SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
            sdf.setTimeZone(TimeZone.getDefault());
            String sTime = sdf.format(new Date());
            mLocker.setLastOpenTime(sTime);
            mLocker.setToggleTimes(mLocker.getToggleTimes() + 1);
            mLastOpenDateTv.setText(sTime);
            mToggleTimesTv.setText(String.valueOf(mLocker.getToggleTimes()));
            /*上传日志*/
            OperationLog operationLog = new OperationLog();
            operationLog.setSerial(mLocker.getSerial());
            operationLog.setPhoneNum(mLocker.getPhoneNum());
            operationLog.setOperation(OperationLog.Operation.Open);
            operationLog.setsTime(sTime);
            operationLog.setDescription("Open Locker");

            OperationLogData operationLogData = new OperationLogData(operationLog);
            operationLogData.addOperationLog();
        } else {
            Toast.makeText(v.getContext(), "请至\"钥匙\"页面选择一把锁", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnUnlockFragmentInteractionListener {
        // TODO: Update argument type and name
        void onUnlockFragmentInteraction(Locker locker);
    }
}
