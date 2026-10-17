package com.thedndpub.data.dto.monster.support;

import com.thedndpub.data.dto.source.SourceReference;

import java.util.List;
import java.util.Objects;

/** Internal support content only. Null body lists denote absence; empty lists remain authored empty values. */
public record LegendaryGroupDefinition(
        LegendaryGroupId id,
        SourceReference source,
        List<SourceReference> additionalSources,
        List<LegendaryGroupBodyEntry> lairActions,
        List<LegendaryGroupBodyEntry> regionalEffects,
        List<LegendaryGroupBodyEntry> mythicEncounter,
        LegendaryGroupProvenance provenance) {
    public LegendaryGroupDefinition {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(provenance, "provenance");
        if (!id.source().equals(source.identity())) {
            throw new IllegalArgumentException("Legendary group source differs from its identity");
        }
        additionalSources = additionalSources == null ? null : List.copyOf(additionalSources);
        lairActions = immutable(lairActions);
        regionalEffects = immutable(regionalEffects);
        mythicEncounter = immutable(mythicEncounter);
    }

    private static List<LegendaryGroupBodyEntry> immutable(List<LegendaryGroupBodyEntry> entries) {
        return entries == null ? null : List.copyOf(entries);
    }
}
