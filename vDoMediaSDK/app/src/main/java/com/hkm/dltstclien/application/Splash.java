package com.hkm.dltstclien.application;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.greysonparrelli.permiso.Permiso;

/**
 * Created by hesk on 18/11/15.
 */
public abstract class Splash extends AppCompatActivity {
    private Handler mhandle = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(getLayoutId());
        Permiso.getInstance().setActivity(this);
        mhandle.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    V23permission_request();
                } else {
                    synchronizeData();
                }
            }
        }, delayOnScreenToSync());
    }

    protected int delayOnScreenToSync() {
        return 500;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Permiso.getInstance().setActivity(this);
    }

    protected abstract void onPermissionGranted();

    protected abstract void onPermissionDenied();

    protected abstract void synchronizeData();

    @LayoutRes
    protected abstract int getLayoutId();


    protected Permiso.IOnPermissionResult getPermProcess() {
        return new Permiso.IOnPermissionResult() {
            @Override
            public void onPermissionResult(Permiso.ResultSet resultSet) {
                if (resultSet.areAllPermissionsGranted()) {
                    // Permission granted!
                    onPermissionGranted();
                } else {
                    // Permission denied.
                    onPermissionDenied();
                }
            }

            @Override
            public void onRationaleRequested(Permiso.IOnRationaleProvided callback, String... permissions) {
                Permiso.getInstance().showRationaleInDialog("Request permissions", permission_message(), null, callback);
            }
        };
    }


    protected void V23permission_request() {
        Permiso.getInstance().requestPermissions(
                getPermProcess(),
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.RECEIVE_BOOT_COMPLETED,
                Manifest.permission.VIBRATE,
                Manifest.permission.GET_ACCOUNTS,
                Manifest.permission.WAKE_LOCK
        );
    }

    protected String permission_message() {
        return "You need to grant those permissions";
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Permiso.getInstance().onRequestPermissionResult(requestCode, permissions, grantResults);
    }
}
