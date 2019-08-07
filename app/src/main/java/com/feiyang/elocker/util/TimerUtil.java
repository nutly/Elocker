package com.feiyang.elocker.util;

import android.os.CountDownTimer;
import android.widget.Button;
import com.feiyang.elocker.R;

public class TimerUtil extends CountDownTimer {
    private Button mBtn;

    /*
     * @param button
     * @param totalTime  总计数时间，单位为秒
     * @param countInterval  触发间隔，单位为秒
     * @return
     */
    public TimerUtil(Button button, int totalTime, int countInterval) {
        super(totalTime * 1000L, countInterval * 1000L);
        this.mBtn = button;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        String tips = mBtn.getContext().getResources().getString(R.string.get_register_code_timer);
        mBtn.setText(millisUntilFinished / 1000 + " " + tips);
    }

    @Override
    public void onFinish() {
        mBtn.setText(R.string.reget_register_code);
        mBtn.setClickable(true);
        mBtn.setBackgroundColor(mBtn.getContext().getColor(R.color.colorLightGray));
    }
}
