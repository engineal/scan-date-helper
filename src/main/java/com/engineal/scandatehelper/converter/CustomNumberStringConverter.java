package com.engineal.scandatehelper.converter;

import javafx.util.converter.NumberStringConverter;

import java.text.NumberFormat;
import java.util.Locale;

public class CustomNumberStringConverter extends NumberStringConverter {
    public CustomNumberStringConverter() {
        this(Locale.getDefault());
    }

    public CustomNumberStringConverter(Locale locale) {
        super(NumberFormat.getIntegerInstance(locale));
        getNumberFormat().setGroupingUsed(false);
    }

    @Override
    public String toString(Number value) {
        if (value == null || value.intValue() == 0) {
            return "";
        }

        return super.toString(value);
    }
}
