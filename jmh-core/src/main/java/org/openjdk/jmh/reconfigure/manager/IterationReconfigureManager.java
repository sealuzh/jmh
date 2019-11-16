package org.openjdk.jmh.reconfigure.manager;

import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.results.IterationResult;
import org.openjdk.jmh.runner.format.OutputFormat;

public class IterationReconfigureManager extends ReconfigureManager {
    public IterationReconfigureManager(BenchmarkParams benchParams, OutputFormat out) {
        super(benchParams, out);
        warmupEvaluation = StatisticalEvaluationFactory.get(benchParams);
    }

    public void addWarmupIteration(int iteration, IterationResult ir) {
        warmupEvaluation.addIteration(toHistogramItems(0, iteration, ir));
    }

    public boolean checkWarmupIterationThreshold() {
        int currentWarmupIteration = warmupEvaluation.getIterationNumber();
        if (currentWarmupIteration < benchParams.getWarmup().getMinCount()) {
            warmupThresholds.add(null);
            return false;
        } else {
            int maxIterations = benchParams.getWarmup().getCount();
            double value = warmupEvaluation.calculateVariability();
            warmupThresholds.add(value);
            boolean result = value < warmupEvaluation.getThreshold();

            if (currentWarmupIteration == maxIterations && !result) {
                printWarning("warmup iterations", warmupEvaluation.getThreshold(), value);
            } else if (currentWarmupIteration < maxIterations && result) {
                printInfo(currentWarmupIteration, maxIterations, "warmup iterations", value, warmupEvaluation.getThreshold());
            }

            return result;
        }
    }
}
