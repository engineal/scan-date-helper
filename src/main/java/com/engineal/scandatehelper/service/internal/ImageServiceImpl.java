package com.engineal.scandatehelper.service.internal;

import com.engineal.scandatehelper.service.ImageService;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageServiceImpl implements ImageService, Closeable {

    private final ExecutorService executorService;

    public ImageServiceImpl() {
        // A single thread should be enough but could add more if needed
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public CompletableFuture<Void> changeDate(Path image, LocalDate date) {
        // Enqueue change
        return CompletableFuture.runAsync(() -> {
            try {
                doChangeDate(image, date);
            } catch (IOException | ImageReadException e) {
                throw new RuntimeException(e);
            }
        }, executorService);
    }

    private static void doChangeDate(Path image, LocalDate date) throws IOException, ImageReadException {
        final ImageMetadata metadata = Imaging.getMetadata(image.toFile());

        if (metadata instanceof JpegImageMetadata) {
            changeJpegDate((JpegImageMetadata) metadata, date);
        } else {
            // TODO: error
        }
    }

    private static void changeJpegDate(JpegImageMetadata metadata, LocalDate date) throws ImageReadException {
        TiffField field = metadata.findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
        if (field == null) {
            // TODO: error
        } else {
            System.out.println(field.getValue());
        }
    }

    @Override
    public void close() {
        executorService.close();
    }
}
