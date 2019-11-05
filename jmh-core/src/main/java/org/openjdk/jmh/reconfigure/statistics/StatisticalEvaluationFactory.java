package org.openjdk.jmh.reconfigure.statistics;

import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.reconfigure.HistogramItem;

import java.util.List;

public class StatisticalEvaluationFactory {
    public static StatisticalEvaluation get(BenchmarkParams benchParams, List<HistogramItem> list) {
        switch (benchParams.getReconfigureMode()){
//            case CI:
//                return new CI(list, benchParams.getReconfigureCiThreshold());
            case COV:
                return new COV(list, benchParams.getReconfigureCovThreshold());
//            case DIVERGENCE:
//                return new Kld(list, benchParams.getReconfigureKldThreshold());
            default:
                throw new IllegalArgumentException("Reconfigure Mode is nod valid");
        }
    }
}
