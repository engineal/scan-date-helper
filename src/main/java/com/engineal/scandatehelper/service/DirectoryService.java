package com.engineal.scandatehelper.service;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Consumer;

public interface DirectoryService extends Closeable {
    void addListener(Consumer<Path> consumer);
    void removeListener(Consumer<Path> consumer);
    void start(Path directory) throws IOException;
    void stop();
}
