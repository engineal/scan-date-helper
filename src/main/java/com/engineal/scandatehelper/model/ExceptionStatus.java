package com.engineal.scandatehelper.model;

import com.engineal.scandatehelper.model.ImageStatus;

public class ExceptionStatus implements ImageStatus {

    private final Throwable exception;

    public ExceptionStatus(Throwable exception) {
        this.exception = exception;
    }

    @Override
    public boolean isComplete() {
        return true;
    }
}
