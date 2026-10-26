package com.thedndpub.data.dto.corerules;

import java.util.List;

public record CanonicalCoreRulesRecord(
        CanonicalRecordIdentity identity,
        CanonicalSourceMetadata sourceMetadata,
        List<CanonicalReference> references,
        String resourcePath) {
    public CanonicalCoreRulesRecord {
        if (identity == null || sourceMetadata == null || resourcePath == null || resourcePath.isBlank()) {
            throw new IllegalArgumentException("Canonical record requires identity, source metadata, and resource path");
        }
        references = references == null ? List.of() : List.copyOf(references);
    }
}
