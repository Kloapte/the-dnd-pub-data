package com.thedndpub.data.dto.cultboon;

import com.thedndpub.data.dto.source.SourceReference;

import java.util.List;
import java.util.Objects;

public record BoonDefinition(
        BoonId id,
        String name,
        SourceReference source,
        String type,
        String abilityEntry,
        String signatureSpellsEntry,
        List<CultBoonReprint> reprintedAs) {
    public BoonDefinition {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(source, "source");
        reprintedAs = List.copyOf(reprintedAs);
    }
}
