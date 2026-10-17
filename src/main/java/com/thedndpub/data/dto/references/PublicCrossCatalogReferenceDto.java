package com.thedndpub.data.dto.references;

import com.fasterxml.jackson.annotation.JsonInclude;

/** Lightweight public projection of an authored cross-catalog relationship. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record PublicCrossCatalogReferenceDto(
        String fieldLocator,
        PublicReferenceTargetKind targetKind,
        PublicReferenceRole role,
        String authoredToken,
        String authoredName,
        String authoredSource,
        String targetId) {
}
