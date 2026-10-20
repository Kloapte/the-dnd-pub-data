package com.thedndpub.data.mechanics.benchmark;

import com.thedndpub.data.mechanics.model.ModelChecks;

import java.util.List;

public record BenchmarkClaimResult(
        String claimKey,
        ExtractionPattern pattern,
        boolean eligible,
        boolean semanticMatch,
        boolean evidenceMatch,
        List<String> diagnostics) {
    public BenchmarkClaimResult {
        ModelChecks.text(claimKey, "claimKey");
        ModelChecks.required(pattern, "pattern");
        diagnostics = ModelChecks.list(diagnostics);
    }
}
