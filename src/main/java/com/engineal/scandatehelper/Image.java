package com.engineal.scandatehelper;

import com.engineal.scandatehelper.exception.ImageException;

import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;

public interface Image {
    OffsetDateTime getOriginalDateTime() throws ImageException;
    OffsetDateTime getDigitizedDateTime() throws ImageException;
    CompletableFuture<Void> setOriginalDateTime(OffsetDateTime originalDateTime);
}
