package org.openjdk.jmh.reconfigure.statistics.divergence;

import org.apache.commons.math3.util.Pair;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.openjdk.jmh.reconfigure.helper.ListToArray;
import org.openjdk.jmh.reconfigure.statistics.Statistic;

import java.util.ArrayList;
import java.util.List;

import static org.openjdk.jmh.reconfigure.statistics.ReconfigureConstants.DIVERGENCE_NUMBER_OF_POINTS;
import static org.openjdk.jmh.reconfigure.statistics.ReconfigureConstants.RANGE_OUTLIER_FACTOR;

public class Divergence implements Statistic {
    private List<Double> before;
    private List<Double> after;

    public Divergence(List<Double> before, List<Double> after) {
        this.before = before;
        this.after = after;
    }

    @Override
    public double getValue() {
        Pair<Double, Double> range = getRange();
        double min = range.getFirst();
        double max = range.getSecond();

        if (min == max) {
            return 1.0;
        }

        List<Double> y = new ArrayList<>();
        double step = (max - min) / (DIVERGENCE_NUMBER_OF_POINTS - 1);
        for (int i = 0; i < DIVERGENCE_NUMBER_OF_POINTS; i++) {
            y.add(min + i * step);
        }

        double[] pdfBefore = ProbabilityDensityFunction.estimate(before, y);
        double[] pdfAfter = ProbabilityDensityFunction.estimate(after, y);

        double kldBefore = KullbackLeiblerDivergence.continuous(pdfBefore, pdfAfter, step);
        double kldAfter = KullbackLeiblerDivergence.continuous(pdfAfter, pdfBefore, step);

        return Math.pow(2.0, -kldBefore) * Math.pow(2.0, -kldAfter);
    }

    private Pair<Double, Double> getRange() {
        Pair<Double, Double> rangeBefore = getRangeDistribution(before);
        Pair<Double, Double> rangeAfter = getRangeDistribution(after);

        double min = Math.min(rangeBefore.getFirst(), rangeAfter.getFirst());
        double max = Math.max(rangeBefore.getSecond(), rangeAfter.getSecond());

        return new Pair<Double, Double>(min, max);
    }

    private Pair<Double, Double> getRangeDistribution(List<Double> list) {
        DescriptiveStatistics ds = new DescriptiveStatistics(ListToArray.toPrimitive(list));
        double q1 = ds.getPercentile(25.0);
        double q3 = ds.getPercentile(75.0);
        double iqr = q3 - q1;

        double max = q3 + RANGE_OUTLIER_FACTOR * iqr;
        double min = q1 - RANGE_OUTLIER_FACTOR * iqr;

        if (min < 0) {
            min = 0.0;
        }
        return new Pair<Double, Double>(min, max);
    }
}