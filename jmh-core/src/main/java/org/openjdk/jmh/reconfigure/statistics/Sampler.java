package org.openjdk.jmh.reconfigure.statistics;

import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;
import org.openjdk.jmh.reconfigure.helper.HistogramItem;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Sampler {
    private List<HistogramItem> items;

    public Sampler(List<HistogramItem> items) {
        this.items = items;
    }

    public List<HistogramItem> getSample(int size) {
        HistogramItem[] output = new HistogramItem[size];
        List<Pair<HistogramItem, Double>> distributionPairs = items.parallelStream().map(it -> new Pair<>(it.single(), (double) it.getCount())).collect(Collectors.toList());
        EnumeratedDistribution ed = new EnumeratedDistribution<>(distributionPairs);
        ed.sample(size, output);
        return Arrays.asList(output);
    }
}
