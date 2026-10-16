package com.thedndpub.data.dto.sense;

import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

public record SenseImportProvenance(
        String sourceFile,
        List<EntryDte> entries,
        List<String> reprintedAs,
        Boolean srd,
        Boolean basicRules,
        Boolean srd52) {
    public SenseImportProvenance {
        entries = entries == null ? List.of() : List.copyOf(entries);
        reprintedAs = reprintedAs == null ? List.of() : List.copyOf(reprintedAs);
    }
}
