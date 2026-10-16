package com.thedndpub.data.dto.feat;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/** One AND group; the list of these groups on a feat represents source alternatives. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record FeatPrerequisiteDto(
        List<FeatAbilityThresholdDto> ability,
        List<String> background,
        List<String> campaign,
        List<String> exclusiveFeatCategory,
        List<String> feat,
        List<String> featCategory,
        List<String> feature,
        FeatLevelRequirementDto level,
        List<String> proficiency,
        List<String> race,
        Boolean spellcasting,
        Boolean spellcasting2020,
        Boolean spellcastingFeature) {
    public FeatPrerequisiteDto {
        ability = copy(ability);
        background = copy(background);
        campaign = copy(campaign);
        exclusiveFeatCategory = copy(exclusiveFeatCategory);
        feat = copy(feat);
        featCategory = copy(featCategory);
        feature = copy(feature);
        proficiency = copy(proficiency);
        race = copy(race);
    }

    private static <T> List<T> copy(List<T> values) {
        return values == null ? List.of() : List.copyOf(values);
    }
}
