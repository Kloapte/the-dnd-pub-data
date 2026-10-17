package com.thedndpub.data.dte.vehicle;

import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

public record VehicleStationDte(
        String name,
        Integer ac,
        Integer hp,
        Integer crew,
        VehicleStringOrListDte size,
        List<EntryDte> entries,
        List<VehicleStationActionDte> action) {
    public VehicleStationDte {
        entries = entries == null ? null : List.copyOf(entries);
        action = action == null ? null : List.copyOf(action);
    }
}
