package com.engineal.scandatehelper;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.util.converter.IntegerStringConverter;

import java.io.File;
import java.net.URL;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static java.time.temporal.ChronoField.YEAR;

public class ScanDateHelperController implements Initializable {
    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField yearTextField;
    @FXML
    private ChoiceBox<String> monthChoiceBox;
    @FXML
    private TextField dayTextField;

    @FXML
    private Hyperlink directoryText;

    @FXML
    private CheckBox scanCheckBox;

    private ResourceBundle resources;

    private static boolean isValidYear(int year) {
        try {
            YEAR.checkValidValue(year);
            return true;
        } catch (DateTimeException exception) {
            return false;
        }
    }

    private static boolean isValidDay(int year, int month, int day) {
        try {
            if (day < 1) {
                return false;
            }
            LocalDate date = LocalDate.of(year, month, day);
            return day <= date.getMonth().length(date.isLeapYear());
        } catch (DateTimeException exception ) {
            return false;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;

        initializeYearTextField();
        initializeMonthChoiceBox();
        initializeDayTextField();
    }

    private void initializeYearTextField() {
        // Validate year field
        yearTextField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), null, change -> {
            String newText = change.getControlNewText();
            try {
                return newText.isEmpty() || isValidYear(Integer.parseInt(newText)) ? change : null;
            } catch (NumberFormatException exception) {
                return null;
            }
        }));
        // Update month field and simple date picker with newly selected date
        yearTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                monthChoiceBox.setValue(resources.getString("choice.month.unknown"));
                monthChoiceBox.setDisable(true);
                // Date field will follow with month listener
            } else {
                monthChoiceBox.setDisable(false);

                // Handles invalidation of February 29th when switching to a non leap year
                try {
                    int newYear = Integer.parseInt(newValue);
                    int month = monthChoiceBox.getSelectionModel().getSelectedIndex();
                    int day = Integer.parseInt(dayTextField.getText());
                    if (isValidDay(newYear, month, day)) {
                        onAdvancedDateChanged(newYear, month, day);
                    } else {
                        dayTextField.setText("");
                    }
                } catch (NumberFormatException ignored) {}
            }
        });
    }

    private void initializeMonthChoiceBox() {
        // Add localized month names
        monthChoiceBox.getItems().add(resources.getString("choice.month.unknown"));
        Arrays.stream(Month.values()).forEachOrdered(month -> monthChoiceBox.getItems().add(month.getDisplayName(TextStyle.FULL, Locale.getDefault())));

        // Update day field and simple date picker with newly selected date
        monthChoiceBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() == 0) {
                dayTextField.setText("");
                dayTextField.setDisable(true);
            } else {
                dayTextField.setDisable(false);

                // Handles invalidation of days when switching to month with fewer days
                try {
                    int year = Integer.parseInt(yearTextField.getText());
                    int newMonth = newValue.intValue();
                    int day = Integer.parseInt(dayTextField.getText());
                    if (isValidDay(year, newMonth, day)) {
                        onAdvancedDateChanged(year, newMonth, day);
                    } else {
                        dayTextField.setText("");
                    }
                } catch (NumberFormatException ignored) {}
            }
        });
    }

    private void initializeDayTextField() {
        // Validate day field
        dayTextField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), null, change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty()) {
                return change;
            }
            try {
                int year = Integer.parseInt(yearTextField.getText());
                int month = monthChoiceBox.getSelectionModel().getSelectedIndex();
                int newDay = Integer.parseInt(newText);
                return isValidDay(year, month, newDay) ? change : null;
            } catch (NumberFormatException exception ) {
                return null;
            }
        }));
        // Update simple date picker with newly selected date
        dayTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                int year = Integer.parseInt(yearTextField.getText());
                int month = monthChoiceBox.getSelectionModel().getSelectedIndex();
                int newDay = Integer.parseInt(newValue);
                onAdvancedDateChanged(year, month, newDay);
            } catch (NumberFormatException ignored) {}
        });
    }

    @FXML
    protected void onSimpleDateChanged() {
        // Update advanced date fields with newly selected date
        yearTextField.setText(Integer.toString(datePicker.getValue().getYear()));
        monthChoiceBox.setValue(datePicker.getValue().getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()));
        dayTextField.setText(Integer.toString(datePicker.getValue().getDayOfMonth()));
    }

    private void onAdvancedDateChanged(int year, int month, int day) {
        // Update simple date picker with newly selected date
        datePicker.setValue(LocalDate.of(year, month, day));
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
