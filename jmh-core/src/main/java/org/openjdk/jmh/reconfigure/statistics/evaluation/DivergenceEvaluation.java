package org.openjdk.jmh.reconfigure.statistics.evaluation;

import org.openjdk.jmh.reconfigure.helper.HistogramHelper;
import org.openjdk.jmh.reconfigure.helper.HistogramItem;
import org.openjdk.jmh.reconfigure.helper.OutlierDetector;
import org.openjdk.jmh.reconfigure.statistics.Sampler;
import org.openjdk.jmh.reconfigure.statistics.divergence.Divergence;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.*;

import static org.openjdk.jmh.reconfigure.statistics.ReconfigureConstants.OUTLIER_FACTOR;
import static org.openjdk.jmh.reconfigure.statistics.ReconfigureConstants.SAMPLE_SIZE;

public class DivergenceEvaluation implements StatisticalEvaluation {
    private double threshold;
    private double historySize;

    private List<Double> allMeasurements = new ArrayList<>();
    private Map<Integer, List<Double>> sampleUntilIteration = new HashMap<>();
    private Map<Integer, Double> pValuePerIteration = new HashMap<>();

    private DivergenceEvaluation(double threshold, int historySize) {
        this.threshold = threshold;
        this.historySize = historySize;
        disableSystemErr();
    }

    public static DivergenceEvaluation getIterationInstance(double threshold) {
        return new DivergenceEvaluation(threshold, 6);
    }

    public static DivergenceEvaluation getForkInstance(double threshold) {
        return new DivergenceEvaluation(threshold, 2);
    }

    @Override
    public void addIteration(List<HistogramItem> list) {
        OutlierDetector od = new OutlierDetector(OUTLIER_FACTOR, list);
        od.run();
        List<HistogramItem> sample = new Sampler(od.getInlier()).getSample(SAMPLE_SIZE);

        int iteration = sampleUntilIteration.size() + 1;
        List<Double> newValues = HistogramHelper.toArray(sample);
        allMeasurements.addAll(newValues);

        List<Double> sampleUntil = getSample(allMeasurements);
        sampleUntilIteration.put(iteration, sampleUntil);
    }

    @Override
    public double getThreshold() {
        return threshold;
    }

    @Override
    public Double calculateVariability() {
        if (sampleUntilIteration.size() < historySize) {
            return null;
        } else {
            List<Double> pvalues = new ArrayList<>();
            int currentIteration = sampleUntilIteration.size();

            for (int i = 0; i <= historySize - 2; i++) {
                Double pvalue = getPValueOfIteration(currentIteration - i);
                pvalues.add(pvalue);
            }

            return pvalues.stream().mapToDouble(it -> it).average().orElse(0.0);
        }
    }

    public Double getPValueOfIteration(int iteration) {
        if (pValuePerIteration.get(iteration) == null) {
            List<Double> currentSample = sampleUntilIteration.get(iteration);
            List<Double> previousSample = sampleUntilIteration.get(iteration - 1);
            if (currentSample == null || previousSample == null) {
                return null;
            } else {
                double pValue = new Divergence(currentSample, previousSample).getValue();
                pValuePerIteration.put(iteration, pValue);
                return pValue;
            }
        } else {
            return pValuePerIteration.get(iteration);
        }
    }

    private List<Double> getSample(List<Double> list) {
        Random random = new Random();
        List<Double> sample = new ArrayList<>();

        for (int i = 0; i < SAMPLE_SIZE; i++) {
            Double d = list.get(random.nextInt(list.size()));
            sample.add(d);
        }

        Collections.sort(sample);
        return sample;
    }

    private void disableSystemErr() {
        System.setErr(new PrintStream(new OutputStream() {
            public void write(int b) {
            }
        }));
    }

    @Override
    public int getIterationNumber() {
        return sampleUntilIteration.size();
    }

    @Override
    public boolean stableEnvironment(Double value) {
        return value != null && value > threshold;
    }
}
