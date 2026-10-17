package com.thedndpub.data.dte.vehicle;

import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

public record VehicleActionObjectDte(
        String type,
        String style,
        List<VehicleActionItemDte> items,
        String name,
        List<EntryDte> entries) implements VehicleActionDte {
    public VehicleActionObjectDte {
        items = items == null ? null : List.copyOf(items);
        entries = entries == null ? null : List.copyOf(entries);
    }
}
