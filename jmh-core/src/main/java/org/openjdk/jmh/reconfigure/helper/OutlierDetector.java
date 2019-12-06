package org.openjdk.jmh.reconfigure.helper;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.ArrayList;
import java.util.List;

public class OutlierDetector {
    private double outlierFactor;
    private List<HistogramItem> input;
    private double[] inputRaw;

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
        double median = ds.getPercentile(50.0);

        max = outlierFactor * median;
        min = median / outlierFactor;

        for (int i = 0; i < input.size(); i++) {
            double value = input.get(i).getValue();

            if (value <= max && value >= min) {
                inlier.add(input.get(i));
            } else {
                outlier.add(input.get(i));
            }
        }
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
