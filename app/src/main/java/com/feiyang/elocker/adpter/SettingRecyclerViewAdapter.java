
package com.feiyang.elocker.adpter;

/*import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.feiyang.elocker.R;
import com.feiyang.elocker.model.Locker;
import com.feiyang.elocker.util.ImaginViewHelper;

import java.util.List;


public class SettingRecyclerViewAdapter extends RecyclerView.Adapter<SettingRecyclerViewAdapter.ViewHolder> {

    private final List<Locker> mValues;
    private final SettingFragment.OnSettingFragmentInteractionListener mListener;
    private final String buttonType = ">>";

    public SettingRecyclerViewAdapter(List<Locker> lockers, SettingFragment.OnSettingFragmentInteractionListener listener) {
        mValues = lockers;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.locker_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mLocker = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getDescription());
        holder.mContentView.setText(mValues.get(position).getPhoneNum());
        holder.mButtonView.setText(this.buttonType);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onSettingFragmentInteraction(holder.mLocker);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final Button mButtonView;
        public final ImageView mImageView;
        public Locker mLocker;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.locker_description);
            mContentView = (TextView) view.findViewById(R.id.locker_origin);
            mImageView = (ImageView) view.findViewById((R.id.locker_ic));
            mButtonView = (Button) view.findViewById((R.id.locker_button));

      ImaginViewHelper.setImaginViewColor(mImageView,R.color.colorLightBlue);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}*/

