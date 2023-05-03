package com.engineal.scandatehelper.control;

import com.engineal.scandatehelper.model.ImageModel;
import com.engineal.scandatehelper.model.ImageStatus;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.paint.Color;
import javafx.util.Callback;

public class StatusCellFactory implements Callback<TableColumn<ImageModel, ImageStatus>, TableCell<ImageModel, ImageStatus>> {
    @Override
    public TableCell<ImageModel, ImageStatus> call(TableColumn<ImageModel, ImageStatus> param) {
        ProgressIndicator progressIndicator = new ProgressIndicator();
        return new TableCell<>() {
            @Override
            protected void updateItem(ImageStatus item, boolean empty) {
                if (item == getItem()) return;

                super.updateItem(item, empty);

                if (item == null) {
                    super.setText(null);
                    super.setGraphic(null);
                } else {
                    super.setText(!item.isComplete() ? null : item.getMessage());
                    super.setGraphic(!item.isComplete() ? progressIndicator : null);
                    super.setTextFill(item.hasError() ? Color.RED : Color.BLACK);
                }
            }
        };
    }
}
