package com.thedndpub.data.dto.optionalfeature;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record OptionalFeaturePrerequisiteDto(
        OptionalFeatureLevelRequirementDto level,
        List<String> items,
        String pact,
        List<String> optionalFeatures,
        List<OptionalFeatureSpellRequirementDto> spells,
        OptionalFeatureOtherRequirementDto otherSummary) {
    public OptionalFeaturePrerequisiteDto {
        items = List.copyOf(items);
        optionalFeatures = List.copyOf(optionalFeatures);
        spells = List.copyOf(spells);
    }
}
