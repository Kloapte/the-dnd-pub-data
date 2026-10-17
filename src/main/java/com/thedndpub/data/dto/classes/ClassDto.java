package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.references.PublicCrossCatalogReferenceDto;
import com.thedndpub.data.dto.source.SourceReference;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassDto(String id, String name, SourceReference source, String edition, Boolean sidekick,
        ClassHitDice hitDice, List<String> savingThrows, List<ClassAbilitySet> primaryAbilities,
        ClassStartingOptions startingOptions, ClassMulticlassOptions multiclassing,
        List<ClassFeatureReferenceDto> features, List<String> subclassIds,
        List<ClassProgressionTable> tables, List<ClassChoiceProgression> choices,
        ClassSpellcasting spellcasting, ClassSourceMetadataDto sources,
        List<PublicCrossCatalogReferenceDto> crossCatalogReferences) {
    public ClassDto {
        savingThrows = immutable(savingThrows);
        primaryAbilities = immutable(primaryAbilities);
        features = immutable(features);
        subclassIds = immutable(subclassIds);
        tables = immutable(tables);
        choices = immutable(choices);
        crossCatalogReferences = immutable(crossCatalogReferences);
    }

    private static <T> List<T> immutable(List<T> values) {
        return values == null ? List.of() : List.copyOf(values);
    }

    public ClassDto withCrossCatalogReferences(List<PublicCrossCatalogReferenceDto> references) {
        return new ClassDto(id, name, source, edition, sidekick, hitDice, savingThrows, primaryAbilities,
                startingOptions, multiclassing, features, subclassIds, tables, choices, spellcasting, sources,
                references);
    }
}
