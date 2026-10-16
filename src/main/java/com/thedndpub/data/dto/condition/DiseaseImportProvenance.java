package com.thedndpub.data.dto.condition;

import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

public record DiseaseImportProvenance(List<EntryDte> entries, Boolean srd, Boolean srd52) {
    public DiseaseImportProvenance {
        entries = List.copyOf(entries);
    }
}
