package com.feiyang.elocker.adpter;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.feiyang.elocker.R;
import com.feiyang.elocker.model.Authorization;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;


public class AuthorizationRecyclerViewAdapter extends RecyclerView.Adapter<AuthorizationRecyclerViewAdapter.RecycleViewHolder> {

    private final static int ITEM_TYPE = 0;
    private final static int HEADER_TYPE = 1;
    private LinkedHashMap<String, List<Authorization>> mAuthorizationsMap;
    /*记录列表是否展开,展开为1，不展开为0, key 为lockerName*/
    private HashMap<String, Boolean> mSpreadMap;
    private LinkedHashMap<Integer, String> mHeaderIndex;

    public AuthorizationRecyclerViewAdapter(LinkedHashMap<String, List<Authorization>> authorizationsMap) {
        mAuthorizationsMap = authorizationsMap;
        mHeaderIndex = new LinkedHashMap<Integer, String>();
        mSpreadMap = new HashMap<String, Boolean>();
    }

    public void initSpreadState() {
        this.mSpreadMap.clear();
    }

    @Override
    public RecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.authorization_item, parent, false);
            return new ItemViewHolder(itemView);
        } else {
            View headerView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.authorization_item_header, parent, false);
            return new HeaderViewHolder(headerView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            /*获取Header所在的position*/
            int headerIndex = this.getHeaderPosition(position);
            String lockerName = mHeaderIndex.get(headerIndex);
            List<Authorization> authorizations = mAuthorizationsMap.get(lockerName);
            int authorizationIndex = position - headerIndex - 1;
            Authorization authorization = authorizations.get(authorizationIndex);

            itemViewHolder.mToAccount.setText(authorization.getToAccount());
            itemViewHolder.mDescription.setText(authorization.getDescription());
            itemViewHolder.mDetail.setOnClickListener(new OnDetailClickLisener(authorization));

        } else {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.mLockerName.setText(mHeaderIndex.get(position));
            headerViewHolder.mView.setTag(mHeaderIndex.get(position));
        }
    }

    /*获取所在的组名（lockerName）对应的position*/
    private int getHeaderPosition(int position) {
        int lastIndex = 0;
        for (int index : mHeaderIndex.keySet()) {
            if (index > position) {
                return lastIndex;
            }
            lastIndex = index;
        }
        return lastIndex;
    }

    /*响应“详情”按钮的点击事件*/
    private class OnDetailClickLisener implements View.OnClickListener {

        private Authorization mAuthorization;

        public OnDetailClickLisener(Authorization authorization) {
            mAuthorization = authorization;
        }

        @Override
        public void onClick(View v) {
            Context context = v.getContext();
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            AlertDialog authorizationDetail = builder.setTitle(R.string.detail)
                    .setView(createAuthorizationDetailView(context, mAuthorization))
                    .create();
            authorizationDetail.show();
        }
    }

    @Override
    public int getItemViewType(int position) {
        /*mHeaderIndex中记录的均为 header的 position*/
        if (mHeaderIndex.containsKey(position)) {
            return HEADER_TYPE;
        } else {
            return ITEM_TYPE;
        }
    }

    @Override
    public int getItemCount() {
        mHeaderIndex.clear();
        int itemCount = 0;

        for (String key : mAuthorizationsMap.keySet()) {
            /*记录每个Header对应的position位置*/
            mHeaderIndex.put(itemCount, key);
            if (mSpreadMap.containsKey(key)) {
                /*条目是展开状态,计算每个展开的项目下有多少个子项目*/
                if (mSpreadMap.get(key)) {
                    itemCount = itemCount + mAuthorizationsMap.get(key).size();
                }
            } else {
                /*没有状态信息，默认设置为折叠状态*/
                mSpreadMap.put(key, false);
            }
            /*Header一定为展开状态，计数加一*/
            itemCount++;
        }
        return itemCount;
    }

    /*抽象类用于统一两种视图的ViewHolder*/
    public abstract class RecycleViewHolder extends RecyclerView.ViewHolder {

        public RecycleViewHolder(View view) {
            super(view);
        }
    }

    public class HeaderViewHolder extends RecycleViewHolder {
        public TextView mLockerName;
        public View mView;

        public HeaderViewHolder(View view) {
            super(view);
            mView = view;
            mLockerName = (TextView) view.findViewById(R.id.authorization_list_locker_name);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String headerName = (String) v.getTag();
                    if (headerName != null) {
                        if (mSpreadMap.containsKey(headerName)) {
                            boolean isSpread = mSpreadMap.get(headerName);
                            mSpreadMap.put(headerName, !isSpread);
                        } else {
                            mSpreadMap.put(headerName, false);
                        }
                        notifyDataSetChanged();
                    }
                }
            });
        }
    }

    public class ItemViewHolder extends RecycleViewHolder {
        public TextView mToAccount;
        public TextView mDescription;
        public ImageButton mDetail;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mToAccount = (TextView) itemView.findViewById(R.id.authorization_item_to_account);
            mDescription = (TextView) itemView.findViewById(R.id.authorization_item_description);
            mDetail = (ImageButton) itemView.findViewById(R.id.authorization_item_detail_btn);
        }
    }

    private View createAuthorizationDetailView(Context context, Authorization authorization) {
        View view = View.inflate(context, R.layout.authorization_detail, null);
        TextView lockerName = view.findViewById(R.id.authorization_detail_locker_name);
        TextView fromAccount = view.findViewById(R.id.authorization_detail_from_account);
        TextView toAccount = view.findViewById(R.id.authorization_detail_to_account);
        TextView serial = view.findViewById(R.id.authorization_detail_serial);
        TextView startDate = view.findViewById(R.id.authorization_detail_start_date);
        TextView endDate = view.findViewById(R.id.authorization_detail_end_date);
        TextView description = view.findViewById(R.id.authorization_detail_descripiton);
        TextView availableTime = view.findViewById(R.id.authorization_detail_available_time);
        TextView availableDay = view.findViewById(R.id.authorization_detail_available_day);

        lockerName.setText(authorization.getLockerName());
        fromAccount.setText(authorization.getFromAccount());
        toAccount.setText(authorization.getToAccount());
        serial.setText(authorization.getSerial());
        startDate.setText(authorization.getStartTime());
        endDate.setText(authorization.getEndTime());
        description.setText(authorization.getDescription());

        if (authorization.getDailyStartTime().equals("00:00:00")
                && authorization.getDailyEndTime().equals("23:59:00")) {
            availableTime.setText(R.string.whole_day);
        } else {
            availableTime.setText(authorization.getDailyStartTime() + " - " + authorization.getDailyEndTime());
        }

        if (authorization.getWeekDay().equals("1,2,3,4,5,6,7")) {
            availableDay.setText(R.string.every_day);
        } else {
            availableDay.setText(authorization.getReadableWeekday());
        }
        return view;
    }
}
