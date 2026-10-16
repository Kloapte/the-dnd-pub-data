package com.thedndpub.data.dte.sense;

import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

public record SenseDte(
        String name,
        String source,
        Integer page,
        Boolean srd,
        Boolean basicRules,
        Boolean srd52,
        List<String> reprintedAs,
        List<EntryDte> entries) {
    public SenseDte {
        reprintedAs = reprintedAs == null ? null : List.copyOf(reprintedAs);
        entries = entries == null ? null : List.copyOf(entries);
    }
}
