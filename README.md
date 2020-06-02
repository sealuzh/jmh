# JMH with support for dynamic reconfiguration

This is the JMH fork that implements dynamic reconfiguration to stop warmup iterations and forks when the results are stable.

Dynamic reconfiguration is the prototype implementation proposed in the *FSE'2020* paper **"Dynamically Reconfiguring Software Microbenchmarks: Reducing Execution Time Without Sacrificing Result Quality"** by Christoph Laaber, Stefan Würsten, Harald C. Gall, and Philipp Leitner [1].
For details, please check out the paper available at http://t.uzh.ch/13k.

Dynamic reconfiguration extends JMH 1.21 by
an additional *benchmark mode*,
three *stoppage criteria* and corresponding *thresholds*,
minimum number of warmup iterations, warmup forks, and forks,
and changes the semantic of warmup iterations and forks to be an upper bound.


## Additional JMH Parameters
The dynamic reconfiguration prototype adds the following additional parameters to JMH 1.21:

### Dynamic Reconfiguration Benchmark Mode

Java annotation:
```Java
@BenchmarkMode(Mode.Reconfigure)
```

Command line parameter:
```bash
-bm Reconfigure
```
or
```bash
-bm re
```

### Stoppage Criteria
Dynamic reconfiguration supports three stoppage criteria (also called reconfigure modes) based on the coefficient of variatio, the Kullback-Leibler divergence, and bootstrap relative confidence interval widths.

Java annotation:
```Java
@Reconfigure(MODE)
```
where `MODE` is one of
`ReconfigureMode.COV`,
`ReconfigureMode.CI`,
or
`ReconfigureMode.DIVERGENCE`.

Command line argument:
```bash
-rm <mode>
```
Reconfigure mode. Available modes are: [COV/cov, CI/ci, DIVERGENCE/kld]. (default: DIVERGENCE)

### Stoppage Criteria Thresholds

#### Coefficient of Variation

Coefficient of variation threshold defaults to `0.01`.

Java annotation:
```Java
@Reconfigure(ReconfigureMode.COV, covThreshold = <double>)
```

Command line argument:
```bash
-rcov <double>
```

#### Kullback-Leibler divergence

Kullback-Leibler divergence threshold defaults to `0.99`.

Java annotation:
```Java
@Reconfigure(ReconfigureMode.DIVERGENCE, kldThreshold = <double>)
```

Command line argument:
```bash
-rkld <double>
```

#### Relative confidence interval width

Relative confidence interval width threshold defaults to `0.03`.

Java annotation:
```Java
@Reconfigure(ReconfigureMode.CI, kldThreshold = <double>)
```

Command line argument:
```bash
-rci <double>
```


### Minimum Number of Warmup Iterations, Warmup Forks, and Forks

#### Warmup Iterations

Minimum number of warmup iterations.
Warmup iterations are not counted towards the benchmark score.
Default value is `5`.

Java annotation:
```Java
@Warmup(minIterations = <int>)
```

Command line argument:
```bash
-mwi <int>
```

#### Warmup Forks

Minimum number of warmup forks defaults to `0`.

Java annotation:
```Java
@Fork(minWarmups = <int>)
```

Command line argument:
```bash
-mwf <int>
```

#### Forks

Minimum number of forks defaults to `2`.

Java annotation:
```Java
@Fork(minValue = <int>)
```

Command line argument:
```bash
-mf <int>
```





## Installation and Usage

JMH with dynamic reconfiguration must be installed from source.
To do so, run the following command from your command line:

```bash
mvn clean install -DskipTests
```

JMH with dynamic reconfiguration can then be used in your JVM projects, by adding the dependency to your build script.
Set `groupId` to `org.openjdk.jmh`,
`artifactId` to `jmh-core`,
and
`version` to `1.21-Reconfigure`.

This protoype relies on the Go tool [*pa*](https://github.com/chrstphlbr/pa) for checking the `CI` stoppage criteria.
It already includes three pre-compiled *pa* versions for Windows, Linux, and macOS for the `amd64` processor architecture.
If a different operating system or architecture is required, you have to compile *pa* from source.
For this, clone *pa* and run the following command:
```bash
GOOS=<os> GOARCH=<arch> go build
```
where `<os>` and `<arch>` are supported by the Go tool chain.

Next you need to copy the generated *pa* binary to `jmh-core/src/main/resources/` and adapt the method `executableName` in `org.openjdk.jmh.reconfigure.statistics.ci.CIHelper` to return your *pa* binary.



# Scientific Publication


[1] Christoph Laaber, Stefan Würsten, Harald C. Gall, Philipp Leitner (2020). **Dynamically Reconfiguring Software Microbenchmarks: Reducing Execution Time Without Sacrificing Result Quality**. In Proceedings of the 2020 ACM Joint European Software Engineering Conference and Symposium on the Foundations of Software Engineering (ESEC/FSE).
