package com.hkm.dltstclien;

import android.Manifest;
import android.content.Intent;

import com.greysonparrelli.permiso.Permiso;
import com.hkm.dltstclien.application.Splash;

/**
 * Created by zJJ on 1/15/2016.
 */
public class SplashScn extends Splash {


    @Override
    protected void onPermissionGranted() {
        synchronizeData();
    }

    @Override
    protected void onPermissionDenied() {
        finish();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.splash;
    }

    @Override
    protected void V23permission_request() {
        Permiso.getInstance().requestPermissions(
                getPermProcess(),
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        );
    }

    @Override
    protected void synchronizeData() {
        Intent d = new Intent(SplashScn.this, testingApp.class);
        d.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(d);
        finish();
    }


}
