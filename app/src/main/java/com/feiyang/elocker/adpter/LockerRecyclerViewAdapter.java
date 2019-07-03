package com.feiyang.elocker.adpter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.feiyang.elocker.R;
import com.feiyang.elocker.activity.LockerListActivity;
import com.feiyang.elocker.activity.UnlockActivity;
import com.feiyang.elocker.model.Locker;

import java.io.Serializable;
import java.util.List;


public class LockerRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Locker> mValues;
    private final String btnText = ">>";

    public LockerRecyclerViewAdapter(List<Locker> lockers) {
        mValues = lockers;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.locker_list_item, parent, false);
        return new LockerViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LockerViewHolder) {
            final LockerViewHolder lockerViewHolder = (LockerViewHolder) holder;
            lockerViewHolder.mLocker = mValues.get(position);
            lockerViewHolder.mDescriptionView.setText(mValues.get(position).getDescription());
            lockerViewHolder.mPhoneNumView.setText(mValues.get(position).getPhoneNum());
            lockerViewHolder.mTextButtonView.setText(this.btnText);
            /*将每个条目的位置为tag记录下来*/
            lockerViewHolder.mTextButtonView.setTag(position);
            lockerViewHolder.mTextButtonView.setOnClickListener(new LockerMenu());
            lockerViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    if (context instanceof LockerListActivity) {
                        LockerListActivity lockerListActivity = (LockerListActivity) context;
                        Intent intent = new Intent(context, UnlockActivity.class);
                        Bundle data = new Bundle();
                        int position = (int) lockerViewHolder.mTextButtonView.getTag();
                        data.putSerializable("locker", (Serializable) mValues.get(position));
                        intent.putExtras(data);
                        lockerListActivity.startActivity(intent);
                        /*取消Activity切换动画*/
                        lockerListActivity.overridePendingTransition(0, 0);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    private class LockerViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mDescriptionView;
        public final TextView mPhoneNumView;
        public final TextView mTextButtonView;
        public final ImageView mImageView;
        public Locker mLocker;

        public LockerViewHolder(View view) {
            super(view);
            mView = view;
            mDescriptionView = (TextView) view.findViewById(R.id.locker_description);
            mPhoneNumView = (TextView) view.findViewById(R.id.locker_origin);
            mImageView = (ImageView) view.findViewById((R.id.locker_ic));
            mTextButtonView = (TextView) view.findViewById((R.id.locker_button));
            /*ImaginViewHelper.setImaginViewColor(mImageView,R.color.colorLightBlue);*/
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mDescriptionView.getText() + "'";
        }
    }

    /*设置锁菜单*/
    private class LockerMenu implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            final Context context = v.getContext();
            Resources res = context.getResources();
            String[] lockerMenu = res.getStringArray(R.array.lockerMenu);
            // lockerMenu = {"查看", "编辑", "删除", "授权", "转移"};
            AlertDialog.Builder menuBuilder = new AlertDialog.Builder(context);
            final int position = (int) v.getTag();
            if (mValues == null || mValues.get(position) == null) {
                return;
            }
            final Locker locker = mValues.get(position);
            menuBuilder.setTitle(locker.getDescription());
            menuBuilder.setItems(lockerMenu, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    switch (i) {
                        case 0:
                            /*查看*/
                            AlertDialog.Builder lockerDetailBuilder = new AlertDialog.Builder(context);
                            AlertDialog lockerDetail = lockerDetailBuilder
                                    .setTitle(locker.getDescription())
                                    .setView(createLockerDetailView(context, locker))
                                    .create();
                            lockerDetail.show();
                            break;
                        case 1:
                            /*编辑*/
                            break;
                        case 2:
                            /*删除*/
                            break;
                        case 3:
                            /*授权*/
                            break;
                        case 4:
                            /*转移*/
                            break;
                    }
                }
            });
            AlertDialog lockerMenus = menuBuilder.create();
            lockerMenus.show();
        }

        private View createLockerDetailView(Context context, Locker locker) {
            View view = View.inflate(context, R.layout.locker_detail, null);
            TextView lockerName = (TextView) view.findViewById(R.id.locker_detail_name_value);
            TextView phoneNum = (TextView) view.findViewById(R.id.locker_detail_phone_num_value);
            TextView lastOpen = (TextView) view.findViewById(R.id.locker_detail_lastopen_value);
            TextView toggleTime = (TextView) view.findViewById(R.id.locker_detail_toggletime_value);
            TextView createTime = (TextView) view.findViewById(R.id.locker_detail_createtime_value);
            TextView serial = (TextView) view.findViewById(R.id.locker_detail_serial_value);
            TextView type = (TextView) view.findViewById(R.id.locker_detail_type_value);

            lockerName.setText(locker.getDescription());
            phoneNum.setText(locker.getSerial());
            lastOpen.setText(locker.getLastOpenTime());
            toggleTime.setText(String.valueOf(locker.getToggleTimes()));
            createTime.setText(locker.getCreateTime());
            serial.setText(locker.getSerial());
            type.setText(locker.getHwType());
            return view;
        }
    }
}
