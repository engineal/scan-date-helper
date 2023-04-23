package com.engineal.scandatehelper;

import com.engineal.scandatehelper.converter.CustomNumberStringConverter;
import com.engineal.scandatehelper.converter.MonthStringConverter;
import com.engineal.scandatehelper.model.ImageModel;
import com.engineal.scandatehelper.model.ScanDateHelperModel;
import com.engineal.scandatehelper.service.DirectoryService;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ResourceBundle;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import static java.time.temporal.ChronoField.YEAR;

public class ScanDateHelperController implements Initializable {
    @FXML
    private RadioButton simpleRadioButton;
    @FXML
    private RadioButton advancedRadioButton;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField yearTextField;
    @FXML
    private ChoiceBox<Month> monthChoiceBox;
    @FXML
    private TextField dayTextField;
    @FXML
    private Hyperlink directoryText;
    @FXML
    private CheckBox scanCheckBox;
    @FXML
    private TableView<ImageModel> imageLog;

    private DirectoryChooser directoryChooser;

    private final DirectoryService directoryService;
    private final ScanDateHelperModel model;

    public ScanDateHelperController(DirectoryService directoryService, ScanDateHelperModel model) {
        this.directoryService = directoryService;
        this.model = model;
    }

    private static boolean isValidYear(int year) {
        try {
            YEAR.checkValidValue(year);
            return true;
        } catch (DateTimeException exception) {
            return false;
        }
    }

    private static boolean isValidDay(int year, Month month, int day) {
        try {
            LocalDate ignored = LocalDate.of(year, month, day);
            return true;
        } catch (DateTimeException exception) {
            return false;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        datePicker.valueProperty().bindBidirectional(model.dateProperty());

        // Validate year field
        yearTextField.setTextFormatter(new TextFormatter<>(new CustomNumberStringConverter(), null, change -> {
            String newText = change.getControlNewText();
            try {
                return newText.isEmpty() || isValidYear(Integer.parseInt(newText)) ? change : null;
            } catch (NumberFormatException exception) {
                return null;
            }
        }));
        yearTextField.textProperty().bindBidirectional(model.yearProperty(), new CustomNumberStringConverter());
        // Enable and disable month field
        model.yearProperty().addListener((observable, oldValue, newValue) -> monthChoiceBox.setDisable(newValue.intValue() == 0));

        monthChoiceBox.getItems().add(null);
        monthChoiceBox.getItems().addAll(Month.values());
        monthChoiceBox.setConverter(new MonthStringConverter());
        monthChoiceBox.valueProperty().bindBidirectional(model.monthProperty());
        // Enable and disable day field
        model.monthProperty().addListener((observable, oldValue, newValue) -> dayTextField.setDisable(newValue == null));

        // Validate day field
        dayTextField.setTextFormatter(new TextFormatter<>(new CustomNumberStringConverter(), null, change -> {
            String newText = change.getControlNewText();
            try {
                return newText.isEmpty() || isValidDay(model.getYear(), model.getMonth(), Integer.parseInt(newText)) ? change : null;
            } catch (NumberFormatException exception) {
                return null;
            }
        }));
        dayTextField.textProperty().bindBidirectional(model.dayProperty(), new CustomNumberStringConverter());

        directoryText.textProperty().bind(Bindings.createStringBinding(() -> model.getDirectory() != null ?
                model.getDirectory().getAbsolutePath() : resources.getString("hyperlink.directory.nothing"),
                model.directoryProperty()));
        scanCheckBox.disableProperty().bind(Bindings.createBooleanBinding(() -> model.getDirectory() == null,
                model.directoryProperty()));

        directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(resources.getString("directory-chooser.title"));
        directoryChooser.initialDirectoryProperty().bindBidirectional(model.directoryProperty());

        imageLog.itemsProperty().bind(model.imagesProperty());
    }

    @FXML
    protected void onFileMenuReset() {
        Preferences userPrefs = Preferences.userNodeForPackage(getClass());
        try {
            userPrefs.clear();
            Platform.exit();
        } catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void onFileMenuExit() {
        Platform.exit();
    }

    @FXML
    protected void onDirectoryAction() {
        File selectedDirectory;
        try {
            selectedDirectory = directoryChooser.showDialog(directoryText.getScene().getWindow());
        } catch (IllegalArgumentException exception) {
            // If directory no longer exists
            model.setDirectory(null);
            selectedDirectory = directoryChooser.showDialog(directoryText.getScene().getWindow());
        }
        // If cancel is pressed keep previous directory
        if (selectedDirectory != null) {
            model.setDirectory(selectedDirectory);
        }
    }

    @FXML
    protected void onScanChecked() {
        if (scanCheckBox.isSelected()) {
            try {
                directoryService.start(model.getDirectory().toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            directoryService.stop();
        }
    }
}
