package org.openjdk.jmh.annotations;

import java.lang.annotation.*;

/**
 * <b>Reconfigure annotation allows to set the default reconfigure thresholds for the benchmark.</b>
 *
 * <p>This annotation may be put at {@link Benchmark} method to have effect on that
 * method only, or at the enclosing class instance to have the effect over all
 * {@link Benchmark} methods in the class. This annotation may be overridden with
 * the runtime options.</p>
 */
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Reconfigure {

    double BLANK_THRESHOLD = -1;

    /**
     * @return reconfigure mode
     * @see ReconfigureMode
     */
    ReconfigureMode value() default ReconfigureMode.NONE;

    /**
     * @return coefficient of variation variability threshold
     */
    double covThreshold() default BLANK_THRESHOLD;

    /**
     * @return confidence interval variability threshold
     */
    double ciThreshold() default BLANK_THRESHOLD;

    /**
     * @return p value of kullback leibler divergence as variability threshold
     */
    double kldThreshold() default BLANK_THRESHOLD;
}
