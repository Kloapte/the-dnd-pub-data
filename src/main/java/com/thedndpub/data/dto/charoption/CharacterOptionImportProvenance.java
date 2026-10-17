package com.thedndpub.data.dto.charoption;

import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

public record CharacterOptionImportProvenance(
        String resourceName,
        List<EntryDte> entries,
        Boolean hasFluffImages) {
    public CharacterOptionImportProvenance {
        entries = List.copyOf(entries);
    }
}
