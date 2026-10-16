package com.thedndpub.data.dto.background;

import com.thedndpub.data.dte.background.BackgroundDte;

import java.util.List;

public record BackgroundImportProvenance(BackgroundDte sourceRecord, List<BackgroundId> copyLineage) {
    public BackgroundImportProvenance {
        copyLineage = List.copyOf(copyLineage);
    }
}
