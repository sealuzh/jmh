package org.openjdk.jmh.reconfigure.statistics;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.openjdk.jmh.reconfigure.helper.HistogramHelper;
import org.openjdk.jmh.reconfigure.helper.HistogramItem;
import org.openjdk.jmh.reconfigure.helper.ListToArray;

import java.util.List;

public class COV implements Statistic {
    private List<Double> list;

    public COV(List<HistogramItem> list) {
        this.list = HistogramHelper.toArray(list);
    }

    @Override
    public double getValue() {
        double[] array = ListToArray.toPrimitive(list);
        DescriptiveStatistics ds = new DescriptiveStatistics(array);
        if (ds.getMean() == 0) {
            return 0;
        } else {
            return ds.getStandardDeviation() / ds.getMean();
        }
    }
}
