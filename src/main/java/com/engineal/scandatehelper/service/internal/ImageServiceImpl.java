package com.engineal.scandatehelper.service.internal;

import com.engineal.scandatehelper.service.ImageService;

import java.io.Closeable;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ImageServiceImpl implements ImageService, Closeable {

    private final ExecutorService executorService;

    public ImageServiceImpl() {
        // A single thread should be enough but could add more if needed
        this.executorService = Executors.newSingleThreadExecutor();
    }

    /*@Override
    public ImageModel getImage(Path image) {
        return new ImageModel(image, image.getName(), LocalDate.now(), LocalDate.now());
    }*/

    public Future<?> changeDate(Path image, LocalDate date) {
        // Enqueue change
        return executorService.submit(() -> {

        });
    }

    /*@Override
    public void setDateTaken(File image, LocalDate date) {
        try {
            final ImageMetadata metadata = Imaging.getMetadata(image);

            if (metadata instanceof JpegImageMetadata) {
                handleJpeg((JpegImageMetadata) metadata);
            } else {
                // TODO: error
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ImageReadException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleJpeg(JpegImageMetadata metadata) throws ImageReadException {
        TiffField field = metadata.findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
        if (field == null) {
            // TODO: error
        } else {
            field.getValue();
        }
    }*/

    @Override
    public void close() {
        // TODO: executorService.close(); with Java 19
        executorService.shutdown();
    }
}
