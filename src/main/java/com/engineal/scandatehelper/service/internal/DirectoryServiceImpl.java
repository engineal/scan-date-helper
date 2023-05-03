package com.engineal.scandatehelper.service.internal;

import com.engineal.scandatehelper.service.DirectoryService;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.*;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class DirectoryServiceImpl implements DirectoryService, Closeable {

    private final ExecutorService executorService;
    private final WatchService watcher;
    private final List<Consumer<Path>> consumers = new LinkedList<>();
    private WatchKey key;
    private Future<?> future;

    public DirectoryServiceImpl() throws IOException {
        this.executorService = Executors.newSingleThreadExecutor();
        this.watcher = FileSystems.getDefault().newWatchService();
    }

    public void addListener(Consumer<Path> consumer) {
        consumers.add(consumer);
    }

    public void removeListener(Consumer<Path> consumer) {
        consumers.remove(consumer);
    }

    @Override
    public void start(Path directory) throws IOException {
        // TODO: protect against changing directory

        key = directory.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
        future = executorService.submit(() -> {
            while (!future.isCancelled()) {
                // wait for key to be signaled
                WatchKey key;
                try {
                    key = watcher.take();
                } catch (InterruptedException x) {
                    return;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    // This key is registered only for ENTRY_CREATE events,
                    // but an OVERFLOW event can occur regardless if events are lost or discarded.
                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    }

                    // The filename is the context of the event.
                    Path filename = (Path) event.context();

                    // Verify that the new file is a text file.
                    // Resolve the filename against the directory.
                    // If the filename is "test" and the directory is "foo", the resolved name is "test/foo".
                    Path child = directory.resolve(filename);

                    // Wait for file to be free
                    // TODO: there's probably a better way
                    try {
                        Thread.sleep(Duration.ofSeconds(1));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Notify the consumers
                    consumers.forEach(consumer -> {
                        try {
                            consumer.accept(child);
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                    });
                }

                // Reset the key -- this step is critical if you want to
                // receive further watch events.  If the key is no longer valid,
                // the directory is inaccessible so exit the loop.
                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            }
        });
    }

    @Override
    public void stop() {
        if (future != null) {
            future.cancel(true);
            future = null;
        }
        if (key != null) {
            key.cancel();
            key = null;
        }
    }

    @Override
    public void close() throws IOException {
        if (future != null) {
            future.cancel(true);
            future = null;
        }
        executorService.close();
        if (key != null) {
            key.cancel();
            key = null;
        }
        watcher.close();
    }
}
