package com.thedndpub.data.dto.race;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RaceSummaryDto(
        String id,
        String name,
        SourceReference source,
        RaceDefinitionKind definitionKind,
        RaceParentReferenceDto parent,
        List<String> size,
        Integer walkingSpeed,
        RaceLineageDto lineage) {
    public RaceSummaryDto {
        size = size == null ? List.of() : List.copyOf(size);
    }
}
