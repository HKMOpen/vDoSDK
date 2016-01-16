package com.hkm.dltstclien.monkeyTest;

import android.app.Fragment;
import android.support.annotation.LayoutRes;
import android.view.View;

/**
 * Created by zJJ on 1/16/2016.
 */
public abstract class z_bonnn extends Fragment {
    @LayoutRes
    protected abstract int LayoutID();

    protected abstract void initBinding(View v) ;

    protected abstract void run_bind_program_start();
}
