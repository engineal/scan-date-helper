package com.engineal.scandatehelper.control;

import com.engineal.scandatehelper.ImageModel;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.nio.file.Path;

public class NameCellFactory implements Callback<TableColumn<ImageModel, Path>, TableCell<ImageModel, Path>> {
    @Override
    public TableCell<ImageModel, Path> call(TableColumn<ImageModel, Path> param) {
        return new TableCell<>() {
            @Override
            protected void updateItem(Path item, boolean empty) {
                if (item == getItem()) return;

                super.updateItem(item, empty);

                if (item == null) {
                    super.setText(null);
                } else {
                    super.setText(item.getFileName().toString());
                }
            }
        };
    }
}
