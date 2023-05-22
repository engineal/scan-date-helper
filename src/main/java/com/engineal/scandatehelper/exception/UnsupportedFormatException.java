package com.engineal.scandatehelper.exception;

import java.nio.file.Path;
import java.text.MessageFormat;

public class UnsupportedFormatException extends ImageException {

    public UnsupportedFormatException(Path image) {
        super(MessageFormat.format("Unsupported image format for image: {0}", image));
    }

    public UnsupportedFormatException(Path image, Class<?> format) {
        super(MessageFormat.format("Unsupported image format: {0} for image: {1}", format, image));
    }
}
