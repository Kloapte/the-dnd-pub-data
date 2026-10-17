package com.thedndpub.data.references;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public record ResolutionSnapshot(
        List<ResolutionResult> results,
        List<ResolutionDiagnostic> diagnostics,
        Map<ResolutionOutcome, Long> countsByOutcome,
        Map<ReferenceTargetKind, Long> countsByTargetKind,
        List<ReferenceResolutionGroup> countsByOwnerFieldKindAndOutcome) {
    public ResolutionSnapshot {
        results = List.copyOf(results);
        diagnostics = List.copyOf(diagnostics);
        countsByOutcome = Map.copyOf(countsByOutcome);
        countsByTargetKind = Map.copyOf(countsByTargetKind);
        countsByOwnerFieldKindAndOutcome = List.copyOf(countsByOwnerFieldKindAndOutcome);
        Objects.requireNonNull(results, "results");
    }
}
