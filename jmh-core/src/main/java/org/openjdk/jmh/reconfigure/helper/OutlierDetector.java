package org.openjdk.jmh.reconfigure.helper;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.ArrayList;
import java.util.List;

public class OutlierDetector {
    private double outlierFactor;
    private List<HistogramItem> input;
    private double[] inputRaw;

    private double q1;
    private double q3;
    private double iqr;
    private double min;
    private double max;

    private List<HistogramItem> outlier = new ArrayList<>();
    private List<HistogramItem> inlier = new ArrayList<>();

    public OutlierDetector(double outlierFactor, List<HistogramItem> input) {
        this.outlierFactor = outlierFactor;
        this.input = input;
        this.inputRaw = ListToArray.toPrimitive(HistogramHelper.toArray(input));
    }

    public void run() {
        DescriptiveStatistics ds = new DescriptiveStatistics(inputRaw);
        q1 = ds.getPercentile(25.0);
        q3 = ds.getPercentile(75.0);
        iqr = q3 - q1;

        max = q3 + outlierFactor * iqr;
        min = q1 - outlierFactor * iqr;

        for (int i = 0; i < input.size(); i++) {
            double value = input.get(i).getValue();

            if (value <= max && value >= min) {
                inlier.add(input.get(i));
            } else {
                outlier.add(input.get(i));
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

    public List<HistogramItem> getOutlier() {
        return outlier;
    }

    public List<HistogramItem> getInlier() {
        return inlier;
    }
}
