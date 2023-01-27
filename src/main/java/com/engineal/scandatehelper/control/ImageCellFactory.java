package com.engineal.scandatehelper.control;

import com.engineal.scandatehelper.ImageModel;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

import java.nio.file.Path;

public class ImageCellFactory implements Callback<TableColumn<ImageModel, Path>, TableCell<ImageModel, Path>> {
    @Override
    public TableCell<ImageModel, Path> call(TableColumn<ImageModel, Path> param) {
        ImageView imageView = new ImageView();
        imageView.fitWidthProperty().bind(param.widthProperty());
        imageView.setFitHeight(50);
        imageView.setPreserveRatio(true);

        return new TableCell<>() {
            @Override
            protected void updateItem(Path item, boolean empty) {
                if (item == getItem()) return;

                super.updateItem(item, empty);

                if (item == null) {
                    super.setGraphic(null);
                } else {
                    imageView.setImage(new Image(item.toString()));
                    super.setGraphic(imageView);
                }
            }
        };
    }
}
