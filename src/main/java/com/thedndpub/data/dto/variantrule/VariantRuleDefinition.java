package com.thedndpub.data.dto.variantrule;

import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.common.entry.EntryDte;
import com.thedndpub.data.dte.variantrule.VariantRuleAdditionalSourceDte;
import com.thedndpub.data.dte.variantrule.VariantRuleReprintDte;

import java.util.List;
import java.util.Objects;

/** Normalized identity and provenance with the authored typed entry tree retained internally. */
public record VariantRuleDefinition(
        VariantRuleId id,
        String name,
        SourceReference source,
        String ruleType,
        String rootEntryType,
        Boolean srd,
        Boolean srd52,
        Boolean basicRules2024,
        List<VariantRuleReprintDte> reprintedAs,
        List<VariantRuleAdditionalSourceDte> additionalSources,
        List<EntryDte> authoredEntries,
        int sectionCount,
        int tableCount) {
    public VariantRuleDefinition {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(source, "source");
        reprintedAs = copy(reprintedAs);
        additionalSources = copy(additionalSources);
        authoredEntries = copy(authoredEntries);
    }

    private static <T> List<T> copy(List<T> values) {
        return values == null ? List.of() : List.copyOf(values);
    }
}
