package org.openjdk.jmh.reconfigure.statistics.ci;

import org.apache.commons.io.IOUtils;
import org.openjdk.jmh.reconfigure.helper.HistogramItem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

public class CI {
    protected List<HistogramItem> histogramList;

    protected String paToolPath;
    protected final int bootstrapSimulations;
    protected final double significanceLevel;
    protected final String statistic;
    protected final int ciInvocationSamples;

    protected double lower;
    protected double upper;
    protected double statisticMetric;

    public CI(List<HistogramItem> histogramList, int bootstrapSimulations, double significanceLevel, String statistic, int ciInvocationSamples) {
        this.histogramList = histogramList;
        this.bootstrapSimulations = bootstrapSimulations;
        this.significanceLevel = significanceLevel;
        this.statistic = statistic;
        this.ciInvocationSamples = ciInvocationSamples;
        executable();
    }

    private void executable() {
        paToolPath = CIHelper.getInstance().getPath();
    }

    public void run() {
        String file = getTmpFile(histogramList);
        try {
            Process process = Runtime.getRuntime().exec(paToolPath + " -om -bs " + bootstrapSimulations + " -is " + ciInvocationSamples + " -sig " + significanceLevel + " -st " + statistic + " " + file);
            String inputString = IOUtils.toString(process.getInputStream(), Charset.defaultCharset());
            String errorString = IOUtils.toString(process.getErrorStream(), Charset.defaultCharset());
            String output = (inputString + "\n" + errorString).trim();
            String line = getFirstLine(output);
            String[] parts = line.split(";");
            statisticMetric = Double.parseDouble(parts[3]);
            lower = Double.parseDouble(parts[4]);
            upper = Double.parseDouble(parts[5]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected String getFirstLine(String input) {
        String[] lines = input.split("\n");

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();

            if (!line.startsWith("#") && !line.isEmpty()) {
                return line;
            }
        }

        return null;
    }

    protected String getTmpFile(List<HistogramItem> list) {
        try {
            File tmpFile = File.createTempFile("reconfigure", ".csv");
            FileWriter fw = new FileWriter(tmpFile);

            for (int i = 0; i < list.size(); i++) {
                HistogramItem hi = list.get(i);
                fw.append(";;;;;0;" + hi.getFork() + ";" + hi.getIteration() + ";;;" + hi.getCount() + ";" + hi.getValue() + "\n");
            }

            fw.flush();
            return tmpFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public double getLower() {
        return lower;
    }

    public double getUpper() {
        return upper;
    }

    public double getStatisticMetric() {
        return statisticMetric;
    }
}
