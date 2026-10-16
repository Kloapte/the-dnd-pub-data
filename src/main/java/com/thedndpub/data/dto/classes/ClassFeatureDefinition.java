package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.thedndpub.data.dto.source.SourceReference;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassFeatureDefinition(
        ClassFeatureId id,
        ClassDefinitionIdentity identity,
        String name,
        SourceReference source,
        ClassId parentClassId,
        int level,
        Boolean variant,
        ClassFeatureConsumption consumes,
        List<ClassFeatureNode> content,
        ClassSourceRelationships sources,
        ClassImportProvenance provenance) {
    public ClassFeatureDefinition {
        content = content == null ? List.of() : List.copyOf(content);
    }
}
