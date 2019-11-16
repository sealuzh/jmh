package org.openjdk.jmh.reconfigure.manager;

import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.reconfigure.helper.HistogramItem;
import org.openjdk.jmh.reconfigure.statistics.evaluation.StatisticalEvaluation;
import org.openjdk.jmh.results.IterationResult;
import org.openjdk.jmh.runner.format.OutputFormat;

import java.util.ArrayList;
import java.util.List;

public class ForkReconfigureManager extends ReconfigureManager {
    private StatisticalEvaluation measurementEvaluation;

    private List<Double> measurementThresholds = new ArrayList<>();

    public ForkReconfigureManager(BenchmarkParams benchParams, OutputFormat out) {
        super(benchParams, out);
        warmupEvaluation = StatisticalEvaluationFactory.get(benchParams);
        measurementEvaluation = StatisticalEvaluationFactory.get(benchParams);
    }

    public void addFork(boolean isWarmup, int fork, List<IterationResult> list) {
        List<HistogramItem> combined = new ArrayList<>();
        for (int iteration = 1; iteration <= list.size(); iteration++) {
            combined.addAll(toHistogramItems(fork, iteration, list.get(iteration - 1)));
        }

        if (isWarmup) {
            addWarmupFork(combined);
        } else {
            addMeasurementFork(combined);
        }
    }

    private void addWarmupFork(List<HistogramItem> list) {
        warmupEvaluation.addIteration(list);
    }

    private void addMeasurementFork(List<HistogramItem> list) {
        measurementEvaluation.addIteration(list);
    }

    public boolean checkForkThreshold(boolean isWarmup) {
        if (isWarmup) {
            return checkWarmupForkThreshold();
        } else {
            return checkMeasurementForkThreshold();
        }
    }

    private boolean checkWarmupForkThreshold() {
        int currentWarmupFork = warmupEvaluation.getIterationNumber();
        if (currentWarmupFork < benchParams.getMinWarmupForks()) {
            warmupThresholds.add(null);
            return false;
        } else {
            int maxForks = benchParams.getWarmupForks();
            double value = warmupEvaluation.calculateVariability();
            warmupThresholds.add(value);
            boolean result = value < warmupEvaluation.getThreshold();

            if (currentWarmupFork == maxForks && !result) {
                printWarning("warmup forks", warmupEvaluation.getThreshold(), value);
            } else if (currentWarmupFork < maxForks && result) {
                printInfo(currentWarmupFork, maxForks, "warmup forks", value, warmupEvaluation.getThreshold());
            }

            return result;
        }
    }

    private boolean checkMeasurementForkThreshold() {
        int currentMeasurementFork = measurementEvaluation.getIterationNumber();
        if (currentMeasurementFork < benchParams.getMinForks()) {
            measurementThresholds.add(null);
            return false;
        } else {
            int maxForks = benchParams.getForks();
            double value = measurementEvaluation.calculateVariability();
            measurementThresholds.add(value);
            boolean result = value < measurementEvaluation.getThreshold();

            if (currentMeasurementFork == maxForks && !result) {
                printWarning("measurement forks", measurementEvaluation.getThreshold(), value);
            } else if (currentMeasurementFork < maxForks && result) {
                printInfo(currentMeasurementFork, maxForks, "measurement forks", value, measurementEvaluation.getThreshold());
            }

            return result;
        }
    }

    public List<Double> getMeasurementThresholds() {
        return measurementThresholds;
    }
}
