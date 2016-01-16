package com.hkm.vdlsdk.exception;

/**
 * Created by zJJ on 12/29/2015.
 */
public class FirstDestinationNotSetException extends Exception {
    public FirstDestinationNotSetException() {
    }

    public FirstDestinationNotSetException(String detailMessage) {
        super(detailMessage);
    }

    public FirstDestinationNotSetException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public FirstDestinationNotSetException(Throwable throwable) {
        super(throwable);
    }

}
