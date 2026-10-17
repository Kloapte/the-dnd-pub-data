package com.thedndpub.data.dte.variantrule;

import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

public record VariantRuleDte(
        String name,
        String source,
        Integer page,
        String ruleType,
        String type,
        Boolean srd,
        Boolean srd52,
        Boolean basicRules2024,
        List<VariantRuleReprintDte> reprintedAs,
        List<VariantRuleAdditionalSourceDte> additionalSources,
        List<EntryDte> entries) {
    public VariantRuleDte {
        reprintedAs = copy(reprintedAs);
        additionalSources = copy(additionalSources);
        entries = copy(entries);
    }

    private static <T> List<T> copy(List<T> values) {
        return values == null ? List.of() : List.copyOf(values);
    }
}
