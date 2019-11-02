package org.openjdk.jmh.annotations;

import java.util.ArrayList;
import java.util.List;

/**
 * Reconfigure mode.
 */
public enum ReconfigureMode {

    /**
     * <p>Internal mode </p>
     */
    NONE("none", "none"),

    /**
     * <p>coefficient of variation as variability criteria</p>
     */
    COV("cov", "coefficient of variation"),

    /**
     * <p>confidence interval as variability criteria</p>
     */
    CI("ci", "confidence interval"),

    /**
     * <p>kullback leibler divergence as variability criteria</p>
     */
    DIVERGENCE("kld", "kullback leibler divergence"),

    ;

    private final String shortLabel;
    private final String longLabel;

    ReconfigureMode(String shortLabel, String longLabel) {
        this.shortLabel = shortLabel;
        this.longLabel = longLabel;
    }

    public String shortLabel() {
        return shortLabel;
    }

    public String longLabel() {
        return longLabel;
    }

    public static ReconfigureMode deepValueOf(String name) {
        try {
            return ReconfigureMode.valueOf(name);
        } catch (IllegalArgumentException iae) {
            ReconfigureMode inferred = null;
            for (ReconfigureMode type : options()) {
                if (type.shortLabel().startsWith(name)) {
                    if (inferred == null) {
                        inferred = type;
                    } else {
                        throw new IllegalStateException("Unable to parse reconfigure mode, ambiguous prefix given: \"" + name + "\"\n" +
                                "Known values are " + getKnown());
                    }
                }
            }
            if (inferred != null) {
                return inferred;
            } else {
                throw new IllegalStateException("Unable to parse reconfigure mode: \"" + name + "\"\n" +
                        "Known values are " + getKnown());
            }
        }
    }

    public static List<String> getKnown() {
        List<String> res = new ArrayList<>();
        for (ReconfigureMode type : ReconfigureMode.options()) {
            res.add(type.name() + "/" + type.shortLabel());
        }
        return res;
    }

    public static ReconfigureMode[] options() {
        return new ReconfigureMode[]{
                COV, CI, DIVERGENCE
        };
    }

    public boolean isNone() {
        return equals(ReconfigureMode.NONE);
    }
}
