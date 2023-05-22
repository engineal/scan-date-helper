package com.engineal.scandatehelper.model;

public class PendingStatus implements ImageStatus {
    @Override
    public boolean isComplete() {
        return false;
    }

    @Override
    public boolean hasError() {
        return false;
    }

    @Override
    public String getMessage() {
        return null;
    }
}
