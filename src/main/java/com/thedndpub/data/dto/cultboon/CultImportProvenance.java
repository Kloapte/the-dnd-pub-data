package com.thedndpub.data.dto.cultboon;

import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

public record CultImportProvenance(String resource, List<EntryDte> entries) {
    public CultImportProvenance {
        entries = List.copyOf(entries);
    }
}
