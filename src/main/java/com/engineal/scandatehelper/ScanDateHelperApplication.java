package com.engineal.scandatehelper;

import com.engineal.scandatehelper.exception.ImageException;
import com.engineal.scandatehelper.model.ImageModel;
import com.engineal.scandatehelper.model.ScanDateHelperModel;
import com.engineal.scandatehelper.service.DirectoryService;
import com.engineal.scandatehelper.service.ImageService;
import com.engineal.scandatehelper.service.internal.DirectoryServiceImpl;
import com.engineal.scandatehelper.service.internal.ImageServiceImpl;
import com.engineal.scandatehelper.util.SimpleControllerFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ScanDateHelperApplication extends Application {

    private AppPreferences appPreferences;
    private Stage stage;
    private DirectoryService directoryService;
    private ImageService imageService;
    private ScanDateHelperModel model;

    @Override
    public void start(Stage stage) throws IOException {
        this.appPreferences = new AppPreferences();
        this.stage = stage;
        this.directoryService = new DirectoryServiceImpl();
        this.imageService = new ImageServiceImpl();
        this.model = new ScanDateHelperModel();

        directoryService.addListener(path -> {
            try {
                Image image = imageService.getImage(path);
                OffsetDateTime originalDateTime = image.getOriginalDateTime();
                if (!Objects.equals(model.getDate(), originalDateTime.toLocalDate())) {
                    OffsetDateTime newDateTime = model.getDate().atTime(originalDateTime.toLocalTime()).atOffset(originalDateTime.getOffset());
                    CompletableFuture<Void> status = image.setOriginalDateTime(newDateTime);
                    model.addImage(new ImageModel(path, originalDateTime, newDateTime, image.getDigitizedDateTime(), status));
                }
            } catch (ImageException | IOException e) {
                throw new RuntimeException(e);
            }
        });

        appPreferences.loadPreferences(model);
        appPreferences.loadPreferences(stage);

        URL location = ScanDateHelperApplication.class.getResource("scan-date-helper-view.fxml");
        ResourceBundle resources = ResourceBundle.getBundle("com.engineal.scandatehelper.scan-date-helper", Locale.getDefault());
        SimpleControllerFactory controllerFactory = new SimpleControllerFactory(Set.of(
                directoryService,
                imageService,
                model
        ));
        FXMLLoader fxmlLoader = new FXMLLoader(location, resources, null, controllerFactory);
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle(resources.getString("stage.title"));
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws IOException {
        directoryService.close();
        imageService.close();

        appPreferences.savePreferences(model);
        appPreferences.savePreferences(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}
