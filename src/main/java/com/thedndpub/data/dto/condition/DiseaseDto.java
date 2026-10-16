package com.thedndpub.data.dto.condition;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record DiseaseDto(
        DiseaseId id,
        String name,
        SourceReference source,
        String classification,
        List<DefinitionReprintReference> reprintedAs) {
    public DiseaseDto {
        reprintedAs = List.copyOf(reprintedAs);
    }

    public static DiseaseDto from(DiseaseDefinition definition) {
        return new DiseaseDto(
                definition.id(),
                definition.name(),
                definition.source(),
                definition.classification(),
                definition.reprintedAs());
    }
}
