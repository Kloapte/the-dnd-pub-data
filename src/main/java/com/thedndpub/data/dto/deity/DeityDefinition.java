package com.thedndpub.data.dto.deity;

import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.common.entry.EntryDte;
import com.thedndpub.data.dte.deity.DeityCopyDte;
import com.thedndpub.data.dte.deity.DeityDte;
import com.thedndpub.data.dte.deity.DeitySymbolImageDte;

import java.util.List;
import java.util.Objects;

/** Immutable normalized deity with authored data retained separately from effective copy fields. */
public record DeityDefinition(
        DeityId id,
        String name,
        SourceReference source,
        String pantheon,
        String category,
        List<String> alignment,
        List<String> domains,
        String title,
        List<String> alternateNames,
        String province,
        String plane,
        String worshipers,
        String symbol,
        DeitySymbolImageDte symbolImage,
        List<SourceReference> additionalSources,
        Boolean srd,
        Boolean basicRules,
        Boolean piety,
        String reprintAlias,
        String customExtensionOf,
        DeityCopyDte copy,
        List<DeityId> copyLineage,
        List<String> inheritedFields,
        List<EntryDte> authoredEntries,
        DeityDte authoredData) {
    public DeityDefinition {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(pantheon, "pantheon");
        alignment = immutable(alignment);
        domains = immutable(domains);
        alternateNames = immutable(alternateNames);
        additionalSources = immutable(additionalSources);
        copyLineage = immutable(copyLineage);
        inheritedFields = immutable(inheritedFields);
        authoredEntries = immutable(authoredEntries);
        Objects.requireNonNull(authoredData, "authoredData");
    }

    private static <T> List<T> immutable(List<T> values) {
        return values == null ? List.of() : List.copyOf(values);
    }
}
