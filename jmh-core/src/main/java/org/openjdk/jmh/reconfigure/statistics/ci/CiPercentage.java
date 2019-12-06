package org.openjdk.jmh.reconfigure.statistics.ci;

import org.openjdk.jmh.reconfigure.helper.HistogramItem;
import org.openjdk.jmh.reconfigure.statistics.Statistic;

import java.util.List;

import static org.openjdk.jmh.reconfigure.statistics.ReconfigureConstants.*;

public class CiPercentage implements Statistic {
    private List<HistogramItem> histogramList;
    private int ciBootstrapSimulations = CI_BOOTSTRAP_SIMULATIONS;
    private double ciSignificanceLevel = CI_SIGNIFICANCE_LEVEL;
    private String ciStatistics = CI_STATISTIC;
    private int ciInvocationSamples = CI_INVOCATION_SAMPLES;

    public CiPercentage(List<HistogramItem> histogramList) {
        this.histogramList = histogramList;
    }

    public CiPercentage(List<HistogramItem> histogramList, int ciBootstrapSimulations) {
        this.histogramList = histogramList;
        this.ciBootstrapSimulations = ciBootstrapSimulations;
    }

    @Override
    public double getValue() {
        CI ci = new CI(histogramList, ciBootstrapSimulations, ciSignificanceLevel, ciStatistics, ciInvocationSamples);
        ci.run();
        return (ci.getUpper() - ci.getLower()) / ci.getStatisticMetric();
    }
}
