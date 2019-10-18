package org.openjdk.jmh.reconfigure.helper;

import java.util.List;

public class ListToArray {
    private static double[] toPrimitive(Double[] array) {
        final double[] result = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i];
        }
        return result;
    }

    public static double[] toPrimitive(List<Double> input) {
        return toPrimitive(input.toArray(new Double[0]));
    }
}
