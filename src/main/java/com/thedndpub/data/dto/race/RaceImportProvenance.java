package com.thedndpub.data.dto.race;

import com.thedndpub.data.dto.source.SourceIdentity;

import java.util.List;

public record RaceImportProvenance(
        List<SourceIdentity> copyLineage,
        boolean expandedVersion,
        boolean templateImplementation,
        String parentDefinitionId) {
    public RaceImportProvenance {
        copyLineage = copyLineage == null ? List.of() : List.copyOf(copyLineage);
    }
}
