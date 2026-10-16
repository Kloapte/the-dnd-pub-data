package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.thedndpub.data.dto.source.SourceReference;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassDefinition(
        ClassId id,
        ClassDefinitionIdentity identity,
        String name,
        SourceReference source,
        String declaredEdition,
        Boolean sidekick,
        ClassHitDice hitDice,
        List<String> savingThrows,
        List<ClassAbilitySet> primaryAbilities,
        ClassStartingOptions startingOptions,
        ClassMulticlassOptions multiclassing,
        List<ClassFeatureUnlock> features,
        List<SubclassId> subclasses,
        List<ClassProgressionTable> tables,
        List<ClassChoiceProgression> choices,
        ClassSpellcasting spellcasting,
        ClassSourceRelationships sources,
        ClassImportProvenance provenance) {
    public ClassDefinition {
        savingThrows = savingThrows == null ? List.of() : List.copyOf(savingThrows);
        primaryAbilities = primaryAbilities == null ? List.of() : List.copyOf(primaryAbilities);
        features = features == null ? List.of() : List.copyOf(features);
        subclasses = subclasses == null ? List.of() : List.copyOf(subclasses);
        tables = tables == null ? List.of() : List.copyOf(tables);
        choices = choices == null ? List.of() : List.copyOf(choices);
    }
}
