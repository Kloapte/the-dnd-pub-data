package com.thedndpub.data.dto.condition;

import com.thedndpub.data.dto.source.SourceReference;

import java.util.List;

public record DiseaseDefinition(
        DiseaseId id,
        String name,
        SourceReference source,
        List<DefinitionReprintReference> reprintedAs,
        String classification) {
    public DiseaseDefinition {
        reprintedAs = List.copyOf(reprintedAs);
    }
}
