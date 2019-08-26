package com.feiyang.elocker.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.widget.Toast;
import com.feiyang.elocker.R;

public class BluetoothUtil {
    private BluetoothAdapter mAdapter;
    private Context mContext;
    private Handler mHandler;
    private boolean mIsBluetoothOpen;
    private static int MAX_SCAN_TIME = 20000; //毫秒

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, final ScanResult result) {

        }
    };


    public BluetoothUtil(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;
        BluetoothManager bm = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mAdapter = bm.getAdapter();
        mIsBluetoothOpen = false;
    }

    public void scanLocker() {
        /*检查系统是否支持蓝牙*/
        if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(mContext, R.string.not_support_ble, Toast.LENGTH_LONG).show();
            return;
        }

        /*开启蓝牙*/
        if (!mIsBluetoothOpen) {
            mAdapter.enable();
            mIsBluetoothOpen = true;
        }

        mAdapter.getBluetoothLeScanner().startScan(mScanCallback);
        // 在指定的扫描间隔后停止扫描
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.getBluetoothLeScanner().stopScan(mScanCallback);
            }
        }, MAX_SCAN_TIME);

    }
}
