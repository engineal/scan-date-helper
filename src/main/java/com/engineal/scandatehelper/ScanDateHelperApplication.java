package com.engineal.scandatehelper;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class ScanDateHelperApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        URL location = ScanDateHelperApplication.class.getResource("scan-date-helper-view.fxml");
        ResourceBundle resources = ResourceBundle.getBundle("com.engineal.scandatehelper.scan-date-helper", Locale.getDefault());
        FXMLLoader fxmlLoader = new FXMLLoader(location, resources);
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle(resources.getString("stage.title"));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
