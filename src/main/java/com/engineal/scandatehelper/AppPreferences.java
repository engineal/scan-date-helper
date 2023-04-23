package com.engineal.scandatehelper;

import com.engineal.scandatehelper.model.ScanDateHelperModel;
import javafx.stage.Stage;

import java.io.File;
import java.util.prefs.Preferences;

public final class AppPreferences {

    private static final String STAGE_X = "stage.x";
    private static final String STAGE_Y = "stage.y";
    private static final String STAGE_WIDTH = "stage.width";
    private static final String STAGE_HEIGHT = "stage.height";
    private static final String STAGE_MAXIMIZED = "stage.maximized";
    private static final String MODEL_DIRECTORY = "model.directory";

    // TODO: column widths and other model fields

    private final Preferences userPrefs = Preferences.userNodeForPackage(AppPreferences.class);

    public void loadPreferences(Stage stage) {
        double x = userPrefs.getDouble(STAGE_X, -1);
        if (x >= 0) {
            stage.setX(x);
        }

        double y = userPrefs.getDouble(STAGE_Y, -1);
        if (y >= 0) {
            stage.setY(y);
        }

        double width = userPrefs.getDouble(STAGE_WIDTH, -1);
        if (width > 0) {
            stage.setWidth(width);
        }

        double height = userPrefs.getDouble(STAGE_HEIGHT, -1);
        if (height > 0) {
            stage.setHeight(height);
        }

        boolean maximized = userPrefs.getBoolean(STAGE_MAXIMIZED, false);
        stage.setMaximized(maximized);
    }

    public void loadPreferences(ScanDateHelperModel model) {
        String directoryPath = userPrefs.get(MODEL_DIRECTORY, null);
        if (directoryPath != null) {
            File directory = new File(directoryPath);
            if (directory.exists()) {
                model.setDirectory(directory);
            } else {
                userPrefs.remove(MODEL_DIRECTORY);
            }
        }
    }

    public void savePreferences(Stage stage) {
        userPrefs.putBoolean(STAGE_MAXIMIZED, stage.isMaximized());

        // If it's maximized don't remember these so next time it launches it'll restore to the previous location
        if (!stage.isMaximized()) {
            userPrefs.putDouble(STAGE_X, stage.getX());
            userPrefs.putDouble(STAGE_Y, stage.getY());
            userPrefs.putDouble(STAGE_WIDTH, stage.getWidth());
            userPrefs.putDouble(STAGE_HEIGHT, stage.getHeight());
        }
    }

    public void savePreferences(ScanDateHelperModel model) {
        if (model.getDirectory() != null && model.getDirectory().exists()) {
            userPrefs.put(MODEL_DIRECTORY, model.getDirectory().getAbsolutePath());
        } else {
            userPrefs.remove(MODEL_DIRECTORY);
        }
    }
}
