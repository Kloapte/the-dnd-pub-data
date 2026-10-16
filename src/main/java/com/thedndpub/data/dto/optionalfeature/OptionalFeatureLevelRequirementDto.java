package com.thedndpub.data.dto.optionalfeature;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record OptionalFeatureLevelRequirementDto(
        Integer level,
        OptionalFeatureOptionReferenceDto characterClass,
        OptionalFeatureOptionReferenceDto subclass) {
}
