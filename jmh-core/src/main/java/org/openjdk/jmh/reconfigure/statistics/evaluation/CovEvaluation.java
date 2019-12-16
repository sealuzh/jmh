package org.openjdk.jmh.reconfigure.statistics.evaluation;

import org.openjdk.jmh.reconfigure.helper.HistogramItem;
import org.openjdk.jmh.reconfigure.helper.OutlierDetector;
import org.openjdk.jmh.reconfigure.statistics.COV;
import org.openjdk.jmh.reconfigure.statistics.Sampler;

import java.util.*;

import static org.openjdk.jmh.reconfigure.statistics.ReconfigureConstants.OUTLIER_FACTOR;
import static org.openjdk.jmh.reconfigure.statistics.ReconfigureConstants.SAMPLE_SIZE;

public class CovEvaluation implements StatisticalEvaluation {
    private double threshold;
    private double historySize;

    private Map<Integer, List<HistogramItem>> samplePerIteration = new HashMap<>();
    private Map<Integer, Double> covPerIteration = new HashMap<>();

    private CovEvaluation(double threshold, int historySize) {
        this.threshold = threshold;
        this.historySize = historySize;
    }

    public static CovEvaluation getIterationInstance(double threshold) {
        return new CovEvaluation(threshold, 5);
    }

    public static CovEvaluation getForkInstance(double threshold) {
        return new CovEvaluation(threshold, 2);
    }

    @Override
    public void addIteration(List<HistogramItem> list) {
        OutlierDetector od = new OutlierDetector(OUTLIER_FACTOR, list);
        od.run();
        List<HistogramItem> sample = new Sampler(od.getInlier()).getSample(SAMPLE_SIZE);

        int iteration = samplePerIteration.size() + 1;
        samplePerIteration.put(iteration, sample);
    }

    @Override
    public double getThreshold() {
        return threshold;
    }

    @Override
    public Double calculateVariability() {
        if (samplePerIteration.size() < historySize) {
            return null;
        } else {
            List<Double> deltas = new ArrayList<>();
            int currentIteration = samplePerIteration.size();
            double currentCov = getCovOfIteration(currentIteration);

            for (int i = 1; i <= historySize - 1; i++) {
                double cov = getCovOfIteration(currentIteration - i);
                double delta = Math.abs(cov - currentCov);
                deltas.add(delta);
            }

            return Collections.max(deltas);
        }
    }

    public double getCovOfIteration(int iteration) {
        if (covPerIteration.get(iteration) == null) {
            List<HistogramItem> all = new ArrayList<>();
            for (int i = 1; i <= iteration; i++) {
                all.addAll(samplePerIteration.get(i));
            }

            double cov = new COV(all).getValue();
            covPerIteration.put(iteration, cov);
            return cov;
        } else {
            return covPerIteration.get(iteration);
        }
    }

    @Override
    public int getIterationNumber() {
        return samplePerIteration.size();
    }

    @Override
    public boolean stableEnvironment(Double value) {
        return value != null && value < threshold;
    }
}
