package org.openjdk.jmh.reconfigure.statistics.evaluation;

import org.openjdk.jmh.reconfigure.helper.HistogramItem;

import java.util.List;

public interface StatisticalEvaluation {
    void addIteration(List<HistogramItem> list);

    double getThreshold();

    Double calculateVariability();

    int getIterationNumber();

    boolean stableEnvironment(Double value);
}
