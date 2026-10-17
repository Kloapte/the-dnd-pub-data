package com.thedndpub.data.references;

import java.util.List;
import java.util.Objects;

public record ResolutionDiagnostic(
        ReferenceOwner owner,
        String fieldLocator,
        ReferenceTargetKind targetKind,
        String originalToken,
        String originalSource,
        ResolutionOutcome outcome,
        CandidateCardinality candidateCardinality,
        List<String> candidateIds,
        String reason,
        DiagnosticSeverity severity) {
    public ResolutionDiagnostic {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(fieldLocator, "fieldLocator");
        Objects.requireNonNull(targetKind, "targetKind");
        Objects.requireNonNull(outcome, "outcome");
        Objects.requireNonNull(candidateCardinality, "candidateCardinality");
        Objects.requireNonNull(severity, "severity");
        candidateIds = candidateIds == null ? List.of() : List.copyOf(candidateIds);
    }

    public static ResolutionDiagnostic from(ResolutionResult result, DiagnosticSeverity severity) {
        AuthoredReference reference = result.reference();
        return new ResolutionDiagnostic(reference.owner(), reference.fieldLocator(), reference.targetKind(),
                reference.originalToken(), reference.originalSource(), result.outcome(),
                result.candidateCardinality(), result.candidateIds(), result.reason(), severity);
    }
}
