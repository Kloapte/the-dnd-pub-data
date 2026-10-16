package com.thedndpub.data.dto.condition;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ConditionDto(
        ConditionId id,
        String name,
        SourceReference source,
        List<DefinitionReprintReference> reprintedAs,
        List<SourceReference> otherSources) {
    public ConditionDto {
        reprintedAs = List.copyOf(reprintedAs);
        otherSources = List.copyOf(otherSources);
    }

    public static ConditionDto from(ConditionDefinition definition) {
        return new ConditionDto(
                definition.id(),
                definition.name(),
                definition.source(),
                definition.reprintedAs(),
                definition.otherSources());
    }
}
