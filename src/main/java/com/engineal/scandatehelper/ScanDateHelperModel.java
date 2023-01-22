package com.engineal.scandatehelper;

import javafx.beans.property.*;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Month;

public class ScanDateHelperModel {

    private final ObjectProperty<LocalDate> dateProperty = new SimpleObjectProperty<>();
    private final IntegerProperty yearProperty = new SimpleIntegerProperty();
    private final ObjectProperty<Month> monthProperty = new SimpleObjectProperty<>();
    private final IntegerProperty dayProperty = new SimpleIntegerProperty();

    public ScanDateHelperModel() {
        dateProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                yearProperty.set(newValue.getYear());
                monthProperty.set(newValue.getMonth());
                dayProperty.set(newValue.getDayOfMonth());
            }
        });
        yearProperty.addListener((observable, oldValue, newValue) -> {
            int newYear = newValue.intValue();
            if (newYear == 0) {
                dateProperty.set(null);
                monthProperty.set(null);
                dayProperty.set(0);
            } else {
                if (dateProperty.get() == null) {
                    Month month = monthProperty.get();
                    int day = dayProperty.get();
                    if (month != null && day != 0) {
                        try {
                            dateProperty.set(LocalDate.of(newYear, month, day));
                        } catch (DateTimeException ignored) {}
                    }
                } else {
                    try {
                        dateProperty.set(dateProperty.get().withYear(newYear));
                    } catch (DateTimeException exception) {
                        dateProperty.set(null);
                    }
                }
            }
        });
        monthProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                dateProperty.set(null);
                dayProperty.set(0);
            } else {
                if (dateProperty.get() == null) {
                    int year = yearProperty.get();
                    int day = dayProperty.get();
                    if (year != 0 && day != 0) {
                        try {
                            dateProperty.set(LocalDate.of(year, newValue, day));
                        } catch (DateTimeException ignored) {}
                    }
                } else {
                    try {
                        dateProperty.set(dateProperty.get().withMonth(newValue.getValue()));
                    } catch (DateTimeException exception) {
                        dateProperty.set(null);
                    }
                }
            }
        });
        dayProperty.addListener((observable, oldValue, newValue) -> {
            int newDay = newValue.intValue();
            if (newDay == 0) {
                dateProperty.set(null);
            } else {
                if (dateProperty.get() == null) {
                    int year = yearProperty.get();
                    Month month = monthProperty.get();
                    if (year != 0 && month != null) {
                        try {
                            dateProperty.set(LocalDate.of(year, month, newDay));
                        } catch (DateTimeException ignored) {}
                    }
                } else {
                    try {
                        dateProperty.set(dateProperty.get().withDayOfMonth(newDay));
                    } catch (DateTimeException exception) {
                        dateProperty.set(null);
                    }
                }
            }
        });
    }

    public LocalDate getDate() {
        return dateProperty.get();
    }

    public void setDate(LocalDate date) {
        dateProperty.set(date);
    }

    public Property<LocalDate> dateProperty() {
        return dateProperty;
    }

    public int getYear() {
        return yearProperty.get();
    }

    public void setYear(int year) {
        yearProperty.set(year);
    }

    public IntegerProperty yearProperty() {
        return yearProperty;
    }

    public Month getMonth() {
        return monthProperty.get();
    }

    public void setMonth(Month month) {
        monthProperty.set(month);
    }

    public ObjectProperty<Month> monthProperty() {
        return monthProperty;
    }

    public int getDay() {
        return dayProperty.get();
    }

    public void setDay(int day) {
        dayProperty.set(day);
    }

    public IntegerProperty dayProperty() {
        return dayProperty;
    }

}
