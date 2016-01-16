package com.hkm.dltstclien.monkeyTest;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hkm.dltstclien.R;
import com.hkm.dltstclien.application.Dialog.ErrorMessage;

/**
 * Created by zJJ on 1/16/2016.
 */
public abstract class testBasic extends z_bonnn {
    protected ProgressBar betterCircleBar;
    protected TextView console;
    protected Handler mHandler = new Handler();

    @Override
    protected void initBinding(View v) {
        betterCircleBar = (ProgressBar) v.findViewById(R.id.progress_client_display);
        console = (TextView) v.findViewById(R.id.console_display);
    }

    @Override
    protected int LayoutID() {
        return R.layout.test_general_main;
    }

    protected void common_process_done(final String message, final boolean withDialog) {
        if (withDialog) ErrorMessage.alert(message, getFragmentManager());
        addMessage(message);
        completeloading();
    }


    protected void completeloading() {
        ViewCompat.animate(betterCircleBar).alpha(0f).withEndAction(new Runnable() {
            @Override
            public void run() {
                run_bind_program_start();
            }
        });
    }

    protected void addMessage(final String mMessage) {
        if (console.getText().toString().isEmpty()) {
            console.setText(mMessage);
        } else
            console.setText(console.getText().toString() + "\n" + mMessage);
    }


    protected void failure(String error_message_out) {
        ErrorMessage.alert(error_message_out, getFragmentManager());
        completeloading();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(LayoutID(), container, false);
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * has returned, but before any saved state has been restored in to the view.
     * This gives subclasses a chance to initialize themselves once
     * they know their view hierarchy has been completely created.  The fragment's
     * view hierarchy is not however attached to its parent at this point.
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initBinding(view);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                run_bind_program_start();
                completeloading();
            }
        }, 100);
    }
}
