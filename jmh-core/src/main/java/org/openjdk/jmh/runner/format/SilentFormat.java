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
package org.openjdk.jmh.runner.format;

import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.infra.IterationParams;
import org.openjdk.jmh.results.BenchmarkResult;
import org.openjdk.jmh.results.IterationResult;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.io.PrintStream;
import java.util.Collection;

/**
 * Silent format, does nothing.
 */
class SilentFormat extends AbstractOutputFormat {

    public SilentFormat(PrintStream out, VerboseMode verbose) {
        super(out, verbose);
    }

    @Override
    public void startRun() {
    }

    @Override
    public void endRun(Collection<RunResult> results, boolean atLeastOneWarning) {
    }

    @Override
    public void startBenchmark(BenchmarkParams benchmarkParams) {

    }

    @Override
    public void endBenchmark(BenchmarkResult result) {

    }

    @Override
    public void iteration(BenchmarkParams benchmarkParams, IterationParams params, int iteration) {

    }

    @Override
    public void iterationResult(BenchmarkParams benchmarkParams, IterationParams params, int iteration, IterationResult data) {

    }

    @Override
    public void print(String s) {

    }

    @Override
    public void println(String s) {

    }

    @Override
    public void verbosePrintln(String s) {

    }
}
