package com.thedndpub.data.mechanics.benchmark;

import com.thedndpub.data.mechanics.model.ModelChecks;

public record BenchmarkPatternResult(
        ExtractionPattern pattern,
        boolean eligible,
        BenchmarkMetric semanticPrecision,
        BenchmarkMetric semanticRecall,
        BenchmarkMetric evidenceAccuracy) {
    public BenchmarkPatternResult {
        ModelChecks.required(pattern, "pattern");
        ModelChecks.required(semanticPrecision, "semanticPrecision");
        ModelChecks.required(semanticRecall, "semanticRecall");
        ModelChecks.required(evidenceAccuracy, "evidenceAccuracy");
    }
}
