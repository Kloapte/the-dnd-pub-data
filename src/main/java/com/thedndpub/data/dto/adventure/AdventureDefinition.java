package com.thedndpub.data.dto.adventure;

import com.thedndpub.data.dto.source.SourceReference;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public record AdventureDefinition(
        AdventureId id,
        String authoredId,
        String name,
        SourceReference source,
        String group,
        LocalDate published,
        String author,
        List<String> aliases,
        String parentSource,
        Integer publishedOrder,
        String storyline,
        AdventureLevelRange levelRange) {
    public AdventureDefinition {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(authoredId, "authoredId");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(source, "source");
        aliases = List.copyOf(aliases);
    }
}
