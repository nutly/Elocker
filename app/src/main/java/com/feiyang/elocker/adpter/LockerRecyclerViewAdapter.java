package com.feiyang.elocker.adpter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.feiyang.elocker.R;
import com.feiyang.elocker.activity.AuthorizationEditActivity;
import com.feiyang.elocker.activity.LockerDetailActivity;
import com.feiyang.elocker.fragment.LockerFragment;
import com.feiyang.elocker.fragment.LockerFragment.OnLockerFragmentInteractionListener;
import com.feiyang.elocker.model.Locker;

import java.util.List;


public class LockerRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Locker> mValues;
    private final String btnText = ">>";
    private final LockerFragment.OnLockerFragmentInteractionListener mListener;
    /*获取当前点击的列表项*/
    private int mPosition = 0;

    public LockerRecyclerViewAdapter(List<Locker> lockers, OnLockerFragmentInteractionListener listener) {
        mValues = lockers;
        mListener = listener;
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
            mPosition = position;
            lockerViewHolder.mTextButtonView.setOnClickListener(new LockerMenu());
            lockerViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        mListener.onLockerFragmentInteraction(lockerViewHolder.mLocker);
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
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
            alertBuilder.setTitle(mValues.get(mPosition).getDescription());
            alertBuilder.setItems(lockerMenu, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent;
                    switch (i) {
                        case 0:
                            /*查看*/
                            intent = new Intent(context, LockerDetailActivity.class);
                            context.startActivity(intent);
                            break;
                        case 1:
                            /*编辑*/
                            break;
                        case 2:
                            /*删除*/
                            break;
                        case 3:
                            /*授权*/
                            intent = new Intent(context, AuthorizationEditActivity.class);
                            context.startActivity(intent);
                            break;
                        case 4:
                            /*转移*/
                            break;
                    }
                }
            });
            AlertDialog lockerMenus = alertBuilder.create();
            lockerMenus.show();
        }
    }

}
