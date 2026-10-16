package com.thedndpub.data.dto.optionalfeature;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record OptionalFeatureSpellRequirementDto(
        String spell,
        String choose,
        String entry,
        String entrySummary,
        Integer count) {
}
