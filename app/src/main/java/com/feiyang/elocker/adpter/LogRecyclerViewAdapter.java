package com.feiyang.elocker.adpter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.feiyang.elocker.R;
import com.feiyang.elocker.model.OperationLog;

import java.util.List;

public class LogRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<OperationLog> mLogs;
    private static final short ITEM_VIEW = 0;
    private static final short FOOTER_VIEW = 1;
    public boolean hasMoreData;

    public LogRecyclerViewAdapter(List<OperationLog> logs) {
        mLogs = logs;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int i = getItemCount();
        if (viewType == ITEM_VIEW) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.log_list_item, parent, false);
            return new LogRecyclerViewAdapter.LogViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.log_list_footer, parent, false);
            return new LogRecyclerViewAdapter.FootViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LogViewHolder) {
            LogViewHolder logViewHolder = (LogViewHolder) holder;
            logViewHolder.mDescription.setText(mLogs.get(position).getDescription());
            logViewHolder.mTime.setText(mLogs.get(position).getsTime());
            logViewHolder.mPhoneNum.setText(mLogs.get(position).getPhoneNum());
            logViewHolder.mOperation.setText(mLogs.get(position).getOperation().toString());
            logViewHolder.mSerial.setText(mLogs.get(position).getSerial());
        } else {
            FootViewHolder footViewHolder = (FootViewHolder) holder;
            if (hasMoreData) {
                footViewHolder.mLoadMore.setVisibility(View.VISIBLE);
                footViewHolder.mNoData.setVisibility(View.GONE);
            } else {
                footViewHolder.mLoadMore.setVisibility(View.GONE);
                footViewHolder.mNoData.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mLogs.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return FOOTER_VIEW;
        } else {
            return ITEM_VIEW;
        }
    }

    private class LogViewHolder extends RecyclerView.ViewHolder {
        public TextView mSerial, mPhoneNum, mOperation, mTime, mDescription;

        public LogViewHolder(View itemView) {
            super(itemView);
            mSerial = itemView.findViewById(R.id.log_item_serial);
            mPhoneNum = itemView.findViewById(R.id.log_item_phone_num);
            mOperation = itemView.findViewById(R.id.log_item_operation);
            mTime = itemView.findViewById(R.id.log_item_time);
            mDescription = itemView.findViewById(R.id.log_item_description);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mDescription.getText().toString() + "'";
        }
    }

    private class FootViewHolder extends RecyclerView.ViewHolder {
        private TextView mNoData;
        private TextView mLoadMore;

        public FootViewHolder(View footerView) {
            super(footerView);
            mLoadMore = footerView.findViewById(R.id.log_search_footer_load_more);
            mNoData = footerView.findViewById(R.id.log_search_footer_no_data);
        }
    }
}
