package org.openjdk.jmh.reconfigure.manager;

import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.reconfigure.statistics.evaluation.CiPercentageEvaluation;
import org.openjdk.jmh.reconfigure.statistics.evaluation.CovEvaluation;
import org.openjdk.jmh.reconfigure.statistics.evaluation.DivergenceEvaluation;
import org.openjdk.jmh.reconfigure.statistics.evaluation.StatisticalEvaluation;

public class StatisticalEvaluationFactory {
    public static StatisticalEvaluation get(BenchmarkParams benchParams) {
        switch (benchParams.getReconfigureMode()) {
            case CI:
                return new CiPercentageEvaluation(benchParams.getReconfigureCiThreshold());
            case COV:
                return new CovEvaluation(benchParams.getReconfigureCovThreshold());
            case DIVERGENCE:
                return new DivergenceEvaluation(benchParams.getReconfigureKldThreshold());
            default:
                throw new IllegalArgumentException("Reconfigure Mode is nod valid");
        }
    }
}
