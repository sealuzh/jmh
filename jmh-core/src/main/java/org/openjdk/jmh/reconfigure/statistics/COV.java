package org.openjdk.jmh.reconfigure.statistics;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.openjdk.jmh.reconfigure.HistogramItem;
import org.openjdk.jmh.reconfigure.helper.HistogramHelper;
import org.openjdk.jmh.reconfigure.helper.ListToArray;
import org.openjdk.jmh.reconfigure.helper.OutlierDetector;

import java.util.List;

class COV implements StatisticalEvaluation {
    private List<Double> list;
    private double threshold;

    public COV(List<HistogramItem> list, double threshold) {
        this.list = HistogramHelper.toArray(list);
        this.threshold = threshold;
    }

    @Override
    public double getValue() {
        OutlierDetector od = new OutlierDetector(10.0, list);
        od.run();
        return calculate(od.getInlier());
    }

    double calculate(List<Double> input) {
        double[] array = ListToArray.toPrimitive(input);
        DescriptiveStatistics ds = new DescriptiveStatistics(array);
        return ds.getStandardDeviation() / ds.getMean();
    }

    @Override
    public double getThreshold() {
        return threshold;
    }
}
