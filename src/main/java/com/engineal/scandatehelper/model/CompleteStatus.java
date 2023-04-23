package com.engineal.scandatehelper.model;

import com.engineal.scandatehelper.model.ImageStatus;

public class CompleteStatus implements ImageStatus {
    @Override
    public boolean isComplete() {
        return true;
    }
}
