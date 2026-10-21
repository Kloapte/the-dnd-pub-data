package com.thedndpub.data.mechanics.experimental;

import java.util.List;

public record RelationshipResolutionResult(
        List<ExperimentalMechanicRelationshipDto> relationships,
        List<ExperimentalDiagnosticDto> diagnostics) {
    public RelationshipResolutionResult {
        relationships = List.copyOf(relationships);
        diagnostics = List.copyOf(diagnostics);
    }
}
