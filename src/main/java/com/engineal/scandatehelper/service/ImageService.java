package com.engineal.scandatehelper.service;

import java.io.Closeable;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

public interface ImageService extends Closeable {
    CompletableFuture<Void> changeDate(Path image, LocalDate date);
    //ImageModel getImage(File image);
    //void setDateTaken(File image, LocalDate date);
}
