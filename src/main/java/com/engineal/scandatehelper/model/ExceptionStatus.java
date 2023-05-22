package com.engineal.scandatehelper.model;

public class ExceptionStatus implements ImageStatus {

    private final Throwable exception;

    public ExceptionStatus(Throwable exception) {
        this.exception = exception;
    }

    @Override
    public boolean isComplete() {
        return true;
    }

    @Override
    public boolean hasError() {
        return true;
    }

    @Override
    public String getMessage() {
        return exception.getLocalizedMessage();
    }
}
