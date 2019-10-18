package org.openjdk.jmh.reconfigure;

import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.reconfigure.helper.HistogramHelper;
import org.openjdk.jmh.reconfigure.statistics.EvaluationType;
import org.openjdk.jmh.reconfigure.statistics.StatisticalEvaluation;
import org.openjdk.jmh.reconfigure.statistics.StatisticalEvaluationFactory;
import org.openjdk.jmh.results.IterationResult;
import org.openjdk.jmh.runner.format.OutputFormat;

import java.util.ArrayList;
import java.util.List;

public class ForkReconfigureManager extends ReconfigureManager {
    private int currentWarmupFork = 0;
    private int currentMeasurementFork = 0;

    public ForkReconfigureManager(BenchmarkParams benchParams, OutputFormat out) {
        super(benchParams, out);
    }

    public void addFork(boolean isWarmup, int fork, List<IterationResult> list) {
        List<HistogramItem> combined = new ArrayList<>();
        for (int iteration = 1; iteration <= list.size(); iteration++) {
            combined.addAll(toHistogramItems(fork, iteration, list.get(iteration - 1)));
        }

        if (isWarmup) {
            addWarmupFork(fork, combined);
        } else {
            addMeasurementFork(fork, combined);
        }
    }

    private void addWarmupFork(int fork, List<HistogramItem> list) {
        currentWarmupFork = fork;
        warmupHistogram.put(fork, list);
    }

    private void addMeasurementFork(int fork, List<HistogramItem> list) {
        currentMeasurementFork = fork;
        measurementHistogram.put(fork, list);
    }


    public boolean checkForkThreshold(boolean isWarmup) {
        if (isWarmup) {
            return checkWarmupForkThreshold();
        } else {
            return checkMeasurementForkThreshold();
        }
    }

    private boolean checkWarmupForkThreshold() {
        if (currentWarmupFork < benchParams.getMinWarmupForks()) {
            warmupThresholds.add(null);
            return false;
        } else {
            int maxForks = benchParams.getWarmupForks();

            List<HistogramItem> warmupList = HistogramHelper.toList(warmupHistogram);
            StatisticalEvaluation se = StatisticalEvaluationFactory.get(benchParams, warmupList, EvaluationType.WARMUP_FORK);
            double value = se.getValue();
            warmupThresholds.add(value);

            boolean result = value < se.getThreshold();

            if (currentWarmupFork == maxForks && !result) {
                printWarning("warmup forks", se.getThreshold(), value);
            } else if (currentWarmupFork < maxForks && result) {
                printInfo(currentWarmupFork, maxForks, "warmup forks", value, se.getThreshold());
            }

            return result;
        }
    }

    private boolean checkMeasurementForkThreshold() {
        if (currentMeasurementFork < benchParams.getMinForks()) {
            measurementThresholds.add(null);
            return false;
        } else {
            int maxForks = benchParams.getForks();

            List<HistogramItem> measurementList = HistogramHelper.toList(measurementHistogram);
            StatisticalEvaluation se = StatisticalEvaluationFactory.get(benchParams, measurementList, EvaluationType.MEASUREMENT_FORK);
            double value = se.getValue();
            measurementThresholds.add(value);

            boolean result = value < se.getThreshold();

            if (currentMeasurementFork == maxForks && !result) {
                printWarning("measurement forks", se.getThreshold(), value);
            } else if (currentMeasurementFork < maxForks && result) {
                printInfo(currentMeasurementFork, maxForks, "measurement forks", value, se.getThreshold());
            }

            return result;
        }
    }
}
