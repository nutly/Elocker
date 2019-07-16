package com.feiyang.elocker.adpter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.feiyang.elocker.R;
import com.feiyang.elocker.model.Authorization;
import com.feiyang.elocker.rest.AuthorizationRest;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;


public class AuthorizationRecyclerViewAdapter extends RecyclerView.Adapter<AuthorizationRecyclerViewAdapter.RecycleViewHolder> {

    private final static int ITEM_TYPE = 0;
    private final static int HEADER_TYPE = 1;
    private LinkedHashMap<String, List<Authorization>> mAuthorizationsMap;
    /*记录列表是否展开,展开为1，不展开为0, key 为lockerName*/
    private HashMap<String, Boolean> mSpreadMap;
    /*记录每组授权的表头位置*/
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
            final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            /*获取Header所在的position*/
            final int headerIndex = this.getHeaderPosition(position);
            final String headerName = mHeaderIndex.get(headerIndex);
            List<Authorization> authorizations = mAuthorizationsMap.get(headerName);
            int authorizationIndex = position - headerIndex - 1;
            final Authorization authorization = authorizations.get(authorizationIndex);

            itemViewHolder.mToAccount.setText(authorization.getToAccount());
            itemViewHolder.mDescription.setText(authorization.getDescription());
            itemViewHolder.mMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    AlertDialog.Builder menuBuilder = new AlertDialog.Builder(context);
                    Resources res = context.getResources();
                    String[] menus = res.getStringArray(R.array.authorizationMenu);
                    AlertDialog authorizationMenu = menuBuilder
                            .setTitle(authorization.getDescription())
                            .setItems(menus,
                                    new AuthorizationItemMenuListener(context,
                                            mAuthorizationsMap, itemViewHolder.getAdapterPosition(), headerIndex, headerName))
                            .create();
                    authorizationMenu.show();
                }
            });

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
        public ImageButton mMenu;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mToAccount = (TextView) itemView.findViewById(R.id.authorization_item_to_account);
            mDescription = (TextView) itemView.findViewById(R.id.authorization_item_description);
            mMenu = (ImageButton) itemView.findViewById(R.id.authorization_item_menu_btn);
        }
    }

    /*响应菜单点击事件*/
    private class AuthorizationItemMenuListener implements DialogInterface.OnClickListener {
        private Context mContext;
        private LinkedHashMap<String, List<Authorization>> mAuthorizationsMap;
        /*点击对应项目的位置*/
        private int mPosition;
        /*记录对应的分组表头的位置*/
        private int mHeaderIndex;
        private String mHeaderName;

        public AuthorizationItemMenuListener(Context context,
                                             LinkedHashMap<String, List<Authorization>> authorizationsMap,
                                             int position, int headerIndex, String headerName) {
            mContext = context;
            mPosition = position;
            mAuthorizationsMap = authorizationsMap;
            mHeaderIndex = headerIndex;
            mHeaderName = headerName;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            int authorizationIndex = mPosition - mHeaderIndex - 1;
            Authorization authorization = mAuthorizationsMap.get(mHeaderName).get(authorizationIndex);
            switch (which) {
                /*详情*/
                case 0:
                    AlertDialog authorizationDetail = builder.setTitle(R.string.detail)
                            .setView(createAuthorizationDetailView(mContext, authorization))
                            .create();
                    authorizationDetail.show();
                    break;
                /*删除*/
                case 1:
                    AuthorizationRest authorizationRest = new AuthorizationRest();
                    authorizationRest.delAuthorizationById(authorization.getId(), authorization.getSerial());
                    int itemCount = getItemCount();
                    mAuthorizationsMap.get(mHeaderName).remove(authorizationIndex);
                    /*该分组下没有子项目*/
                    if (mAuthorizationsMap.get(mHeaderName).size() == 0) {
                        mAuthorizationsMap.remove(mHeaderName);
                        notifyItemMoved(mPosition - 1, mPosition);
                        notifyItemRangeChanged(mPosition - 1, itemCount);
                        //notifyDataSetChanged();
                    } else {
                        notifyItemRemoved(mPosition);
                        notifyItemRangeChanged(mPosition, itemCount);
                    }
                    break;
                default:
                    break;
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
                availableTime.setText(authorization.getDailyStartTime() + " --" + authorization.getDailyEndTime());
            }

            if (authorization.getWeekDay().equals("1,2,3,4,5,6,7")) {
                availableDay.setText(R.string.every_day);
            } else {
                String weekday = authorization.getWeekDay();
                Resources res = context.getResources();
                availableDay.setText(weekday
                        .replace("1", res.getString(R.string.Monday))
                        .replace("2", res.getString(R.string.Tuesday))
                        .replace("3", res.getString(R.string.Wednesday))
                        .replace("4", res.getString(R.string.Thursday))
                        .replace("5", res.getString(R.string.Friday))
                        .replace("6", res.getString(R.string.Saturday))
                        .replace("7", res.getString(R.string.Sunday)));
            }
            return view;
        }
    }

}
