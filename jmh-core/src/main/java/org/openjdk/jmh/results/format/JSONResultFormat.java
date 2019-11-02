/*
 * Copyright (c) 2005, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package org.openjdk.jmh.results.format;

import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.results.BenchmarkResult;
import org.openjdk.jmh.results.IterationResult;
import org.openjdk.jmh.results.Result;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.util.Statistics;
import org.openjdk.jmh.util.Utils;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

class JSONResultFormat implements ResultFormat {

    private static final boolean PRINT_RAW_DATA =
            Boolean.parseBoolean(System.getProperty("jmh.json.rawData", "true"));

    private final PrintStream out;

    public JSONResultFormat(PrintStream out) {
        this.out = out;
    }

    @Override
    public void writeOut(Collection<RunResult> results) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        boolean first = true;

        pw.println("[");
        for (RunResult runResult : results) {
            BenchmarkParams params = runResult.getParams();
            boolean isReconfigureMode = params.getMode().equals(Mode.Reconfigure);

            if (first) {
                first = false;
                pw.println();
            } else {
                pw.println(",");
            }

            pw.println("{");
            pw.println("\"jmhVersion\" : \"" + params.getJmhVersion() + "\",");
            pw.println("\"benchmark\" : \"" + params.getBenchmark() + "\",");
            pw.println("\"mode\" : \"" + params.getMode().shortLabel() + "\",");
            pw.println("\"threads\" : " + params.getThreads() + ",");
            pw.println("\"forks\" : " + params.getForks() + ",");
            if (isReconfigureMode){
                pw.println("\"minForks\" : " + params.getMinForks() + ",");
            }
            pw.println("\"warmupForks\" : " + params.getWarmupForks() + ",");
            if (isReconfigureMode){
                pw.println("\"minWarmupForks\" : " + params.getMinWarmupForks() + ",");
            }
            pw.println("\"jvm\" : " + toJsonString(params.getJvm()) + ",");
            // if empty, write an empty array.
            pw.println("\"jvmArgs\" : [");
            printStringArray(pw, params.getJvmArgs());
            pw.println("],");
            pw.println("\"jdkVersion\" : " + toJsonString(params.getJdkVersion()) + ",");
            pw.println("\"vmName\" : " + toJsonString(params.getVmName()) + ",");
            pw.println("\"vmVersion\" : " + toJsonString(params.getVmVersion()) + ",");
            pw.println("\"warmupIterations\" : " + params.getWarmup().getCount() + ",");
            if (isReconfigureMode){
                pw.println("\"minWarmupIterations\" : " + params.getWarmup().getMinCount() + ",");
            }
            pw.println("\"warmupTime\" : \"" + params.getWarmup().getTime() + "\",");
            pw.println("\"warmupBatchSize\" : " + params.getWarmup().getBatchSize() + ",");
            pw.println("\"measurementIterations\" : " + params.getMeasurement().getCount() + ",");
            pw.println("\"measurementTime\" : \"" + params.getMeasurement().getTime() + "\",");
            pw.println("\"measurementBatchSize\" : " + params.getMeasurement().getBatchSize() + ",");

            if (!params.getParamsKeys().isEmpty()) {
                pw.println("\"params\" : {");
                pw.println(emitParams(params));
                pw.println("},");
            }

            if (isReconfigureMode) {
                pw.println("\"thresholds\" : {");

                pw.println("\"warmupForks\" : ");
                Collection<String> warmupForkThresholds = toListOfStrings(runResult.getWarmupThresholds());
                pw.println(printMultiple(warmupForkThresholds, "[", "]"));
                pw.println(",");

                pw.println("\"measurementForks\" : ");
                Collection<String> measurementForkThresholds = toListOfStrings(runResult.getMeasurementThresholds());
                pw.println(printMultiple(measurementForkThresholds, "[", "]"));
                pw.println(",");

                Collection<String> warmupIterationList = new ArrayList<>();
                for (BenchmarkResult benchmarkResult : runResult.getBenchmarkResults()) {
                    Collection<String> warmupIterationThresholds = toListOfStrings(benchmarkResult.getMetadata().getWarmupThresholds());
                    warmupIterationList.add(printMultiple(warmupIterationThresholds, "[", "]"));
                }

                pw.println("\"warmupIterations\" : {");
                pw.println(printMultiple(warmupIterationList, "[", "]"));
                pw.println("},");

                pw.println("},");

                pw.println("\"warnings\" : {");
                pw.println(tidy(getThresholdWarnings(runResult)));
                pw.println("},");
            }

            Result primaryResult = runResult.getPrimaryResult();
            pw.println("\"primaryMetric\" : {");
            pw.println("\"score\" : " + emit(primaryResult.getScore()) + ",");
            pw.println("\"scoreError\" : " + emit(primaryResult.getScoreError()) + ",");
            pw.println("\"scoreConfidence\" : " + emit(primaryResult.getScoreConfidence()) + ",");
            pw.println(emitPercentiles(primaryResult.getStatistics()));
            pw.println("\"scoreUnit\" : \"" + primaryResult.getScoreUnit() + "\",");

            switch (params.getMode()) {
                case SampleTime:
                case Reconfigure:
                    pw.println("\"rawDataHistogram\" :");
                    pw.println(getRawData(runResult, true));
                    break;
                default:
                    pw.println("\"rawData\" :");
                    pw.println(getRawData(runResult, false));
            }

            pw.println("},"); // primaryMetric end

            Collection<String> secondaries = new ArrayList<>();
            for (Map.Entry<String, Result> e : runResult.getSecondaryResults().entrySet()) {
                String secondaryName = e.getKey();
                Result result = e.getValue();

                StringBuilder sb = new StringBuilder();
                sb.append("\"").append(secondaryName).append("\" : {");
                sb.append("\"score\" : ").append(emit(result.getScore())).append(",");
                sb.append("\"scoreError\" : ").append(emit(result.getScoreError())).append(",");
                sb.append("\"scoreConfidence\" : ").append(emit(result.getScoreConfidence())).append(",");
                sb.append(emitPercentiles(result.getStatistics()));
                sb.append("\"scoreUnit\" : \"").append(result.getScoreUnit()).append("\",");
                sb.append("\"rawData\" : ");

                Collection<String> l2 = new ArrayList<>();
                for (BenchmarkResult benchmarkResult : runResult.getBenchmarkResults()) {
                    Collection<String> scores = new ArrayList<>();
                    for (IterationResult r : benchmarkResult.getIterationResults()) {
                        Result rr = r.getSecondaryResults().get(secondaryName);
                        if (rr != null) {
                            scores.add(emit(rr.getScore()));
                        }
                    }
                    l2.add(printMultiple(scores, "[", "]"));
                }

                sb.append(printMultiple(l2, "[", "]"));
                sb.append("}");
                secondaries.add(sb.toString());
            }
            pw.println("\"secondaryMetrics\" : {");
            pw.println(printMultiple(secondaries, "", ""));
            pw.println("}");

            pw.print("}"); // benchmark end
        }
        pw.println("]");

        out.println(tidy(sw.toString()));
    }

    private String getRawData(RunResult runResult, boolean histogram) {
        StringBuilder sb = new StringBuilder();
        Collection<String> runs = new ArrayList<>();

        if (PRINT_RAW_DATA) {
            for (BenchmarkResult benchmarkResult : runResult.getBenchmarkResults()) {
                Collection<String> iterations = new ArrayList<>();
                for (IterationResult r : benchmarkResult.getIterationResults()) {
                    if (histogram) {
                        Collection<String> singleIter = new ArrayList<>();
                        for (Map.Entry<Double, Long> item : Utils.adaptForLoop(r.getPrimaryResult().getStatistics().getRawData())) {
                            singleIter.add("< " + emit(item.getKey()) + "; " + item.getValue() + " >");
                        }
                        iterations.add(printMultiple(singleIter, "[", "]"));
                    } else {
                        iterations.add(emit(r.getPrimaryResult().getScore()));
                    }
                }
                runs.add(printMultiple(iterations, "[", "]"));
            }
        }
        sb.append(printMultiple(runs, "[", "]"));

        return sb.toString();
    }

    private String getThresholdWarnings(RunResult runResult) {
        BenchmarkParams params = runResult.getParams();

        StringBuilder sb = new StringBuilder();

        boolean warmupForkHasWarning = false;
        if (runResult.getWarmupThresholds().size() > 0) {
            Double lastWarmupForkItem = runResult.getWarmupThresholds().get(runResult.getWarmupThresholds().size() - 1);
            // TODO threshold
            double warmupForkThreshold = 0.05;
            warmupForkHasWarning = runResult.getWarmupThresholds().size() == params.getMinWarmupForks() && lastWarmupForkItem != null && lastWarmupForkItem > warmupForkThreshold;
        }
        sb.append("\"warmupForks\" : " + warmupForkHasWarning + ",");

        boolean measurementForkJasWarning = false;
        if (runResult.getMeasurementThresholds().size() > 0) {
            Double lastMeasurementForkItem = runResult.getMeasurementThresholds().get(runResult.getMeasurementThresholds().size() - 1);
            // TODO threshold
            double measurementForkThreshold = 0.05;
            measurementForkJasWarning = runResult.getMeasurementThresholds().size() == params.getMinForks() && lastMeasurementForkItem != null && lastMeasurementForkItem > measurementForkThreshold;
        }
        sb.append("\"measurementForks\" : " + measurementForkJasWarning + ",");

        Collection<String> warmupIterationList = new ArrayList<>();
        for (BenchmarkResult benchmarkResult : runResult.getBenchmarkResults()) {
            List<Double> warmupThresholds = benchmarkResult.getMetadata().getWarmupThresholds();
            if (warmupThresholds.size() > 0) {
                Double lastWarmupIterationItem = warmupThresholds.get(warmupThresholds.size() - 1);
                // TODO threshold
                double warmupIterationThreshold = 0.05;
                boolean warmupIterationHasWarning = warmupThresholds.size() == params.getWarmup().getCount() && lastWarmupIterationItem != null && lastWarmupIterationItem > warmupIterationThreshold;
                warmupIterationList.add(warmupIterationHasWarning ? "true" : "false");
            }
        }

        sb.append("\"warmupIterations\" : ");
        sb.append(printMultiple(warmupIterationList, "[", "]"));
        sb.append(",");

        boolean totalHasWarning = warmupForkHasWarning || measurementForkJasWarning || warmupIterationList.contains("true");
        sb.append("\"total\" : " + totalHasWarning);

        return sb.toString();
    }

    private String emitParams(BenchmarkParams params) {
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (String k : params.getParamsKeys()) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(", ");
            }
            sb.append("\"").append(k).append("\" : ");
            sb.append("\"").append(params.getParam(k)).append("\"");
        }
        return sb.toString();
    }

    private String emitPercentiles(Statistics stats) {
        StringBuilder sb = new StringBuilder();
        sb.append("\"scorePercentiles\" : {");
        boolean firstPercentile = true;
        for (double p : new double[]{0.00, 50.0, 90, 95, 99, 99.9, 99.99, 99.999, 99.9999, 100}) {
            if (firstPercentile) {
                firstPercentile = false;
            } else {
                sb.append(",");
            }

            double v = stats.getPercentile(p);
            sb.append("\"").append(emit(p)).append("\" : ");
            sb.append(emit(v));
        }
        sb.append("},");
        return sb.toString();
    }

    private String emit(double[] ds) {
        StringBuilder sb = new StringBuilder();

        boolean isFirst = true;
        sb.append("[");
        for (double d : ds) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(",");
            }
            sb.append(emit(d));
        }
        sb.append("]");
        return sb.toString();
    }

    private String emit(double d) {
        if (d != d)
            return "\"NaN\"";
        if (d == Double.NEGATIVE_INFINITY)
            return "\"-INF\"";
        if (d == Double.POSITIVE_INFINITY)
            return "\"+INF\"";
        return String.valueOf(d);
    }

    /**
     * Escaping for a JSON string. Does the typical escaping of double quotes and backslash.
     * Also escapes characters that are handled by the tidying process, so that every ASCII
     * character makes it correctly into the JSON output. Control characters are filtered.
     */
    static String toJsonString(String s) {
        StringBuilder sb = new StringBuilder();
        sb.append("\"");
        for (char c : s.toCharArray()) {
            if (Character.isISOControl(c)) {
                continue;
            }
            switch (c) {
                // use & as escape character to escape the tidying
                case '&': sb.append("&&"); break;
                // we cannot escape to \\\\ since this would create sequences interpreted by the tidying
                case '\\': sb.append("&/"); break;
                case '"': sb.append("&'"); break;
                // escape spacial chars for the tidying formatting below that might appear in a string
                case ',': sb.append(";"); break;
                case '[': sb.append("<"); break;
                case ']': sb.append(">"); break;
                case '<': sb.append("&-"); break;
                case '>': sb.append("&="); break;
                case ';': sb.append("&:"); break;
                case '{': sb.append("&("); break;
                case '}': sb.append("&)"); break;
                default: sb.append(c);
            }
        }
        sb.append("\"");
        return sb.toString();
    }

    static String tidy(String s) {
        s = s.replaceAll("\r", "");
        s = s.replaceAll("\n", " ");
        s = s.replaceAll(",", ",\n");
        s = s.replaceAll("\\{", "{\n");
        s = s.replaceAll("\\[", "[\n");
        s = s.replaceAll("\\}", "\n}\n");
        s = s.replaceAll("\\]", "\n]\n");
        s = s.replaceAll("\\]\n,\n", "],\n");
        s = s.replaceAll("\\}\n,\n", "},\n");
        s = s.replaceAll("\n( *)\n", "\n");

        // Keep these inline:
        s = s.replaceAll(";", ",");
        s = s.replaceAll("\\<", "[");
        s = s.replaceAll("\\>", "]");
        // translate back from string escaping to keep all string characters intact
        s = s.replaceAll("&:", ";");
        s = s.replaceAll("&'", "\\\\\"");
        s = s.replaceAll("&\\(", "{");
        s = s.replaceAll("&\\)", "}");
        s = s.replaceAll("&-", "<");
        s = s.replaceAll("&=", ">");
        s = s.replaceAll("&/", "\\\\\\\\");
        s = s.replaceAll("&&", "&");

        String[] lines = s.split("\n");

        StringBuilder sb = new StringBuilder();

        int ident = 0;
        String prevL = null;
        for (String l : lines) {
            if (prevL != null && (prevL.endsWith("{") || prevL.endsWith("["))) {
                ident++;
            }
            if (l.equals("}") || l.equals("]") || l.equals("},") || l.equals("],")) {
                ident--;
            }

            for (int c = 0; c < ident; c++) {
                sb.append("    ");
            }
            sb.append(l.trim());
            sb.append("\n");
            prevL = l;
        }

        return sb.toString();
    }

    private String printMultiple(Collection<String> elements, String leftBracket, String rightBracket) {
        StringBuilder sb = new StringBuilder();
        sb.append(leftBracket);
        boolean isFirst = true;
        for (String e : elements) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(",");
            }
            sb.append(e);
        }
        sb.append(rightBracket);
        return sb.toString();
    }

    private static void printStringArray(PrintWriter pw, Collection<String> col) {
        boolean isFirst = true;
        for (String e : col) {
            if (isFirst) {
                isFirst = false;
            } else {
                pw.print(',');
            }
            pw.print(toJsonString(e));
        }
    }

    private static List<String> toListOfStrings(List<Double> doubles){
        List<String> strings = new ArrayList<>();
        for (Double threshold : doubles) {
            if (threshold == null) {
                strings.add(null);
            } else {
                strings.add(threshold.toString());
            }
        }
        return strings;
    }
}
