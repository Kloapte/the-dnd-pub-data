package com.thedndpub.data.dto.spell;

import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;
import java.util.Objects;

/** Immutable internal definition; descriptive entries are retained for import fidelity only. */
public record SpellDefinition(
        SpellId id,
        SpellIdentity identity,
        SourceReference source,
        Integer level,
        String school,
        List<String> aliases,
        List<SpellCastingTime> castingTimes,
        SpellRange range,
        SpellComponents components,
        List<SpellDuration> durations,
        Boolean ritual,
        SpellMechanics mechanics,
        List<SpellScaling> scaling,
        List<EntryDte> entries,
        List<EntryDte> entriesHigherLevel,
        SpellSourceRelationships sourceRelationships,
        SpellImportMetadata importMetadata) {
    public SpellDefinition {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(identity, "identity");
        Objects.requireNonNull(source, "source");
        aliases = List.copyOf(aliases);
        castingTimes = List.copyOf(castingTimes);
        durations = List.copyOf(durations);
        scaling = List.copyOf(scaling);
        entries = List.copyOf(entries);
        entriesHigherLevel = List.copyOf(entriesHigherLevel);
        Objects.requireNonNull(mechanics, "mechanics");
        Objects.requireNonNull(sourceRelationships, "sourceRelationships");
        Objects.requireNonNull(importMetadata, "importMetadata");
    }
}
