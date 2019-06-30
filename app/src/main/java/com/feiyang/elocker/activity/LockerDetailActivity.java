package com.feiyang.elocker.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.feiyang.elocker.R;
import com.feiyang.elocker.model.Locker;

import java.util.ArrayList;
import java.util.List;

public class LockerDetailActivity extends AppCompatActivity {

    private Locker mLocker;
    // menu[] = {"序列号", "类型", "添加时间", "最后开锁时间", "开锁次数"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locker_detail);

        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        mLocker = (Locker) data.getSerializable("locker");
        List<LockerDetail> lockerDetails = new ArrayList<LockerDetail>();

        Resources res = this.getResources();
        lockerDetails.add(new LockerDetail(res.getString(R.string.lockerDescription), mLocker.getDescription()));
        lockerDetails.add(new LockerDetail(res.getString(R.string.serial), mLocker.getSerial()));
        lockerDetails.add(new LockerDetail(res.getString(R.string.serial), mLocker.getSerial()));
        lockerDetails.add(new LockerDetail(res.getString(R.string.hwType), mLocker.getHwType()));
        lockerDetails.add(new LockerDetail(res.getString(R.string.lastOpen), mLocker.getLastOpenTime()));
        lockerDetails.add(new LockerDetail(res.getString(R.string.toggleTimes), String.valueOf(mLocker.getToggleTimes())));

        ListView listView = (ListView) findViewById(R.id.listview);
        LockerDetailAdapter adapter = new LockerDetailAdapter(this, R.layout.locker_detail_item, lockerDetails);
        listView.setAdapter(adapter);
    }

    class LockerDetailAdapter extends ArrayAdapter {
        private int mResourceId;
        private List<LockerDetail> mLockerDetails;

        public LockerDetailAdapter(@NonNull Context context, int resource, @NonNull List objects) {
            super(context, resource, objects);
            this.mResourceId = resource;
            this.mLockerDetails = (List<LockerDetail>) objects;
        }

        @Override
        public LockerDetail getItem(int position) {
            return mLockerDetails.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LockerDetail lockerDetail = this.getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext())
                        .inflate(mResourceId, parent, false);
            }
            TextView itemName = (TextView) convertView.findViewById(R.id.locker_detail_item_name);
            TextView itemValue = (TextView) convertView.findViewById(R.id.locker_detail_item_value);

            itemName.setText(lockerDetail.getItemName());
            itemValue.setText(lockerDetail.getItemValue());
            return convertView;
        }
    }

    private class LockerDetail {
        private String itemName;
        private String itemValue;

        public LockerDetail(String itemName, String itemValue) {
            this.itemName = itemName;
            this.itemValue = itemValue;
        }

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public String getItemValue() {
            return itemValue;
        }

        public void setItemValue(String itemValue) {
            this.itemValue = itemValue;
        }
    }
}
