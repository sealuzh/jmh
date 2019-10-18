package org.openjdk.jmh.reconfigure.helper;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.ArrayList;
import java.util.List;

public class OutlierDetector {
    private double outlierFactor;
    private double[] input;

    private double q1;
    private double q3;
    private double iqr;
    private double min;
    private double max;

    private List<Double> outlier = new ArrayList<>();
    private List<Double> inlier = new ArrayList<>();

    public OutlierDetector(double outlierFactor, List<Double> input) {
        this.outlierFactor = outlierFactor;
        this.input = ListToArray.toPrimitive(input);
    }

    public void run() {
        DescriptiveStatistics ds = new DescriptiveStatistics(input);
        q1 = ds.getPercentile(25.0);
        q3 = ds.getPercentile(75.0);
        iqr = q3 - q1;

        max = q3 + outlierFactor * iqr;
        min = q1 - outlierFactor * iqr;

        for (int i = 0; i < input.length; i++) {
            double value = input[i];

            if (value <= max && value >= min) {
                inlier.add(value);
            } else {
                outlier.add(value);
            }
        }
    }

    public double getQ1() {
        return q1;
    }

    public double getQ3() {
        return q3;
    }

    public double getIqr() {
        return iqr;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public List<Double> getOutlier() {
        return outlier;
    }

    public List<Double> getInlier() {
        return inlier;
    }
}
