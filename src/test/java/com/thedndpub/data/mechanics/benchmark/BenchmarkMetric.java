package com.thedndpub.data.mechanics.benchmark;

import com.thedndpub.data.mechanics.model.ModelChecks;

/** Exact counts, not confidence. A zero denominator has no accuracy value. */
public record BenchmarkMetric(long correct, long denominator, MetricAvailability availability) {
    public BenchmarkMetric {
        ModelChecks.require(correct >= 0 && denominator >= correct, "Invalid benchmark counts");
        ModelChecks.require(availability == (denominator == 0
                        ? MetricAvailability.NOT_MEASURED : MetricAvailability.MEASURED),
                "Metric availability conflicts with its denominator");
    }

    public static BenchmarkMetric of(long correct, long denominator) {
        return new BenchmarkMetric(correct, denominator,
                denominator == 0 ? MetricAvailability.NOT_MEASURED : MetricAvailability.MEASURED);
    }
}
