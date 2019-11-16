package org.openjdk.jmh.reconfigure.statistics.divergence;

public class KullbackLeiblerDivergence {
    public static double continuous(double[] x, double[] y, double width) {
        boolean intersection = false;
        double kl = 0.0;

        for (int i = 0; i < x.length; i++) {
            if (x[i] != 0.0 && y[i] != 0.0) {
                intersection = true;
                kl += x[i] * Math.log(x[i] / y[i]) * width;
            }
        }

        if (intersection) {
            return kl;
        } else {
            return Double.POSITIVE_INFINITY;
        }
    }
}
