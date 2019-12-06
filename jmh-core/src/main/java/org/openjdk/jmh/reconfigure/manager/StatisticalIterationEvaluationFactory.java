package org.openjdk.jmh.reconfigure.manager;

import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.reconfigure.statistics.evaluation.CiPercentageEvaluation;
import org.openjdk.jmh.reconfigure.statistics.evaluation.CovEvaluation;
import org.openjdk.jmh.reconfigure.statistics.evaluation.DivergenceEvaluation;
import org.openjdk.jmh.reconfigure.statistics.evaluation.StatisticalEvaluation;

public class StatisticalIterationEvaluationFactory {
    public static StatisticalEvaluation get(BenchmarkParams benchParams) {
        switch (benchParams.getReconfigureMode()) {
            case CI:
                return CiPercentageEvaluation.getIterationInstance(benchParams.getReconfigureCiThreshold());
            case COV:
                return CovEvaluation.getIterationInstance(benchParams.getReconfigureCovThreshold());
            case DIVERGENCE:
                return DivergenceEvaluation.getIterationInstance(benchParams.getReconfigureKldThreshold());
            default:
                throw new IllegalArgumentException("Reconfigure Mode is nod valid");
        }
    }
}
