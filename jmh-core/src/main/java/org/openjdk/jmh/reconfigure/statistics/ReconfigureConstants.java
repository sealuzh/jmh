package org.openjdk.jmh.reconfigure.statistics;

public class ReconfigureConstants {
    public static final double OUTLIER_FACTOR = 10.0;
    public static final double RANGE_OUTLIER_FACTOR = 1.5;
    public static final int SAMPLE_SIZE = 1000;
    public static final int DIVERGENCE_NUMBER_OF_POINTS = 1000;
    public static final int CI_BOOTSTRAP_SIMULATIONS = 1000;
    public static final double CI_SIGNIFICANCE_LEVEL = 0.01;
    public static final String CI_STATISTIC = "mean";
    public static final int CI_INVOCATION_SAMPLES = -1;
    public static final int CI_SAMPLE_SIZE = 10000;
}
