package com.engineal.scandatehelper.model;

public class CompleteStatus implements ImageStatus {
    @Override
    public boolean isComplete() {
        return true;
    }

    @Override
    public boolean hasError() {
        return false;
    }

    @Override
    public String getMessage() {
        return "Complete";
    }
}
