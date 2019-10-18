package org.openjdk.jmh.reconfigure.helper;

import org.apache.commons.math3.util.Pair;
import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.results.BenchmarkResult;
import org.openjdk.jmh.util.Multimap;

import java.util.List;

public class BenchmarkMetaData {
    private Multimap<BenchmarkParams, BenchmarkResult> results;
    private List<Double> warmupThresholds;
    private List<Double> measurementThresholds;
    private boolean atLeastOneWarning = false;

    public BenchmarkMetaData() {
    }

    public BenchmarkMetaData(Multimap<BenchmarkParams, BenchmarkResult> results, List<Double> warmupThresholds, List<Double> measurementThresholds, boolean atLeastOneWarning) {
        this.results = results;
        this.warmupThresholds = warmupThresholds;
        this.measurementThresholds = measurementThresholds;
        this.atLeastOneWarning = atLeastOneWarning;
    }

    public Multimap<BenchmarkParams, BenchmarkResult> getResults() {
        return results;
    }

    public Pair<List<Double>, List<Double>> getThresholdsPair() {
        return new Pair<List<Double>, List<Double>>(warmupThresholds, measurementThresholds);
    }

    public boolean hasAtLeastOneWarning() {
        return atLeastOneWarning;
    }
}
