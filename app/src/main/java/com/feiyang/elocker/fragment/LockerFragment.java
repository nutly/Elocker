package com.feiyang.elocker.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.feiyang.elocker.R;
import com.feiyang.elocker.adpter.LockerRecyclerViewAdapter;
import com.feiyang.elocker.data.LockerData;
import com.feiyang.elocker.model.Locker;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.feiyang.elocker.Constant.MESSAGE_lOCKER_LIST;


/**
 * 钥匙
 */
public class LockerFragment extends Fragment {

    private OnLockerFragmentInteractionListener mListener;
    private boolean mIsViewCreated; // 界面是否已创建完成
    private LockerHandler mHandler;
    private RecyclerView mRecyclerView;
    private LockerRecyclerViewAdapter mViewAdapter;
    private List<Locker> mLockers = new ArrayList<Locker>();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LockerFragment() {
    }


    public static LockerFragment newInstance() {
        LockerFragment fragment = new LockerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /*更新、设置当前可见状态，用于判断是否从后台获取数据*/
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && mIsViewCreated) {
            LockerData lockerData;
            if (mHandler != null) {
                lockerData = new LockerData(mHandler);
                lockerData.getAllLocker();
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new LockerHandler(this);
        mViewAdapter = new LockerRecyclerViewAdapter(mLockers, mListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRecyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_lockers, container, false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        mRecyclerView.setAdapter(mViewAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this.getContext(), DividerItemDecoration.VERTICAL));
        this.mIsViewCreated = true;
        Log.i("LockerFragment", "on LockerFragment CreatView, lockers size : " + mLockers.size());
        return mRecyclerView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLockerFragmentInteractionListener) {
            mListener = (OnLockerFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLockerFragmentInteractionListener");
        }
        Log.e("LockerFragment", "on LockerFragment Attach");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("LockerFragment", "on LockerFragment onResume");
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        Log.i("LockerFragment", "on LockerFragment Detach");
    }

    /*用于与Fragment交互*/
    public interface OnLockerFragmentInteractionListener {
        // TODO: Update argument type and name
        void onLockerFragmentInteraction(Locker locker);
    }

    private static class LockerHandler extends Handler {
        private final WeakReference<LockerFragment> mFragment;

        public LockerHandler(LockerFragment lockerFragment) {
            this.mFragment = new WeakReference<LockerFragment>(lockerFragment);
        }

        @Override
        public void handleMessage(Message message) {
            LockerFragment lockerFragment = this.mFragment.get();
            if (message.what == MESSAGE_lOCKER_LIST) {
                Bundle data = message.getData();
                if (data.containsKey("error")) {
                    Toast.makeText(lockerFragment.getContext(), R.string.network_error, Toast.LENGTH_LONG).show();
                } else {
                    lockerFragment.mLockers.clear();
                    lockerFragment.mLockers.addAll((List) data.getSerializable("lockerList"));
                    lockerFragment.mViewAdapter.notifyDataSetChanged();
                }
            }
        }
    }
}
