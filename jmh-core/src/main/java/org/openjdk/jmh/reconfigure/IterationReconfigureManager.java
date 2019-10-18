package org.openjdk.jmh.reconfigure;

import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.reconfigure.helper.HistogramHelper;
import org.openjdk.jmh.reconfigure.statistics.EvaluationType;
import org.openjdk.jmh.reconfigure.statistics.StatisticalEvaluation;
import org.openjdk.jmh.reconfigure.statistics.StatisticalEvaluationFactory;
import org.openjdk.jmh.results.IterationResult;

import java.util.*;

public class IterationReconfigureManager {
    private final BenchmarkParams benchParams;
    private int currentWarmupIteration = 0;
    private int currentMeasurementIteration = 0;

    private Map<Integer, List<HistogramItem>> warmupHistogram = new HashMap<>();
    private Map<Integer, List<HistogramItem>> measurementHistogram = new HashMap<>();

    private List<Double> warmupThresholds = new ArrayList<>();
    private List<Double> measurementThresholds = new ArrayList<>();

    public IterationReconfigureManager(BenchmarkParams benchParams) {
        this.benchParams = benchParams;
    }

    public void addWarmupIteration(int iteration, IterationResult ir) {
        currentWarmupIteration = iteration;
        warmupHistogram.put(iteration, toHistogramItems(iteration, ir));
    }

    public void addMeasurementIteration(int iteration, IterationResult ir) {
        currentMeasurementIteration = iteration;
        measurementHistogram.put(iteration, toHistogramItems(iteration, ir));
    }

    private List<HistogramItem> toHistogramItems(int iteration, IterationResult ir) {
        List<HistogramItem> list = new ArrayList<>();
        Iterator<Map.Entry<Double, Long>> iterator = ir.getPrimaryResult().getStatistics().getRawData();
        while (iterator.hasNext()) {
            Map.Entry<Double, Long> entry = iterator.next();
            list.add(new HistogramItem(0, iteration, entry.getKey(), entry.getValue()));
        }
        return list;
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

    public List<Double> getWarmupThresholds() {
        return warmupThresholds;
    }

    public List<Double> getMeasurementThresholds() {
        return measurementThresholds;
    }
}
