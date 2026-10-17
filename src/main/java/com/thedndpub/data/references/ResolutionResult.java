package com.thedndpub.data.references;

import java.util.List;
import java.util.Objects;

public record ResolutionResult(
        AuthoredReference reference,
        ResolutionOutcome outcome,
        CandidateCardinality candidateCardinality,
        String resolvedTargetId,
        ReferenceTargetKind resolvedDefinitionKind,
        List<TargetIdentity> candidates,
        String reason) {
    public ResolutionResult {
        Objects.requireNonNull(reference, "reference");
        Objects.requireNonNull(outcome, "outcome");
        Objects.requireNonNull(candidateCardinality, "candidateCardinality");
        candidates = candidates == null ? List.of() : List.copyOf(candidates);
        if (outcome == ResolutionOutcome.RESOLVED && resolvedTargetId == null) {
            throw new IllegalArgumentException("Resolved result requires a target ID");
        }
        if (outcome != ResolutionOutcome.RESOLVED && resolvedTargetId != null) {
            throw new IllegalArgumentException("Only resolved results may contain a target ID");
        }
        if (outcome == ResolutionOutcome.RESOLVED && resolvedDefinitionKind == null) {
            throw new IllegalArgumentException("Resolved result requires the resolved definition kind");
        }
        if (outcome != ResolutionOutcome.RESOLVED && resolvedDefinitionKind != null) {
            throw new IllegalArgumentException("Only resolved results may contain a resolved definition kind");
        }
    }

    public List<String> candidateIds() {
        return candidates.stream().map(TargetIdentity::stableId).toList();
    }
}
