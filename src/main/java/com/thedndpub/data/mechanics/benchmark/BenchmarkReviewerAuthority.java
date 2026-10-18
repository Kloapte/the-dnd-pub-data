package com.thedndpub.data.mechanics.benchmark;

import com.thedndpub.data.mechanics.model.ModelChecks;

/** Explicit trusted caller configuration, never populated from a fixture being evaluated. */
public record BenchmarkReviewerAuthority(String reviewerId, String authorityReference, String mandateSha256) {
    public BenchmarkReviewerAuthority {
        ModelChecks.text(reviewerId, "reviewerId");
        ModelChecks.text(authorityReference, "authorityReference");
        ModelChecks.digest(mandateSha256);
    }
}
