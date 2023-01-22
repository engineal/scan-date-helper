package com.engineal.scandatehelper;

import com.engineal.scandatehelper.converter.CustomNumberStringConverter;
import com.engineal.scandatehelper.converter.MonthStringConverter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.ResourceBundle;

import static java.time.temporal.ChronoField.YEAR;

public class ScanDateHelperController implements Initializable {
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

    private ResourceBundle resources;

    private final ScanDateHelperModel model = new ScanDateHelperModel();

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
        } catch (DateTimeException exception ) {
            return false;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;

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
            } catch (NumberFormatException exception ) {
                return null;
            }
        }));
        dayTextField.textProperty().bindBidirectional(model.dayProperty(), new CustomNumberStringConverter());
    }

    @FXML
    protected void onDirectoryAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(resources.getString("file-chooser.title"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(resources.getString("file-chooser.extension.jpeg"), "*.jpg", "*.jpeg")
        );
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(directoryText.getScene().getWindow());
        if (selectedFiles == null || selectedFiles.isEmpty()) {
            directoryText.setText(resources.getString("hyperlink.directory.nothing"));
            scanCheckBox.setDisable(true);
        } else if (selectedFiles.size() == 1) {
            directoryText.setText(String.format(resources.getString("hyperlink.directory.singular-file"), selectedFiles.size()));
            scanCheckBox.setDisable(false);
        } else {
            directoryText.setText(String.format(resources.getString("hyperlink.directory.plural-files"), selectedFiles.size()));
            scanCheckBox.setDisable(false);
        }
    }
}
