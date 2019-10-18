package org.openjdk.jmh.reconfigure;

import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.reconfigure.helper.HistogramHelper;
import org.openjdk.jmh.reconfigure.statistics.EvaluationType;
import org.openjdk.jmh.reconfigure.statistics.StatisticalEvaluation;
import org.openjdk.jmh.reconfigure.statistics.StatisticalEvaluationFactory;
import org.openjdk.jmh.results.IterationResult;

import java.util.List;

public class IterationReconfigureManager extends ReconfigureManager {
    private int currentWarmupIteration = 0;
    private int currentMeasurementIteration = 0;

    public IterationReconfigureManager(BenchmarkParams benchParams) {
        super(benchParams);
    }

    public void addWarmupIteration(int iteration, IterationResult ir) {
        currentWarmupIteration = iteration;
        warmupHistogram.put(iteration, toHistogramItems(0, iteration, ir));
    }

    public void addMeasurementIteration(int iteration, IterationResult ir) {
        currentMeasurementIteration = iteration;
        measurementHistogram.put(iteration, toHistogramItems(0, iteration, ir));
    }

    public boolean checkWarmupIterationThreshold() {
        if (currentWarmupIteration < benchParams.getWarmup().getMinCount()) {
            warmupThresholds.add(null);
            return false;
        } else {
            List<HistogramItem> warmupList = HistogramHelper.toList(warmupHistogram);
            StatisticalEvaluation se = StatisticalEvaluationFactory.get(benchParams, warmupList, EvaluationType.WARMUP_ITERATION);
            double value = se.getValue();
            warmupThresholds.add(value);
            return value < se.getThreshold();
        }
    }

    public boolean checkMeasurementIterationThreshold() {
        if (currentMeasurementIteration < benchParams.getMeasurement().getMinCount()) {
            measurementThresholds.add(null);
            return false;
        } else {
            List<HistogramItem> measurementList = HistogramHelper.toList(measurementHistogram);
            StatisticalEvaluation se = StatisticalEvaluationFactory.get(benchParams, measurementList, EvaluationType.MEASUREMENT_ITERATION);
            double value = se.getValue();
            measurementThresholds.add(value);
            return value < se.getThreshold();
        }
    }
}
