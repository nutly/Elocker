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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.feiyang.elocker.R;
import com.feiyang.elocker.activity.AuthorizationEditActivity;
import com.feiyang.elocker.activity.LockerListActivity;
import com.feiyang.elocker.activity.LogActivity;
import com.feiyang.elocker.activity.UnlockActivity;
import com.feiyang.elocker.model.Locker;
import com.feiyang.elocker.rest.LockerRest;

import java.io.Serializable;
import java.util.List;


public class LockerRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /*当前用户*/
    private final String mPhoneNum;
    private final List<Locker> mLockers;
    private final String btnText = ">>";

    public LockerRecyclerViewAdapter(String phoneNum, List<Locker> lockers) {
        this.mPhoneNum = phoneNum;
        this.mLockers = lockers;
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
            lockerViewHolder.mDescriptionView.setText(mLockers.get(position).getDescription());
            lockerViewHolder.mPhoneNumView.setText(mLockers.get(position).getPhoneNum());
            /*将每个条目的位置为tag记录下来*/
            lockerViewHolder.mTextButtonView.setTag(position);

            lockerViewHolder.mLocker = mLockers.get(position);
            if (mLockers.get(position).getPhoneNum().equals(mPhoneNum)) {
                lockerViewHolder.mTextButtonView.setText(this.btnText);
                lockerViewHolder.mTextButtonView.setOnClickListener(new LockerMenu());
            } else {
                /*对于其它账户授权的锁，禁止进行查看、转移、删除等*/
                lockerViewHolder.mTextButtonView.setVisibility(View.GONE);
            }

            lockerViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    if (context instanceof LockerListActivity) {
                        LockerListActivity lockerListActivity = (LockerListActivity) context;
                        Intent intent = new Intent(context, UnlockActivity.class);
                        Bundle data = new Bundle();
                        int position = (int) lockerViewHolder.mTextButtonView.getTag();
                        data.putSerializable("locker", (Serializable) mLockers.get(position));
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
        return mLockers.size();
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
            mDescriptionView = view.findViewById(R.id.fragment_setting_account);
            mPhoneNumView = view.findViewById(R.id.locker_origin);
            mImageView = view.findViewById((R.id.locker_ic));
            mTextButtonView = view.findViewById((R.id.locker_button));
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
            int position = (int) v.getTag();
            if (mLockers == null || mLockers.get(position) == null) {
                return;
            }
            menuBuilder.setTitle(mLockers.get(position).getDescription());
            menuBuilder.setItems(lockerMenu, new LockerMenuLisener(context, position));
            AlertDialog lockerMenus = menuBuilder.create();
            lockerMenus.show();
        }
    }

    /*设置锁菜单响应*/
    private class LockerMenuLisener implements DialogInterface.OnClickListener {

        private Context context;
        private int mPosition;

        public LockerMenuLisener(Context context, int position) {
            this.context = context;
            this.mPosition = position;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            Intent intent;
            switch (which) {
                case 0:
                    /*查看*/
                    builder.setTitle(mLockers.get(mPosition).getDescription())
                            .setView(createLockerDetailView(context, mLockers.get(mPosition)))
                            .create()
                            .show();
                    //设置弹出框大小
                    //dialog.getWindow().setLayout(DensityUtil.dip2px(context,300), LinearLayout.LayoutParams.WRAP_CONTENT);
                    break;
                case 1:
                    /*编辑*/
                    final View lockerModifyView = this.createLockerModifyView(context, mLockers.get(mPosition));
                    builder.setTitle(mLockers.get(mPosition).getDescription())
                            .setView(lockerModifyView)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    EditText lockerName = lockerModifyView.findViewById(R.id.locker_modify_name);
                                    String lockerDescription = lockerName.getText().toString();
                                    if (lockerDescription != null && !lockerDescription.equals("")) {
                                        mLockers.get(mPosition).setDescription(lockerDescription);
                                        LockerRest lockerRest = new LockerRest(context);
                                        lockerRest.updateLockerDescription(mLockers.get(mPosition));
                                        /*刷新页面*/
                                        notifyItemChanged(mPosition);
                                    }
                                }
                            })
                            .create()
                            .show();
                    break;
                case 2:
                    /*删除*/
                    LockerRest lockerRest = new LockerRest(context);
                    lockerRest.delLocker(mLockers.get(mPosition).getSerial());
                    mLockers.remove(mPosition);
                    notifyItemRemoved(mPosition);
                    notifyItemRangeChanged(mPosition, getItemCount());
                    break;
                case 3:
                    /*授权*/
                    intent = new Intent(context, AuthorizationEditActivity.class);
                    Bundle data = new Bundle();
                    data.putSerializable("locker", mLockers.get(mPosition));
                    intent.putExtras(data);
                    context.startActivity(intent);
                    break;
                case 4:
                    /*转移*/
                    final String lockerDescription = mLockers.get(mPosition).getDescription();
                    final View lockerTransferview = createLockerTransferview(context, lockerDescription);
                    builder.setTitle(lockerDescription)
                            .setView(lockerTransferview)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    EditText toAccountEditText =
                                            lockerTransferview.findViewById(R.id.locker_transfer_to_account);
                                    String toAccount = toAccountEditText.getText().toString();
                                    if (toAccount != null) {
                                        LockerRest lockerRest = new LockerRest(context);
                                        lockerRest.transferLocker(
                                                mLockers.get(mPosition).getPhoneNum(),
                                                mLockers.get(mPosition).getSerial(),
                                                toAccount
                                        );
                                    }
                                }
                            })
                            .create()
                            .show();
                    break;
                /*查看日志*/
                case 5:
                    String serial = mLockers.get(mPosition).getSerial();
                    intent = new Intent(context, LogActivity.class);
                    intent.putExtra("serial", serial);
                    context.startActivity(intent);
                    break;
                default:
                    break;
            }
        }

        private View createLockerDetailView(Context context, Locker locker) {
            View view = View.inflate(context, R.layout.locker_detail, null);
            TextView lockerName = view.findViewById(R.id.locker_detail_name);
            TextView phoneNum = view.findViewById(R.id.locker_detail_phone_num);
            TextView lastOpen = view.findViewById(R.id.locker_detail_lastopen);
            TextView toggleTime = view.findViewById(R.id.locker_detail_toggletime);
            TextView createTime = view.findViewById(R.id.locker_detail_createtime);
            TextView serial = view.findViewById(R.id.locker_detail_serial);
            TextView type = view.findViewById(R.id.locker_detail_type);

            lockerName.setText(locker.getDescription());
            phoneNum.setText(locker.getPhoneNum());
            lastOpen.setText(locker.getLastOpenTime());
            toggleTime.setText(String.valueOf(locker.getToggleTimes()));
            createTime.setText(locker.getCreateTime());
            serial.setText(locker.getSerial());
            type.setText(locker.getHwType());
            return view;
        }

        private View createLockerModifyView(Context context, final Locker locker) {
            View view = View.inflate(context, R.layout.locker_modify, null);
            final EditText lockerName = view.findViewById(R.id.locker_modify_name);
            TextView phoneNum = view.findViewById(R.id.locker_modify_phone_num);
            TextView serial = view.findViewById(R.id.locker_modify_serial);
            TextView type = view.findViewById(R.id.locker_modify_type);

            lockerName.setText(locker.getDescription());
            phoneNum.setText(locker.getPhoneNum());
            serial.setText(locker.getSerial());
            type.setText(locker.getHwType());

            return view;
        }

        private View createLockerTransferview(Context context, String lockerDescription) {
            View view = View.inflate(context, R.layout.locker_transfer, null);
            EditText toAccountEditText = view.findViewById(R.id.locker_transfer_to_account);
            TextView lockerName = view.findViewById(R.id.locker_transfer_locker_name);

            lockerName.setText(lockerDescription);
            return view;
        }
    }
}
