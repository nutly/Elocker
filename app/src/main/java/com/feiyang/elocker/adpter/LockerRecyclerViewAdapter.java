package com.feiyang.elocker.adpter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.feiyang.elocker.R;
import com.feiyang.elocker.fragment.LockerFragment;
import com.feiyang.elocker.fragment.LockerFragment.OnLockerFragmentInteractionListener;
import com.feiyang.elocker.model.Locker;

import java.util.List;


public class LockerRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Locker> mValues;
    private final LockerFragment.OnLockerFragmentInteractionListener mListener;
    private final String menuButton = ">>";

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
            lockerViewHolder.mButtonView.setText(this.menuButton);
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
        public final TextView mButtonView;
        public final ImageView mImageView;
        public Locker mLocker;

        public LockerViewHolder(View view) {
            super(view);
            mView = view;
            mDescriptionView = (TextView) view.findViewById(R.id.locker_description);
            mPhoneNumView = (TextView) view.findViewById(R.id.locker_origin);
            mImageView = (ImageView) view.findViewById((R.id.locker_ic));
            mButtonView = (TextView) view.findViewById((R.id.locker_button));
            /*ImaginViewHelper.setImaginViewColor(mImageView,R.color.colorLightBlue);*/
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mDescriptionView.getText() + "'";
        }
    }

}
