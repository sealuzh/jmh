package org.openjdk.jmh.reconfigure.statistics.evaluation;

import org.openjdk.jmh.reconfigure.helper.HistogramItem;
import org.openjdk.jmh.reconfigure.statistics.Sampler;
import org.openjdk.jmh.reconfigure.statistics.ci.CiPercentage;

import java.util.*;

public class CiPercentageEvaluation implements StatisticalEvaluation {
    private double threshold;

    private List<HistogramItem> allMeasurements = new ArrayList<>();
    private Map<Integer, List<HistogramItem>> sampleInIteration = new HashMap<>();
    private Map<Integer, Double> ciPercentagePerIteration = new HashMap<>();

    public CiPercentageEvaluation(double threshold) {
        this.threshold = threshold;
    }

    @Override
    public void addIteration(List<HistogramItem> list) {
        int iteration = sampleInIteration.size() + 1;
        allMeasurements.addAll(list);

        // TODO SIZE
        List<HistogramItem> sample = new Sampler(allMeasurements).getSample(10000);

        Collections.sort(sample, new Comparator<HistogramItem>() {

            @Override
            public int compare(final HistogramItem item1, final HistogramItem item2) {
                int diffFork = item1.getFork() - item2.getFork();
                if (diffFork > 0) {
                    return 1;
                } else if (diffFork < 0) {
                    return -1;
                } else {
                    int diffIteration = item1.getIteration() - item2.getIteration();
                    if (diffIteration > 0) {
                        return 1;
                    } else if (diffIteration < 0) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            }

        });
        sampleInIteration.put(iteration, sample);
    }

    @Override
    public double getThreshold() {
        return threshold;
    }

    @Override
    public Double calculateVariability() {
        if (sampleInIteration.size() < 5) {
            return null;
        } else {
            List<Double> deltas = new ArrayList<>();
            int currentIteration = sampleInIteration.size();
            double currentCiPercentage = getCiPercentageOfIteration(currentIteration);

            for (int i = 1; i <= 4; i++) {
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
    public boolean stableEnvironment(double value) {
        return value < threshold;
    }
}
