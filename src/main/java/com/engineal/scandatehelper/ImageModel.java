package com.engineal.scandatehelper;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.concurrent.Future;

public class ImageModel {

    private final ReadOnlyObjectWrapper<Path> pathProperty;
    private final ReadOnlyObjectWrapper<LocalDate> originalDateProperty;
    private final ReadOnlyObjectWrapper<LocalDate> newDateProperty;
    private final ReadOnlyObjectWrapper<Future<?>> statusProperty;

    public ImageModel(Path image, LocalDate originalDate, LocalDate newDate, Future<?> status) {
        this.pathProperty = new ReadOnlyObjectWrapper<>(image);
        this.originalDateProperty = new ReadOnlyObjectWrapper<>(originalDate);
        this.newDateProperty = new ReadOnlyObjectWrapper<>(newDate);

        // TODO: future probably needs to update this observable somehow
        this.statusProperty = new ReadOnlyObjectWrapper<>(status);
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

    public Future<?> getStatus() {
        return statusProperty.get();
    }

    public ReadOnlyObjectProperty<Future<?>> statusProperty() {
        return statusProperty;
    }
}
