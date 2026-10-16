package com.thedndpub.data.dto.optionalfeature;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record OptionalFeatureDto(
        String id,
        String name,
        SourceReference source,
        List<String> featureTypes,
        List<OptionalFeatureReprintDto> reprintedAs,
        List<SourceReference> otherSources,
        List<OptionalFeaturePrerequisiteDto> prerequisites,
        OptionalFeatureConsumptionDto consumption,
        List<OptionalFeatureSpellGrantDto> spellGrants,
        List<OptionalFeatureProficiencyGrantDto> proficiencyGrants,
        List<OptionalFeatureSenseGrantDto> senseGrants,
        List<OptionalFeatureProgressionDto> featProgressions,
        List<OptionalFeatureProgressionDto> optionalFeatureProgressions) {
    public OptionalFeatureDto {
        featureTypes = List.copyOf(featureTypes);
        reprintedAs = List.copyOf(reprintedAs);
        otherSources = List.copyOf(otherSources);
        prerequisites = List.copyOf(prerequisites);
        spellGrants = List.copyOf(spellGrants);
        proficiencyGrants = List.copyOf(proficiencyGrants);
        senseGrants = List.copyOf(senseGrants);
        featProgressions = List.copyOf(featProgressions);
        optionalFeatureProgressions = List.copyOf(optionalFeatureProgressions);
    }

    public static OptionalFeatureDto from(OptionalFeatureDefinition definition) {
        return new OptionalFeatureDto(
                definition.id(), definition.name(), definition.source(), definition.featureTypes(),
                definition.reprintedAs(), definition.otherSources(), definition.prerequisites(),
                definition.consumption(), definition.spellGrants(), definition.proficiencyGrants(),
                definition.senseGrants(), definition.featProgressions(), definition.optionalFeatureProgressions());
    }
}
