/*
 * Copyright (c) 2014, 2015, Oracle and/or its affiliates. All rights reserved.
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
package org.openjdk.jmh.results;

import java.io.Serializable;
import java.util.List;

public class BenchmarkResultMetaData implements Serializable {

    private long startTime;
    private final long warmupTime;
    private final long measurementTime;
    private final long stopTime;
    private final long warmupOps;
    private final long measurementOps;
    private final List<Double> warmupThresholds;
    private final boolean atLeastOneWarning;

    public BenchmarkResultMetaData(long warmupTime, long measurementTime, long stopTime, long warmupOps, long measurementOps, List<Double> warmupThresholds, boolean atLeastOneWarning) {
        this.startTime = Long.MIN_VALUE;
        this.warmupTime = warmupTime;
        this.measurementTime = measurementTime;
        this.stopTime = stopTime;
        this.warmupOps = warmupOps;
        this.measurementOps = measurementOps;
        this.warmupThresholds = warmupThresholds;
        this.atLeastOneWarning = atLeastOneWarning;
    }

    public long getStartTime() {
        if (startTime == Long.MIN_VALUE) {
            throw new IllegalStateException("Unset start time");
        }
        return startTime;
    }

    public long getWarmupTime() {
        return warmupTime;
    }

    public long getMeasurementTime() {
        return measurementTime;
    }

    public long getStopTime() {
        return stopTime;
    }

    public long getMeasurementOps() {
        return measurementOps;
    }

    public long getWarmupOps() {
        return warmupOps;
    }

    public List<Double> getWarmupThresholds() {
        return warmupThresholds;
    }

    public boolean hasAtLeastOneWarning() {
        return atLeastOneWarning;
    }

    public void adjustStart(long startTime) {
        this.startTime = startTime;
    }
}
