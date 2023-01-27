package com.engineal.scandatehelper.service;

import java.io.Closeable;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.concurrent.Future;

public interface ImageService extends Closeable {
    Future<?> changeDate(Path image, LocalDate date);
    //ImageModel getImage(File image);
    //void setDateTaken(File image, LocalDate date);
}
