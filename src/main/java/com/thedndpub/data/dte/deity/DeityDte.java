package com.thedndpub.data.dte.deity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

public record DeityDte(
        String name,
        String source,
        Integer page,
        @JsonProperty("_copy") DeityCopyDte copy,
        String pantheon,
        List<String> alignment,
        String title,
        List<String> domains,
        String symbol,
        Boolean srd,
        Boolean basicRules,
        List<String> altNames,
        String category,
        String customExtensionOf,
        List<DeityAdditionalSourceDte> additionalSources,
        List<EntryDte> entries,
        Boolean piety,
        String plane,
        String province,
        String reprintAlias,
        DeitySymbolImageDte symbolImg,
        String worshipers) {
    public DeityDte {
        alignment = immutableNullable(alignment);
        domains = immutableNullable(domains);
        altNames = immutableNullable(altNames);
        additionalSources = immutableNullable(additionalSources);
        entries = immutableNullable(entries);
    }

    private static <T> List<T> immutableNullable(List<T> values) {
        return values == null ? null : List.copyOf(values);
    }
}
