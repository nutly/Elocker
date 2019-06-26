package com.feiyang.elocker;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

public class EfficientTimeActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView btnDate, btnTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_efficient_time);
        btnDate = (TextView) findViewById(R.id.startTime);
        btnTime = (TextView) findViewById(R.id.endTime);
        btnDate.setOnClickListener(this);
        btnTime.setOnClickListener(this);
        //显示时间控件

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startTime:
                DatePickerDialog datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        //Toast.makeText(this, year+"year "+(monthOfYear+1)+"month "+dayOfMonth+"day", Toast.LENGTH_SHORT).show();
                    }
                }, 2013, 7, 20);
                datePicker.show();
                break;
        }

    }
}
