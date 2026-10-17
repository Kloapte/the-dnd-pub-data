package com.thedndpub.data.references;

public record ReferenceResolutionGroup(
        String ownerCatalog,
        String fieldLocator,
        ReferenceTargetKind targetKind,
        ReferenceSemanticForm semanticForm,
        ResolutionOutcome outcome,
        CandidateCardinality candidateCardinality,
        long occurrences) {
}
