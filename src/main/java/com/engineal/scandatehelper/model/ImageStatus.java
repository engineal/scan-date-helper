package com.engineal.scandatehelper.model;

public interface ImageStatus {
    boolean isComplete();
    boolean hasError();
    String getMessage();
}
