package com.thedndpub.data.dto.optionalfeature;

import com.thedndpub.data.dto.source.SourceReference;

import java.util.List;

public record OptionalFeatureDefinition(
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
    public OptionalFeatureDefinition {
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
}
