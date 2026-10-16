package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.thedndpub.data.dto.source.SourceReference;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SubclassDefinition(
        SubclassId id,
        ClassDefinitionIdentity identity,
        String name,
        String shortName,
        SourceReference source,
        ClassId parentClassId,
        String declaredEdition,
        List<ClassFeatureUnlock> features,
        List<ClassProgressionTable> tables,
        List<ClassChoiceProgression> choices,
        ClassSpellcasting spellcasting,
        ClassSourceRelationships sources,
        ClassImportProvenance provenance) {
    public SubclassDefinition {
        features = features == null ? List.of() : List.copyOf(features);
        tables = tables == null ? List.of() : List.copyOf(tables);
        choices = choices == null ? List.of() : List.copyOf(choices);
    }
}
