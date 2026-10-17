package com.thedndpub.data.dto.optionalfeature;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dto.references.PublicCrossCatalogReferenceDto;

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
        List<OptionalFeatureProgressionDto> optionalFeatureProgressions,
        List<PublicCrossCatalogReferenceDto> crossCatalogReferences) {
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
        crossCatalogReferences = List.copyOf(crossCatalogReferences);
    }

    public static OptionalFeatureDto from(OptionalFeatureDefinition definition) {
        return from(definition, List.of());
    }

    public static OptionalFeatureDto from(
            OptionalFeatureDefinition definition,
            List<PublicCrossCatalogReferenceDto> crossCatalogReferences) {
        return new OptionalFeatureDto(
                definition.id(), definition.name(), definition.source(), definition.featureTypes(),
                definition.reprintedAs(), definition.otherSources(), definition.prerequisites(),
                definition.consumption(), definition.spellGrants(), definition.proficiencyGrants(),
                definition.senseGrants(), definition.featProgressions(), definition.optionalFeatureProgressions(),
                crossCatalogReferences);
    }

    public OptionalFeatureDto withCrossCatalogReferences(List<PublicCrossCatalogReferenceDto> references) {
        return new OptionalFeatureDto(id, name, source, featureTypes, reprintedAs, otherSources, prerequisites,
                consumption, spellGrants, proficiencyGrants, senseGrants, featProgressions,
                optionalFeatureProgressions, references);
    }
}
