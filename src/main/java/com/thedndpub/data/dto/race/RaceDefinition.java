package com.thedndpub.data.dto.race;

import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;
import java.util.Objects;

public record RaceDefinition(
        RaceId id,
        String name,
        SourceReference source,
        RaceDefinitionKind kind,
        RaceId parentId,
        RaceSourceData sourceData,
        RaceMechanics mechanics,
        List<EntryDte> effectiveEntries,
        List<String> effectiveTraitNames,
        RaceImportProvenance provenance) {
    public RaceDefinition {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(kind, "kind");
        Objects.requireNonNull(sourceData, "sourceData");
        Objects.requireNonNull(mechanics, "mechanics");
        effectiveEntries = effectiveEntries == null ? List.of() : List.copyOf(effectiveEntries);
        effectiveTraitNames = effectiveTraitNames == null ? List.of() : List.copyOf(effectiveTraitNames);
        Objects.requireNonNull(provenance, "provenance");
        if ((kind == RaceDefinitionKind.SUBRACE) != (parentId != null)) {
            throw new IllegalArgumentException("Only a subrace definition has a parent race identity");
        }
    }
}
