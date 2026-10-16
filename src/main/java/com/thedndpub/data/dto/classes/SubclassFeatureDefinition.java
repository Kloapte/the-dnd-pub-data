package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.thedndpub.data.dto.source.SourceReference;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SubclassFeatureDefinition(
        SubclassFeatureId id,
        ClassDefinitionIdentity identity,
        String name,
        SourceReference source,
        ClassId parentClassId,
        SubclassId parentSubclassId,
        int level,
        Boolean variant,
        ClassFeatureConsumption consumes,
        List<ClassFeatureNode> content,
        ClassSourceRelationships sources,
        ClassImportProvenance provenance) {
    public SubclassFeatureDefinition {
        content = content == null ? List.of() : List.copyOf(content);
    }
}
