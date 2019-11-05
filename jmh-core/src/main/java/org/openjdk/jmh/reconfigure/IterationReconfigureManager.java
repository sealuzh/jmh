package org.openjdk.jmh.reconfigure;

import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.reconfigure.helper.HistogramHelper;
import org.openjdk.jmh.reconfigure.statistics.StatisticalEvaluation;
import org.openjdk.jmh.reconfigure.statistics.StatisticalEvaluationFactory;
import org.openjdk.jmh.results.IterationResult;
import org.openjdk.jmh.runner.format.OutputFormat;

import java.util.List;

public class IterationReconfigureManager extends ReconfigureManager {
    private int currentWarmupIteration = 0;

    public IterationReconfigureManager(BenchmarkParams benchParams, OutputFormat out) {
        super(benchParams, out);
    }

    public void addWarmupIteration(int iteration, IterationResult ir) {
        currentWarmupIteration = iteration;
        warmupHistogram.put(iteration, toHistogramItems(0, iteration, ir));
    }

    public boolean checkWarmupIterationThreshold() {
        if (currentWarmupIteration < benchParams.getWarmup().getMinCount()) {
            warmupThresholds.add(null);
            return false;
        } else {
            int maxIterations = benchParams.getWarmup().getCount();

            List<HistogramItem> warmupList = HistogramHelper.toList(warmupHistogram);
            StatisticalEvaluation se = StatisticalEvaluationFactory.get(benchParams, warmupList);
            double value = se.getValue();
            warmupThresholds.add(value);
            boolean result = value < se.getThreshold();

            if (currentWarmupIteration == maxIterations && !result) {
                printWarning("warmup iterations", se.getThreshold(), value);
            } else if (currentWarmupIteration < maxIterations && result) {
                printInfo(currentWarmupIteration, maxIterations, "warmup iterations", value, se.getThreshold());
            }

            return result;
        }
    }
}
