package com.thedndpub.data.dte.condition;

import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

public record ConditionDte(
        String name,
        String source,
        Integer page,
        List<String> reprintedAs,
        List<EntryDte> entries,
        Boolean srd,
        Boolean basicRules,
        Boolean srd52,
        Boolean basicRules2024,
        List<ConditionSourceReferenceDte> otherSources,
        Boolean hasFluffImages) {
}
