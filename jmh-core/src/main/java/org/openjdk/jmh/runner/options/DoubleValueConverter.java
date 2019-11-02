package org.openjdk.jmh.runner.options;

import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;
import joptsimple.internal.Reflection;

/**
 * Converts option value from {@link String} to {@link Double} and makes sure the value exceeds given minimal and maximal threshold.
 */
public class DoubleValueConverter implements ValueConverter<Double> {
    private final static ValueConverter<Double> TO_DOUBLE_CONVERTER = Reflection.findConverter(double.class);

    public final static DoubleValueConverter PROBABILITY = new DoubleValueConverter(0, 1);
    public final static DoubleValueConverter NON_NEGATIVE = new DoubleValueConverter(0, Double.MAX_VALUE);

    private final double minValue;
    private final double maxValue;

    public DoubleValueConverter(double minValue, double maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public Double convert(String value) {
        Double newValue = TO_DOUBLE_CONVERTER.convert(value);
        if (newValue == null) {
            // should not get here
            throw new ValueConversionException("value should not be null");
        }

        if (newValue < minValue || newValue > maxValue) {
            String message = "The given value " + value + " should be greater or equal than " + minValue + " and less or equal than " + maxValue;
            throw new ValueConversionException(message);
        }
        return newValue;
    }

    @Override
    public Class<Double> valueType() {
        return TO_DOUBLE_CONVERTER.valueType();
    }

    @Override
    public String valuePattern() {
        return "double";
    }
}
