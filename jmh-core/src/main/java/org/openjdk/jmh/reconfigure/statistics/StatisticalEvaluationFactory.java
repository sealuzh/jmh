package org.openjdk.jmh.reconfigure.statistics;

import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.reconfigure.HistogramItem;

import java.util.List;

public class StatisticalEvaluationFactory {
    public static StatisticalEvaluation get(BenchmarkParams benchParams, List<HistogramItem> list, EvaluationType type) {
        // TODO threshold
        return new COV(list, 0.05);
    }
}
