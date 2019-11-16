package org.openjdk.jmh.reconfigure.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HistogramHelper {
    public static List<HistogramItem> toList(Map<Integer, List<HistogramItem>> map) {
        List<HistogramItem> list = new ArrayList<>();
        for (Integer key : map.keySet()) {
            list.addAll(map.get(key));
        }

        return list;
    }

    public static List<Double> toArray(List<HistogramItem> input) {
        List<Double> out = new ArrayList<>();

        for (int i = 0; i < input.size(); i++) {
            HistogramItem item = input.get(i);

            for (int j = 0; j < item.getCount(); j++) {
                out.add(item.getValue());
            }
        }

        return out;
    }
}
