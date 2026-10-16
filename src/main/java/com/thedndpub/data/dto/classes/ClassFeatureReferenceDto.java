package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassFeatureReferenceDto(
        String id,
        int level,
        int order,
        Boolean gainSubclassFeature,
        Boolean gainSubclassFeatureHasContent,
        String tableDisplayName) {
}
