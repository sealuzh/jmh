package org.openjdk.jmh.reconfigure.statistics.evaluation;

import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;
import org.openjdk.jmh.reconfigure.helper.HistogramHelper;
import org.openjdk.jmh.reconfigure.helper.HistogramItem;
import org.openjdk.jmh.reconfigure.statistics.divergence.Divergence;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;

import static org.openjdk.jmh.reconfigure.statistics.ReconfigureConstants.SAMPLE_SIZE;

public class DivergenceEvaluation implements StatisticalEvaluation {
    private double threshold;

    private List<Double> allMeasurements = new ArrayList<>();
    private Map<Integer, List<Double>> sampleUntilIteration = new HashMap<>();
    private Map<Integer, Double> pValuePerIteration = new HashMap<>();

    public DivergenceEvaluation(double threshold) {
        this.threshold = threshold;
        disableSystemErr();
    }

    @Override
    public void addIteration(List<HistogramItem> list) {
        int iteration = sampleUntilIteration.size() + 1;
        List<Double> newValues = HistogramHelper.toArray(list);
        allMeasurements.addAll(newValues);

        List<Double> sample = getSample(allMeasurements);
        sampleUntilIteration.put(iteration, sample);
    }

    @Override
    public double getThreshold() {
        return threshold;
    }

    @Override
    public Double calculateVariability() {
        if (sampleUntilIteration.size() < 6) {
            return null;
        } else {
            List<Double> pvalues = new ArrayList<>();
            int currentIteration = sampleUntilIteration.size();

            for (int i = 0; i <= 4; i++) {
                Double pvalue = getPValueOfIteration(currentIteration - i);
                pvalues.add(pvalue);
            }

            return Collections.min(pvalues);
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
        ArrayList<Pair<Double, Double>> distributionPairs = list.stream().map(it -> new Pair<Double, Double>(it, 1.0)).collect(Collectors.toCollection(ArrayList<Pair<Double, Double>>::new));
        EnumeratedDistribution ed = new EnumeratedDistribution<Double>(distributionPairs);
        List<Double> sample = new ArrayList<Double>((List<Double>) (List<?>) Arrays.asList(ed.sample(SAMPLE_SIZE)));
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
}
