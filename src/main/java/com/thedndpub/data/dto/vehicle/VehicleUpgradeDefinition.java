package com.thedndpub.data.dto.vehicle;

import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

/** Authored upgrade identity and classification; prose is retained internally and never projected publicly. */
public record VehicleUpgradeDefinition(
        VehicleUpgradeId id,
        String name,
        SourceReference source,
        List<String> upgradeType,
        Integer page,
        List<EntryDte> authoredEntries) {
    public VehicleUpgradeDefinition {
        upgradeType = upgradeType == null ? List.of() : List.copyOf(upgradeType);
        authoredEntries = authoredEntries == null ? List.of() : List.copyOf(authoredEntries);
    }
}
