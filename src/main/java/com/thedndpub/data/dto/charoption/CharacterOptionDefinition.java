package com.thedndpub.data.dto.charoption;

import com.thedndpub.data.dto.source.SourceReference;

import java.util.List;
import java.util.Objects;

public record CharacterOptionDefinition(
        CharacterOptionId id,
        String name,
        SourceReference source,
        List<CharacterOptionType> optionTypes,
        List<CharacterOptionPrerequisite> prerequisites,
        List<SourceReference> otherSources,
        List<CharacterOptionReprint> reprintedAs) {
    public CharacterOptionDefinition {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(source, "source");
        optionTypes = List.copyOf(optionTypes);
        prerequisites = List.copyOf(prerequisites);
        otherSources = List.copyOf(otherSources);
        reprintedAs = List.copyOf(reprintedAs);
    }
}
