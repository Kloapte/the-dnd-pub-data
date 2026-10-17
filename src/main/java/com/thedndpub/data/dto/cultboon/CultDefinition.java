package com.thedndpub.data.dto.cultboon;

import com.thedndpub.data.dto.source.SourceReference;

import java.util.List;
import java.util.Objects;

public record CultDefinition(
        CultId id,
        String name,
        SourceReference source,
        String type,
        String goalEntry,
        String cultistsEntry,
        String signatureSpellsEntry,
        List<SourceReference> otherSources,
        List<CultBoonReprint> reprintedAs) {
    public CultDefinition {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(source, "source");
        otherSources = List.copyOf(otherSources);
        reprintedAs = List.copyOf(reprintedAs);
    }
}
