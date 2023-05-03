package com.engineal.scandatehelper.service.internal;

import com.engineal.scandatehelper.Image;
import com.engineal.scandatehelper.exception.ImageException;
import com.engineal.scandatehelper.exception.UnsupportedFormatException;
import com.engineal.scandatehelper.service.ImageService;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.TiffDirectoryType;
import org.apache.commons.imaging.formats.tiff.constants.TiffEpTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoAscii;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageServiceImpl implements ImageService, Closeable {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");
    private static final DateTimeFormatter OFFSET_TIME_FORMATTER = DateTimeFormatter.ofPattern("xxx");
    private static final DateTimeFormatter SUB_SEC_FORMATTER = new DateTimeFormatterBuilder()
            .appendFraction(ChronoField.NANO_OF_SECOND, 1, 9, false)
            .toFormatter();

    private static final TagInfoAscii EXIF_TAG_OFFSET_TIME = new TagInfoAscii(
            "OffsetTime", 0x9010, 7,
            TiffDirectoryType.EXIF_DIRECTORY_EXIF_IFD);
    private static final TagInfoAscii EXIF_TAG_OFFSET_TIME_ORIGINAL = new TagInfoAscii(
            "OffsetTimeOriginal", 0x9011, 7,
            TiffDirectoryType.EXIF_DIRECTORY_EXIF_IFD);

    private static final TagInfoAscii EXIF_TAG_OFFSET_TIME_DIGITIZED = new TagInfoAscii(
            "OffsetTimeDigitized", 0x9012, 7,
            TiffDirectoryType.EXIF_DIRECTORY_EXIF_IFD);

    private final ExecutorService executorService;

    public ImageServiceImpl() {
        // A single thread should be enough but could add more if needed
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public Image getImage(Path image) throws ImageException, IOException {
        try {
            final ImageMetadata metadata = Imaging.getMetadata(new File(image.toUri()));
            if (metadata instanceof JpegImageMetadata) {
                return new JpegImage(image, (JpegImageMetadata) metadata);
            } else {
                throw new UnsupportedFormatException(image, metadata.getClass());
            }
        } catch (ImageReadException e) {
            throw new ImageException(e);
        }
    }

    class JpegImage implements Image {

        private final Path path;
        private final JpegImageMetadata metadata;

        JpegImage(Path path, JpegImageMetadata metadata) {
            this.path = path;
            this.metadata = metadata;
        }

        @Override
        public OffsetDateTime getOriginalDateTime() throws ImageException {
            try {
                OffsetDateTime dateTimeOriginal = getOffsetDateTime(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL,
                        ExifTagConstants.EXIF_TAG_SUB_SEC_TIME_ORIGINAL, EXIF_TAG_OFFSET_TIME_ORIGINAL,
                        TiffEpTagConstants.EXIF_TAG_TIME_ZONE_OFFSET, 0);
                if (dateTimeOriginal != null) {
                    return dateTimeOriginal;
                }

                return getOffsetDateTime(TiffTagConstants.TIFF_TAG_DATE_TIME, ExifTagConstants.EXIF_TAG_SUB_SEC_TIME,
                        EXIF_TAG_OFFSET_TIME, TiffEpTagConstants.EXIF_TAG_TIME_ZONE_OFFSET, 1);
            } catch (ImageReadException e) {
                throw new ImageException(e);
            }
        }

        @Override
        public OffsetDateTime getDigitizedDateTime() throws ImageException {
            try {
                OffsetDateTime dateTimeDigitized = getOffsetDateTime(ExifTagConstants.EXIF_TAG_DATE_TIME_DIGITIZED,
                        ExifTagConstants.EXIF_TAG_SUB_SEC_TIME_DIGITIZED, EXIF_TAG_OFFSET_TIME_DIGITIZED);
                if (dateTimeDigitized != null) {
                    return dateTimeDigitized;
                }

                return getOffsetDateTime(TiffTagConstants.TIFF_TAG_DATE_TIME, ExifTagConstants.EXIF_TAG_SUB_SEC_TIME,
                        EXIF_TAG_OFFSET_TIME, TiffEpTagConstants.EXIF_TAG_TIME_ZONE_OFFSET, 1);
            } catch (ImageReadException e) {
                throw new ImageException(e);
            }
        }

        @Override
        public CompletableFuture<Void> setOriginalDateTime(OffsetDateTime originalDateTime) {
            // Enqueue change
            return CompletableFuture.runAsync(() -> {
                final TiffImageMetadata exif = metadata.getExif();
                try {
                    Path tempFile = createTempFile(path);

                    try (OutputStream os = Files.newOutputStream(tempFile)) {
                        final TiffOutputSet outputSet = exif != null ? exif.getOutputSet() : new TiffOutputSet();
                        final TiffOutputDirectory exifDirectory = outputSet.getOrCreateExifDirectory();
                        exifDirectory.removeField(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
                        exifDirectory.removeField(EXIF_TAG_OFFSET_TIME_ORIGINAL);
                        exifDirectory.add(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL, originalDateTime.format(DATE_TIME_FORMATTER));
                        exifDirectory.add(EXIF_TAG_OFFSET_TIME_ORIGINAL, originalDateTime.format(OFFSET_TIME_FORMATTER));
                        new ExifRewriter().updateExifMetadataLossless(new File(path.toUri()), os, outputSet);
                    } catch (ImageReadException | ImageWriteException | IOException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }

                    // Delete original image and move modified image to original path
                    Files.delete(path);
                    Files.move(tempFile, path);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }, executorService);
        }

        private OffsetDateTime getOffsetDateTime(TagInfo dateTimeTag, TagInfo subSecTimeTag, TagInfo offsetTimeTag)
                throws ImageReadException {
            LocalDateTime localDateTime = getLocalDateTime(dateTimeTag, subSecTimeTag);
            if (localDateTime == null) {
                return null;
            }

            ZoneOffset zoneOffset = getZoneOffset(offsetTimeTag);

            // TODO: if no timezone should we assume UTC or return a LocalDateTime?
            return zoneOffset != null ? localDateTime.atOffset(zoneOffset) : localDateTime.atOffset(ZoneOffset.UTC);
        }

        private OffsetDateTime getOffsetDateTime(TagInfo dateTimeTag, TagInfo subSecTimeTag, TagInfo offsetTimeTag,
                                                 TagInfo fallbackOffsetTimeTag, int index) throws ImageReadException {
            LocalDateTime localDateTime = getLocalDateTime(dateTimeTag, subSecTimeTag);
            if (localDateTime == null) {
                return null;
            }

            ZoneOffset zoneOffset = getZoneOffset(offsetTimeTag);
            if (zoneOffset == null) {
                zoneOffset = getZoneOffset(fallbackOffsetTimeTag, index);
            }

            // TODO: if no timezone should we assume UTC or return a LocalDateTime?
            return zoneOffset != null ? localDateTime.atOffset(zoneOffset) : localDateTime.atOffset(ZoneOffset.UTC);
        }

        private LocalDateTime getLocalDateTime(TagInfo dateTimeTag, TagInfo subSecTimeTag) throws ImageReadException {
            TiffField dateTimeField = metadata.findEXIFValue(dateTimeTag);
            if (dateTimeField == null) {
                return null;
            }

            LocalDateTime localDateTime = LocalDateTime.parse(dateTimeField.getStringValue(), DATE_TIME_FORMATTER);

            TiffField subSecTimeField = metadata.findEXIFValue(subSecTimeTag);
            if (subSecTimeField != null) {
                LocalTime fractionalSeconds = LocalTime.parse(subSecTimeField.getStringValue(), SUB_SEC_FORMATTER);
                return localDateTime.withNano(fractionalSeconds.getNano());
            } else {
                return localDateTime;
            }
        }

        private ZoneOffset getZoneOffset(TagInfo offsetTimeTag) throws ImageReadException {
            TiffField offsetTimeField = metadata.findEXIFValue(offsetTimeTag);
            if (offsetTimeField == null) {
                return null;
            }

            return OFFSET_TIME_FORMATTER.parse(offsetTimeField.getStringValue(), ZoneOffset::from);
        }

        private ZoneOffset getZoneOffset(TagInfo offsetTimeTag, int index) throws ImageReadException {
            TiffField offsetTimeField = metadata.findEXIFValue(offsetTimeTag);
            if (offsetTimeField == null) {
                return null;
            }

            int[] zoneOffsets = offsetTimeField.getIntArrayValue();
            if (zoneOffsets.length > index) {
                return ZoneOffset.ofHours(zoneOffsets[index]);
            }

            return null;
        }
    }

    @Override
    public void close() {
        executorService.close();
    }

    private static Path createTempFile(Path originalFile) throws IOException {
        String fileName = originalFile.getFileName().toString();
        int i = fileName.lastIndexOf('.');
        String prefix = fileName.substring(0, i);
        String suffix = fileName.substring(i + 1);
        return Files.createTempFile(prefix, suffix);
    }
}
