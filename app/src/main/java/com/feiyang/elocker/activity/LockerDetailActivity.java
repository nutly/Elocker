package com.feiyang.elocker.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.feiyang.elocker.R;

public class LockerDetailActivity extends AppCompatActivity {

    private String menu[] = {"序列号", "类型", "添加时间", "开锁次数"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locker_menu);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menu);
        ListView listView = (ListView) findViewById(R.id.locker_menu);
        listView.setAdapter(adapter);
    }

}
