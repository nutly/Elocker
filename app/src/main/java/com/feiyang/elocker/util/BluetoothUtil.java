package com.feiyang.elocker.util;

import android.bluetooth.*;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.widget.Toast;
import com.feiyang.elocker.Constant;
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
    /*校验成功后默认执行开锁操作*/
    private short mAction = 1;
    private static short OPEN = 0;
    private static short CLOSE = 1;

    private static int MAX_SCAN_TIME = 10000; //毫秒
    /*开锁之后等待该时间自动关锁*/
    private static int CLOSE_WAIT_TIME = 4000;

    /*扫描结果回调*/
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, final ScanResult result) {
            result.getDevice().connectGatt(mContext, false, mGattCallBack);
        }

        @Override
        public void onScanFailed(int errorCode) {
            switch (errorCode) {
                case SCAN_FAILED_ALREADY_STARTED:
                    Toast.makeText(mContext, R.string.error_already_started, Toast.LENGTH_LONG).show();
                    break;
                case SCAN_FAILED_FEATURE_UNSUPPORTED:
                    Toast.makeText(mContext, R.string.error_feature_not_support, Toast.LENGTH_LONG).show();
                    break;
                case SCAN_FAILED_APPLICATION_REGISTRATION_FAILED:
                    Toast.makeText(mContext, R.string.error_app_not_registered, Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(mContext, R.string.internal_error, Toast.LENGTH_LONG).show();
            }
        }
    };

    /*连接回调*/
    private final BluetoothGattCallback mGattCallBack = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                gatt.close();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                mCharacteristics.clear();
                for (BluetoothGattService s : gatt.getServices()) {
                    BluetoothGattService service = gatt.getService(UUID.fromString(Constant.SERVICE_UUID));
                    System.out.println(service == null ? "*****************" : service.getUuid() + "########################");
                    if (s.getUuid().toString().equals(Constant.SERVICE_UUID)) {
                        for (BluetoothGattCharacteristic ch : s.getCharacteristics()) {
                            mCharacteristics.put(ch.getUuid().toString(), ch);
                        }
                    }
                }
                /*读取随机码*/
                if (mCharacteristics.containsKey(Constant.RANDOM_KEY_CHARACT_UUID)) {
                    gatt.readCharacteristic(mCharacteristics.get(Constant.RANDOM_KEY_CHARACT_UUID));
                }
            } else {
                gatt.close();
            }
        }

        @Override
        public void onCharacteristicRead(final BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                String rCode = bytesToStr(characteristic.getValue());
                /*先替换MAC地址中的冒号，计算出加密指令后取前16位*/
                String encPass = MD5Util.md5Hex(mMac.replaceAll(":", "") + mPAK + rCode);
                encPass = encPass.substring(0, 16);
                /*执行关锁或者开锁操作*/
                if (mAction == OPEN) {
                    mCharacteristics.get(Constant.OPEN_CHARACT_UUID).setValue(hexStringToBytes(encPass));
                    gatt.writeCharacteristic(mCharacteristics.get(Constant.OPEN_CHARACT_UUID));
                    /*开锁成功后自动关锁*/
                    mAction = CLOSE;
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            gatt.readCharacteristic(mCharacteristics.get(Constant.RANDOM_KEY_CHARACT_UUID));
                        }
                    }, CLOSE_WAIT_TIME);
                } /*电量充足时才执行关锁操作*/ else if (mAction == CLOSE && !isPowerLow(rCode)) {
                    mCharacteristics.get(Constant.CLOSE_CHARACT_UUID).setValue(hexStringToBytes(encPass));
                    gatt.writeCharacteristic(mCharacteristics.get(Constant.CLOSE_CHARACT_UUID));
                    gatt.close();
                }
            }
        }
    };

    public BluetoothUtil(Context context, Handler handler) {
        mHandler = handler;
        mContext = context;
        BluetoothManager bm = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mAdapter = bm.getAdapter();
        mCharacteristics = new HashMap<>();
    }

    public void openLocker(String mac, String pak) {
        mMac = mac;
        mPAK = pak;
        mAction = OPEN;
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
            status = Integer.parseInt(rCode.substring(5, 6));
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

    private byte[] hexStringToBytes(String hex) {
        hex = hex.toUpperCase();
        int length = hex.length() / 2;
        byte[] b = new byte[length];
        char[] chs = hex.toCharArray();
        for (int i = 0; i < length; i++) {
            b[i] = (byte) ((charToByte(chs[2 * i]) & 0xFF) << 4 |
                    charToByte(chs[2 * i + 1]) & 0xFF);
        }
        return b;
    }

    private byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
}
