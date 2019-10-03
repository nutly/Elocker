package com.feiyang.elocker.util;

import android.bluetooth.*;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import com.feiyang.elocker.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class BluetoothUtil {
    private final BluetoothAdapter mAdapter;
    private Context mContext;
    private Handler mHandler;
    private HashMap<String, BluetoothGattCharacteristic> mCharacteristics;
    /*锁的MAC地址*/
    private String mMac;
    /*每个锁的密钥*/
    private String mPAK;
    /*加密后的密文*/
    private String mEncPass;
    private static int MAX_SCAN_TIME = 20000; //毫秒
    /*开锁之后等待该时间自动关锁*/
    private static int CLOSE_WAIT_TIME = 40000; //
    private static String SERVICE_UUID = "00";
    private static String RANDOM_KEY_UUID = "01";
    private static String OPEN_UUID = "02";
    private static String CLOSE_UUID = "03";

    /*扫描结果回调*/
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, final ScanResult result) {
            result.getDevice().connectGatt(mContext, false, mGattCallBack);
        }
    };

    /*连接回调*/
    private final BluetoothGattCallback mGattCallBack = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (gatt != null) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    gatt.discoverServices();
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    gatt.close();
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS && gatt != null) {
                BluetoothGattService service = gatt.getService(UUID.fromString(SERVICE_UUID));
                if (service != null) {
                    mCharacteristics.clear();
                    for (BluetoothGattCharacteristic ch : service.getCharacteristics()) {
                        Log.e("UUID", "Characteristic find: " + ch.getUuid().toString());
                        mCharacteristics.put(ch.getUuid().toString(), ch);
                    }
                    /*读取随机码*/
                    if (mCharacteristics.containsKey(RANDOM_KEY_UUID))
                        gatt.readCharacteristic(mCharacteristics.get(RANDOM_KEY_UUID));
                } else {
                    gatt.disconnect();
                }
            } else if (gatt != null) {
                gatt.disconnect();
            }
        }

        @Override
        public void onCharacteristicRead(final BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS && gatt != null) {
                String rCode = bytesToStr(characteristic.getValue());
                mEncPass = MD5Util.md5(mMac + mPAK + rCode);
                mCharacteristics.get(OPEN_UUID).setValue(mEncPass);
                gatt.writeCharacteristic(mCharacteristics.get(OPEN_UUID));
                /*如果电量充足，自动上锁*/
                if (!isPowerLow(rCode)) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mCharacteristics.get(CLOSE_UUID).setValue(mEncPass);
                            gatt.writeCharacteristic(mCharacteristics.get(CLOSE_UUID));
                            gatt.disconnect();
                        }
                    }, CLOSE_WAIT_TIME);
                }
            } else if (gatt != null) {
                gatt.disconnect();
            }
        }
    };

    public BluetoothUtil(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;
        BluetoothManager bm = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mAdapter = bm.getAdapter();
        mCharacteristics = new HashMap<>();
    }

    public void openLocker(String mac, String pak) {
        mMac = mac;
        mPAK = pak;
        /*检查系统是否支持蓝牙*/
        if (mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)
                && mAdapter != null) {
            /*开启蓝牙*/
            if (!mAdapter.isEnabled()) {
                mAdapter.enable();
            }
        } else {
            Toast.makeText(mContext, R.string.not_support_ble, Toast.LENGTH_LONG).show();
            return;
        }

        if (mAdapter.isEnabled()) {
            List<ScanFilter> filters = new ArrayList<>();
            ScanFilter scanFilter = new ScanFilter.Builder()
                    .setDeviceAddress(mac)
                    .build();
            filters.add(scanFilter);
            ScanSettings settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build();
            mAdapter.getBluetoothLeScanner().startScan(filters, settings, mScanCallback);
            // 在指定的扫描间隔后停止扫描
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAdapter.getBluetoothLeScanner().stopScan(mScanCallback);
                }
            }, MAX_SCAN_TIME);
        }
    }

    /*判断是否电量过低*/
    private boolean isPowerLow(String rCode) {
        boolean isPowerLow = false;
        int status;
        try {
            status = Integer.parseInt(rCode.substring(4, 5));
        } catch (Exception e) {
            status = 4;
        }
        if (status == 0) {
            Toast.makeText(mContext, R.string.power_too_low, Toast.LENGTH_LONG).show();
            isPowerLow = true;
        } else if (status == 1) {
            Toast.makeText(mContext, R.string.power_low, Toast.LENGTH_LONG).show();
        }
        return isPowerLow;
    }

    private String bytesToStr(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
