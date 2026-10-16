package com.thedndpub.data.dto.condition;

import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

public record ConditionImportProvenance(
        List<EntryDte> entries,
        Boolean srd,
        Boolean basicRules,
        Boolean srd52,
        Boolean basicRules2024,
        Boolean hasFluffImages) {
    public ConditionImportProvenance {
        entries = List.copyOf(entries);
    }
}
