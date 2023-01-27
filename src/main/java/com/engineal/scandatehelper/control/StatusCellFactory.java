package com.engineal.scandatehelper.control;

import com.engineal.scandatehelper.ImageModel;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.util.concurrent.Future;

public class StatusCellFactory implements Callback<TableColumn<ImageModel, Future<?>>, TableCell<ImageModel, Future<?>>> {
    @Override
    public TableCell<ImageModel, Future<?>> call(TableColumn<ImageModel, Future<?>> param) {
        ProgressIndicator progressIndicator = new ProgressIndicator();
        return new TableCell<>() {
            @Override
            protected void updateItem(Future<?> item, boolean empty) {
                if (item == getItem()) return;

                super.updateItem(item, empty);

                if (item == null || item.isDone()) {
                    super.setGraphic(null);
                } else {
                    super.setGraphic(progressIndicator);
                }
            }
        };
    }
}
