package com.engineal.scandatehelper.model;

import javafx.beans.property.*;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

public class ImageModel {

    private final ReadOnlyObjectWrapper<Path> pathProperty;
    private final ReadOnlyObjectWrapper<LocalDate> originalDateProperty;
    private final ReadOnlyObjectWrapper<LocalDate> newDateProperty;
    private final ObjectProperty<ImageStatus> statusProperty;

    public ImageModel(Path image, LocalDate originalDate, LocalDate newDate, CompletableFuture<Void> status) {
        this.pathProperty = new ReadOnlyObjectWrapper<>(image);
        this.originalDateProperty = new ReadOnlyObjectWrapper<>(originalDate);
        this.newDateProperty = new ReadOnlyObjectWrapper<>(newDate);

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

    public LocalDate getOriginalDate() {
        return originalDateProperty.get();
    }

    public ReadOnlyObjectProperty<LocalDate> originalDateProperty() {
        return originalDateProperty;
    }

    public LocalDate getNewDate() {
        return newDateProperty.get();
    }

    public ReadOnlyObjectProperty<LocalDate> newDateProperty() {
        return newDateProperty;
    }

    public ImageStatus getStatus() {
        return statusProperty.get();
    }

    public ObjectProperty<ImageStatus> statusProperty() {
        return statusProperty;
    }
}
