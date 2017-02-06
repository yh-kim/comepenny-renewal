package com.pickth.comepennyrenewal.exception;

/**
 * Created by Kim on 2017-01-31.
 */

public class PickthException extends Exception {
    public PickthException() {
    }

    public PickthException(String detailMessage) {
        super(detailMessage);
    }

    public PickthException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public PickthException(Throwable throwable) {
        super(throwable);
    }
}
