package org.openjdk.jmh.reconfigure.statistics.ci;

import org.openjdk.jmh.reconfigure.helper.HistogramItem;
import org.openjdk.jmh.reconfigure.statistics.Statistic;

import java.util.List;

import static org.openjdk.jmh.reconfigure.statistics.ReconfigureConstants.*;

public class CiPercentage implements Statistic {
    private List<HistogramItem> histogramList;

    public CiPercentage(List<HistogramItem> histogramList) {
        this.histogramList = histogramList;
    }

    @Override
    public double getValue() {
        CI ci = new CI(histogramList, CI_BOOTSTRAP_SIMULATIONS, CI_SIGNIFICANCE_LEVEL, CI_STATISTIC);
        ci.run();
        return (ci.getUpper() - ci.getLower()) / ci.getStatisticMetric();
    }
}
