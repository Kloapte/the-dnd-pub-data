package com.thedndpub.data.mechanics.benchmark;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thedndpub.data.mechanics.model.ModelChecks;

import java.time.Instant;

/** Recorded decision only; trust is supplied separately by BenchmarkReviewPolicy. */
public record BenchmarkHumanDecision(
        String decisionId,
        String fixtureId,
        FixtureRevision revision,
        String sourceSha256,
        FixtureLifecycle decision,
        String reviewerId,
        String authorityReference,
        @JsonProperty(required = true) boolean independentlyReviewed,
        String reviewedAt,
        String rationale) {
    public BenchmarkHumanDecision {
        ModelChecks.text(decisionId, "decisionId");
        ModelChecks.text(fixtureId, "fixtureId");
        ModelChecks.required(revision, "revision");
        ModelChecks.digest(sourceSha256);
        ModelChecks.require(decision == FixtureLifecycle.APPROVED || decision == FixtureLifecycle.REJECTED,
                "A human decision must approve or reject");
        ModelChecks.text(reviewerId, "reviewerId");
        ModelChecks.text(authorityReference, "authorityReference");
        ModelChecks.require(decision != FixtureLifecycle.APPROVED || independentlyReviewed,
                "Approval requires an explicit independent human review attestation");
        Instant.parse(ModelChecks.text(reviewedAt, "reviewedAt"));
        ModelChecks.text(rationale, "rationale");
    }
}
