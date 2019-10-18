package org.openjdk.jmh.reconfigure;

import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.results.IterationResult;

import java.util.*;

public abstract class ReconfigureManager {
    protected final BenchmarkParams benchParams;

    protected Map<Integer, List<HistogramItem>> warmupHistogram = new HashMap<>();
    protected Map<Integer, List<HistogramItem>> measurementHistogram = new HashMap<>();

    protected List<Double> warmupThresholds = new ArrayList<>();
    protected List<Double> measurementThresholds = new ArrayList<>();

    protected ReconfigureManager(BenchmarkParams benchParams) {
        this.benchParams = benchParams;
    }

    protected List<HistogramItem> toHistogramItems(int fork, int iteration, IterationResult ir) {
        List<HistogramItem> list = new ArrayList<>();
        Iterator<Map.Entry<Double, Long>> iterator = ir.getPrimaryResult().getStatistics().getRawData();
        while (iterator.hasNext()) {
            Map.Entry<Double, Long> entry = iterator.next();
            list.add(new HistogramItem(fork, iteration, entry.getKey(), entry.getValue()));
        }
        return list;
    }

    public List<Double> getWarmupThresholds() {
        return warmupThresholds;
    }

    public List<Double> getMeasurementThresholds() {
        return measurementThresholds;
    }
}
