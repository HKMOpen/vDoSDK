package com.hkm.vdlsdk.exception;

/**
 * Created by zJJ on 12/29/2015.
 */
public class OrderDisableExecption extends Exception {

    public OrderDisableExecption() {
    }

    public OrderDisableExecption(String detailMessage) {
        super(detailMessage);
    }

    public OrderDisableExecption(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public OrderDisableExecption(Throwable throwable) {
        super(throwable);
    }

}
