package com.engineal.scandatehelper.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;

import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;

public class ImageModel {

    private final ReadOnlyObjectWrapper<Path> pathProperty;
    private final ReadOnlyObjectWrapper<OffsetDateTime> originalDateTimeProperty;
    private final ReadOnlyObjectWrapper<OffsetDateTime> newDateTimeProperty;
    private final ReadOnlyObjectWrapper<OffsetDateTime> digitizedDateTimeProperty;
    private final ObjectProperty<ImageStatus> statusProperty;

    public ImageModel(Path path, OffsetDateTime originalDateTime, OffsetDateTime newDateTime, OffsetDateTime digitizedDateTime, CompletableFuture<Void> status) {
        this.pathProperty = new ReadOnlyObjectWrapper<>(path);
        this.originalDateTimeProperty = new ReadOnlyObjectWrapper<>(originalDateTime);
        this.newDateTimeProperty = new ReadOnlyObjectWrapper<>(newDateTime);
        this.digitizedDateTimeProperty = new ReadOnlyObjectWrapper<>(digitizedDateTime);

        this.statusProperty = new SimpleObjectProperty<>(new PendingStatus());
        status.whenComplete(((imageStatus, ex) -> {
            if (ex != null) {
                this.statusProperty.set(new ExceptionStatus(ex));
            } else {
                this.statusProperty.set(new CompleteStatus());
            }
        }));
    }

    public Path getPath() {
        return pathProperty.get();
    }

    public ReadOnlyObjectProperty<Path> pathProperty() {
        return pathProperty;
    }

    public OffsetDateTime getOriginalDateTime() {
        return originalDateTimeProperty.get();
    }

    public ReadOnlyObjectProperty<OffsetDateTime> originalDateTimeProperty() {
        return originalDateTimeProperty;
    }

    public OffsetDateTime getNewDateTime() {
        return newDateTimeProperty.get();
    }

    public ReadOnlyObjectProperty<OffsetDateTime> newDateTimeProperty() {
        return newDateTimeProperty;
    }

    public OffsetDateTime getDigitizedDateTime() {
        return digitizedDateTimeProperty.get();
    }

    public ReadOnlyObjectProperty<OffsetDateTime> newDigitizedDateTimeProperty() {
        return digitizedDateTimeProperty;
    }

    public ImageStatus getStatus() {
        return statusProperty.get();
    }

    public ObjectProperty<ImageStatus> statusProperty() {
        return statusProperty;
    }
}
