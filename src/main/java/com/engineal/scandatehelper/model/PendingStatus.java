package com.engineal.scandatehelper.model;

public class PendingStatus implements ImageStatus {
    @Override
    public boolean isComplete() {
        return false;
    }
}
