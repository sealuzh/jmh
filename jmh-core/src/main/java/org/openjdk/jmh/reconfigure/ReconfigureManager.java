package org.openjdk.jmh.reconfigure;

import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.results.IterationResult;
import org.openjdk.jmh.runner.format.OutputFormat;

import java.util.*;

public abstract class ReconfigureManager {
    protected final BenchmarkParams benchParams;
    private final OutputFormat out;

    protected Map<Integer, List<HistogramItem>> warmupHistogram = new HashMap<>();
    protected Map<Integer, List<HistogramItem>> measurementHistogram = new HashMap<>();

    protected List<Double> warmupThresholds = new ArrayList<>();
    protected List<Double> measurementThresholds = new ArrayList<>();

    private boolean atLeastOneWarning = false;

    public ReconfigureManager(BenchmarkParams benchParams, OutputFormat out) {
        this.benchParams = benchParams;
        this.out = out;
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

    protected void printWarning(String type, double threshold, double value) {
        out.println("");
        out.println("##########");
        out.println(String.format("# WARNING: Maximum number of %s was reached but statistical variability threshold of %.4f is not achieved with current value of %.4f", type, threshold, value));
        out.println("##########");
        out.println("");
        atLeastOneWarning = true;
    }

    protected void printInfo(int currentNumber, int maxNumber, String type, double value, double threshold) {
        out.println("");
        out.println(String.format("# Data collection is stopped after %d of %d %s because value of %.4f is less than threshold %.4f", currentNumber, maxNumber, type, value, threshold));
        out.println("");
    }

    public List<Double> getWarmupThresholds() {
        return warmupThresholds;
    }

    public List<Double> getMeasurementThresholds() {
        return measurementThresholds;
    }

    public boolean hasAtLeastOneWarning() {
        return atLeastOneWarning;
    }
}
