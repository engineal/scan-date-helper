package com.engineal.scandatehelper.converter;

import javafx.util.StringConverter;

import java.time.Month;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.util.Locale;

import static java.time.temporal.ChronoField.MONTH_OF_YEAR;

public class MonthStringConverter extends StringConverter<Month> {

    private final TextStyle style = TextStyle.FULL_STANDALONE;
    private final Locale locale;

    public MonthStringConverter() {
        this(Locale.getDefault());
    }

    public MonthStringConverter(Locale locale) {
        this.locale = locale;
    }

    @Override
    public String toString(Month value) {
        if (value == null) {
            return "";
        }
        return new DateTimeFormatterBuilder().appendText(MONTH_OF_YEAR, style).toFormatter(locale).format(value);
    }

    @Override
    public Month fromString(String value) {
        if (value == null) {
            return null;
        }
        return new DateTimeFormatterBuilder().appendText(MONTH_OF_YEAR, style).toFormatter(locale).parse(value, Month::from);
    }
}
