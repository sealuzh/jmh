package org.openjdk.jmh.reconfigure;

public class HistogramItem {
    private final int fork;
    private final int iteration;
    private final double value;
    private final long count;

    public HistogramItem(int fork, int iteration, double value, long count) {
        this.fork = fork;
        this.iteration = iteration;
        this.value = value;
        this.count = count;
    }

    public int getFork() {
        return fork;
    }

    public int getIteration() {
        return iteration;
    }

    public double getValue() {
        return value;
    }

    public long getCount() {
        return count;
    }
}
