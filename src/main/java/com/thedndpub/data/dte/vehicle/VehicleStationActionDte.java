package com.thedndpub.data.dte.vehicle;

import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

public record VehicleStationActionDte(String name, List<EntryDte> entries) {
    public VehicleStationActionDte {
        entries = entries == null ? null : List.copyOf(entries);
    }
}
