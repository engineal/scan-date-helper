package com.engineal.scandatehelper.service;

import com.engineal.scandatehelper.Image;
import com.engineal.scandatehelper.exception.ImageException;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;

public interface ImageService extends Closeable {
    boolean isImageSupported(Path image) throws IOException;
    Image getImage(Path image) throws ImageException, IOException;
}
