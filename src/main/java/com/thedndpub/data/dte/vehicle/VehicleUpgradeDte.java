package com.thedndpub.data.dte.vehicle;

import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

public record VehicleUpgradeDte(
        String name,
        String source,
        Integer page,
        List<String> upgradeType,
        List<EntryDte> entries) {
    public VehicleUpgradeDte {
        upgradeType = upgradeType == null ? null : List.copyOf(upgradeType);
        entries = entries == null ? null : List.copyOf(entries);
    }
}
