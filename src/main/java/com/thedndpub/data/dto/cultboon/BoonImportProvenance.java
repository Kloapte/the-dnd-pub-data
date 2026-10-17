package com.thedndpub.data.dto.cultboon;

import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

public record BoonImportProvenance(String resource, List<EntryDte> entries) {
    public BoonImportProvenance {
        entries = List.copyOf(entries);
    }
}
