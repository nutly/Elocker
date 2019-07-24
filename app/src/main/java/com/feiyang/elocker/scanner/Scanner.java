package com.feiyang.elocker.scanner;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import com.feiyang.elocker.Constant;
import com.feiyang.elocker.R;
import com.feiyang.elocker.rest.LockerRest;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.camera.CameraSettings;

import java.lang.ref.WeakReference;

public class Scanner extends AppCompatActivity implements DecoratedBarcodeView.TorchListener {
    private boolean isLightOn = false;
    private ImageButton mSwithLight;
    private DecoratedBarcodeView mDBV;
    private CaptureManager mCaptureManager;

    public static void startScan(AppCompatActivity activity) {
        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setCaptureActivity(Scanner.class);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setOrientationLocked(false);
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.initiateScan();
    }

    public static void handleResult(Context context, int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        Toast.makeText(context, "Result: " + result.getContents(), Toast.LENGTH_LONG).show();
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(context, R.string.failed_to_recognize, Toast.LENGTH_LONG).show();
            } else {
                String serial = result.getContents();
                if (serial != null) {
                    String defaultLockerName = context.getResources().getString(R.string.default_locker);
                    LockerRest lockerRest = new LockerRest(context);
                    lockerRest.addLocker(new ScannerHandler(context), serial, defaultLockerName);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scaner);
        mSwithLight = findViewById(R.id.scanner_torch);
        mDBV = findViewById(R.id.scanner_dbv);
        mDBV.setTorchListener(this);
        // 如果没有闪光灯功能，就去掉相关按钮
        if (!hasFlash()) {
            mSwithLight.setVisibility(View.GONE);
        }

        //初始化
        mCaptureManager = new CaptureManager(this, mDBV);
        mCaptureManager.initializeFromIntent(getIntent(), savedInstanceState);
        mCaptureManager.decode();

        /*设置相机参数*/
        CameraSettings cameraSettings = mDBV.getBarcodeView().getCameraSettings();
        cameraSettings.setAutoFocusEnabled(true);
        cameraSettings.setFocusMode(CameraSettings.FocusMode.CONTINUOUS);

        mSwithLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLightOn) {
                    mDBV.setTorchOff();
                } else {
                    mDBV.setTorchOn();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCaptureManager.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCaptureManager.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCaptureManager.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mCaptureManager.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return mDBV.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    @Override
    public void onTorchOn() {
        isLightOn = true;
    }

    @Override
    public void onTorchOff() {
        isLightOn = false;
    }

    // 判断是否有闪光灯功能
    private boolean hasFlash() {
        return getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    private static class ScannerHandler extends Handler {
        private final WeakReference<Context> mContext;

        public ScannerHandler(Context context) {
            this.mContext = new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(Message message) {
            if (message.what == Constant.MESSAGE_ADD_LOCKER_STATUS) {
                Context context = mContext.get();
                switch (message.getData().getInt("status")) {
                    case 200:
                        Toast.makeText(context, R.string.add_locker_success, Toast.LENGTH_SHORT).show();
                        break;
                    case 603:
                        Toast.makeText(context, R.string.unknow_serial, Toast.LENGTH_LONG).show();
                        break;
                    case 604:
                        Toast.makeText(context, R.string.duplicate_serial, Toast.LENGTH_LONG).show();
                        break;
                    case 500:
                        Toast.makeText(context, R.string.internal_error, Toast.LENGTH_LONG).show();
                        break;
                    case -1:
                        Toast.makeText(context, R.string.network_error, Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
