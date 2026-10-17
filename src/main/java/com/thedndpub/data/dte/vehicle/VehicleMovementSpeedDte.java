package com.thedndpub.data.dte.vehicle;

import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

public record VehicleMovementSpeedDte(String mode, List<EntryDte> entries) {
    public VehicleMovementSpeedDte {
        entries = entries == null ? null : List.copyOf(entries);
    }
}
