package org.openjdk.jmh.reconfigure.manager;

import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.reconfigure.statistics.evaluation.CiPercentageEvaluation;
import org.openjdk.jmh.reconfigure.statistics.evaluation.CovEvaluation;
import org.openjdk.jmh.reconfigure.statistics.evaluation.DivergenceEvaluation;
import org.openjdk.jmh.reconfigure.statistics.evaluation.StatisticalEvaluation;

public class StatisticalForkEvaluationFactory {
    public static StatisticalEvaluation get(BenchmarkParams benchParams) {
        switch (benchParams.getReconfigureMode()) {
            case CI:
                return CiPercentageEvaluation.getForkInstance(benchParams.getReconfigureCiThreshold());
            case COV:
                return CovEvaluation.getForkInstance(benchParams.getReconfigureCovThreshold());
            case DIVERGENCE:
                return DivergenceEvaluation.getForkInstance(benchParams.getReconfigureKldThreshold());
            default:
                throw new IllegalArgumentException("Reconfigure Mode is nod valid");
        }
    }
}
