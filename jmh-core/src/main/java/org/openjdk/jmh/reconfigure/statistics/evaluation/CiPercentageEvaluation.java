package org.openjdk.jmh.reconfigure.statistics.evaluation;

import org.openjdk.jmh.reconfigure.helper.HistogramItem;
import org.openjdk.jmh.reconfigure.helper.OutlierDetector;
import org.openjdk.jmh.reconfigure.statistics.Sampler;
import org.openjdk.jmh.reconfigure.statistics.ci.CiPercentage;

import java.util.*;

import static org.openjdk.jmh.reconfigure.statistics.ReconfigureConstants.*;

public class CiPercentageEvaluation implements StatisticalEvaluation {
    private double threshold;
    private double historySize;

    private List<HistogramItem> allMeasurements = new ArrayList<>();
    private Map<Integer, List<HistogramItem>> sampleInIteration = new HashMap<>();
    private Map<Integer, Double> ciPercentagePerIteration = new HashMap<>();

    private CiPercentageEvaluation(double threshold, int historySize) {
        this.threshold = threshold;
        this.historySize = historySize;
    }

    public static CiPercentageEvaluation getIterationInstance(double threshold) {
        return new CiPercentageEvaluation(threshold, 5);
    }

    public static CiPercentageEvaluation getForkInstance(double threshold) {
        return new CiPercentageEvaluation(threshold, 2);
    }

    @Override
    public void addIteration(List<HistogramItem> list) {
        OutlierDetector od = new OutlierDetector(OUTLIER_FACTOR, list);
        od.run();
        List<HistogramItem> sample = new Sampler(od.getInlier()).getSample(SAMPLE_SIZE);

        int iteration = sampleInIteration.size() + 1;
        allMeasurements.addAll(sample);

        List<HistogramItem> sampleUntil = new Sampler(allMeasurements).getSample(SAMPLE_SIZE);
        sampleInIteration.put(iteration, sampleUntil);
    }

    @Override
    public double getThreshold() {
        return threshold;
    }

    @Override
    public Double calculateVariability() {
        if (sampleInIteration.size() < historySize) {
            return null;
        } else {
            List<Double> deltas = new ArrayList<>();
            int currentIteration = sampleInIteration.size();
            double currentCiPercentage = getCiPercentageOfIteration(currentIteration);

            for (int i = 1; i <= historySize - 1; i++) {
                double ciPercentage = getCiPercentageOfIteration(currentIteration - i);
                double delta = Math.abs(ciPercentage - currentCiPercentage);
                deltas.add(delta);
            }

            return Collections.max(deltas);
        }
    }

    public double getCiPercentageOfIteration(int iteration) {
        if (ciPercentagePerIteration.get(iteration) == null) {
            double ciPercentage = new CiPercentage(sampleInIteration.get(iteration)).getValue();
            ciPercentagePerIteration.put(iteration, ciPercentage);
            return ciPercentage;
        } else {
            return ciPercentagePerIteration.get(iteration);
        }
    }

    @Override
    public int getIterationNumber() {
        return sampleInIteration.size();
    }

    @Override
    public boolean stableEnvironment(Double value) {
        return value != null && value < threshold;
    }
}