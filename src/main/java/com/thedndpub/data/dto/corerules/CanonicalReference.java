package com.thedndpub.data.dto.corerules;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record CanonicalReference(
        String targetType,
        String id,
        String relationship,
        String operation) {
    public CanonicalReference {
        if (targetType == null || targetType.isBlank()) {
            throw new IllegalArgumentException("Reference targetType is required");
        }
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Reference id is required");
        }
        if (!id.startsWith(targetType + ":")) {
            throw new IllegalArgumentException("Reference targetType must match the ID prefix: " + id);
        }
    }
}
