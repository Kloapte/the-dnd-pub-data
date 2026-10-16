package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassFeatureUnlock(
        ClassResolvedFeatureReference feature,
        int level,
        int order,
        Boolean gainSubclassFeature,
        Boolean gainSubclassFeatureHasContent,
        String tableDisplayName) {
}
