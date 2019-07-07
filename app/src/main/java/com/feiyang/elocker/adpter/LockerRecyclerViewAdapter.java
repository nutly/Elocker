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
import com.feiyang.elocker.activity.UnlockActivity;
import com.feiyang.elocker.model.Locker;
import com.feiyang.elocker.rest.LockerRest;

import java.io.Serializable;
import java.util.List;


public class LockerRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Locker> mLockers;
    private final String btnText = ">>";

    public LockerRecyclerViewAdapter(List<Locker> lockers) {
        mLockers = lockers;
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
            lockerViewHolder.mLocker = mLockers.get(position);
            lockerViewHolder.mDescriptionView.setText(mLockers.get(position).getDescription());
            lockerViewHolder.mPhoneNumView.setText(mLockers.get(position).getPhoneNum());
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
            switch (which) {
                case 0:
                    /*查看*/
                    AlertDialog lockerDetail = builder
                            .setTitle(mLockers.get(mPosition).getDescription())
                            .setView(createLockerDetailView(context, mLockers.get(mPosition)))
                            .create();
                    lockerDetail.show();
                    //设置弹出框大小
                    //dialog.getWindow().setLayout(DensityUtil.dip2px(context,300), LinearLayout.LayoutParams.WRAP_CONTENT);
                    break;
                case 1:
                    /*编辑*/
                    final View lockerModifyView = this.createLockerModifyView(context, mLockers.get(mPosition));
                    AlertDialog lockerModify = builder
                            .setTitle(mLockers.get(mPosition).getDescription())
                            .setView(lockerModifyView)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    EditText lockerName = (EditText) lockerModifyView.findViewById(R.id.locker_modify_name);
                                    String lockerDescription = lockerName.getText().toString();
                                    if (lockerDescription != null && !lockerDescription.equals("")) {
                                        mLockers.get(mPosition).setDescription(lockerDescription);
                                        LockerRest lockerRest = new LockerRest();
                                        lockerRest.updateLockerDescription(mLockers.get(mPosition));

                                        /*刷新页面*/
                                        notifyItemChanged(mPosition);
                                    }
                                }
                            })
                            .create();
                    lockerModify.show();
                    break;
                case 2:
                    /*删除*/
                    break;
                case 3:
                    /*授权*/
                    Intent intent = new Intent(context, AuthorizationEditActivity.class);
                    Bundle data = new Bundle();
                    data.putSerializable("locker", mLockers.get(mPosition));
                    intent.putExtras(data);
                    context.startActivity(intent);
                    break;
                case 4:
                    /*转移*/
                    final String lockerDescription = mLockers.get(mPosition).getDescription();
                    final View lockerTransferview = createLockerTransferview(context, lockerDescription);
                    AlertDialog lockerTransfer = builder
                            .setTitle(lockerDescription)
                            .setView(lockerTransferview)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    EditText toAccountEditText =
                                            lockerTransferview.findViewById(R.id.locker_transfer_to_account);
                                    String toAccount = toAccountEditText.getText().toString();
                                    if (toAccount != null) {
                                        LockerRest lockerRest = new LockerRest();
                                        lockerRest.transferLocker(
                                                mLockers.get(mPosition).getPhoneNum(),
                                                mLockers.get(mPosition).getSerial(),
                                                toAccount
                                        );
                                    }

                                }
                            })
                            .create();
                    lockerTransfer.show();
                    break;
            }
        }

        private View createLockerDetailView(Context context, Locker locker) {
            View view = View.inflate(context, R.layout.locker_detail, null);
            TextView lockerName = (TextView) view.findViewById(R.id.locker_detail_name);
            TextView phoneNum = (TextView) view.findViewById(R.id.locker_detail_phone_num);
            TextView lastOpen = (TextView) view.findViewById(R.id.locker_detail_lastopen);
            TextView toggleTime = (TextView) view.findViewById(R.id.locker_detail_toggletime);
            TextView createTime = (TextView) view.findViewById(R.id.locker_detail_createtime);
            TextView serial = (TextView) view.findViewById(R.id.locker_detail_serial);
            TextView type = (TextView) view.findViewById(R.id.locker_detail_type);

            lockerName.setText(locker.getDescription());
            phoneNum.setText(locker.getSerial());
            lastOpen.setText(locker.getLastOpenTime());
            toggleTime.setText(String.valueOf(locker.getToggleTimes()));
            createTime.setText(locker.getCreateTime());
            serial.setText(locker.getSerial());
            type.setText(locker.getHwType());
            return view;
        }

        private View createLockerModifyView(Context context, final Locker locker) {
            View view = View.inflate(context, R.layout.locker_modify, null);
            final EditText lockerName = (EditText) view.findViewById(R.id.locker_modify_name);
            TextView phoneNum = (TextView) view.findViewById(R.id.locker_modify_phone_num);
            TextView serial = (TextView) view.findViewById(R.id.locker_modify_serial);
            TextView type = (TextView) view.findViewById(R.id.locker_modify_type);

            lockerName.setText(locker.getDescription());
            phoneNum.setText(locker.getPhoneNum());
            serial.setText(locker.getSerial());
            type.setText(locker.getHwType());

            return view;
        }

        private View createLockerTransferview(Context context, String lockerDescription) {
            View view = View.inflate(context, R.layout.locker_transfer, null);
            EditText toAccountEditText = (EditText) view.findViewById(R.id.locker_transfer_to_account);
            TextView lockerName = (TextView) view.findViewById(R.id.locker_transfer_locker_name);

            lockerName.setText(lockerDescription);
            return view;
        }
    }
}
