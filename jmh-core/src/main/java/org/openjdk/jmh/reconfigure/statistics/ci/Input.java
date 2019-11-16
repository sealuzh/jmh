package org.openjdk.jmh.reconfigure.statistics.ci;

import com.opencsv.bean.CsvBindByPosition;

public class Input {
    @CsvBindByPosition(position = 0)
    String project;
    @CsvBindByPosition(position = 1)
    String commit;
    @CsvBindByPosition(position = 2)
    String benchmark;
    @CsvBindByPosition(position = 3)
    String params;
    @CsvBindByPosition(position = 4)
    String instance;
    @CsvBindByPosition(position = 5)
    int trial;
    @CsvBindByPosition(position = 6)
    int fork;
    @CsvBindByPosition(position = 7)
    int iteration;
    @CsvBindByPosition(position = 8)
    String mode;
    @CsvBindByPosition(position = 9)
    String unit;
    @CsvBindByPosition(position = 10)
    long value_count;
    @CsvBindByPosition(position = 11)
    double value;

    public Input() {
    }

    public Input(String project, String commit, String benchmark, String params, String instance, int trial, int fork, int iteration, String mode, String unit, long value_count, double value) {
        this.project = project;
        this.commit = commit;
        this.benchmark = benchmark;
        this.params = params;
        this.instance = instance;
        this.trial = trial;
        this.fork = fork;
        this.iteration = iteration;
        this.mode = mode;
        this.unit = unit;
        this.value_count = value_count;
        this.value = value;
    }

    public Input(int fork, int iteration, long value_count, double value) {
        this.project = "";
        this.commit = "";
        this.benchmark = "";
        this.params = "";
        this.instance = "";
        this.trial = 0;
        this.fork = fork;
        this.iteration = iteration;
        this.mode = "";
        this.unit = "";
        this.value_count = value_count;
        this.value = value;
    }

    public String getProject() {
        return project;
    }

    public String getCommit() {
        return commit;
    }

    public String getBenchmark() {
        return benchmark;
    }

    public String getParams() {
        return params;
    }

    public String getInstance() {
        return instance;
    }

    public int getTrial() {
        return trial;
    }

    public int getFork() {
        return fork;
    }

    public int getIteration() {
        return iteration;
    }

    public String getMode() {
        return mode;
    }

    public String getUnit() {
        return unit;
    }

    public long getValue_count() {
        return value_count;
    }

    public double getValue() {
        return value;
    }
}