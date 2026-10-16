package com.thedndpub.data.dto.condition;

import com.thedndpub.data.dto.source.SourceReference;

import java.util.List;

public record ConditionDefinition(
        ConditionId id,
        String name,
        SourceReference source,
        List<DefinitionReprintReference> reprintedAs,
        List<SourceReference> otherSources) {
    public ConditionDefinition {
        reprintedAs = List.copyOf(reprintedAs);
        otherSources = List.copyOf(otherSources);
    }
}
