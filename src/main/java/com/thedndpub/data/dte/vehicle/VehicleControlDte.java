package com.thedndpub.data.dte.vehicle;

import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

public record VehicleControlDte(String name, Integer ac, Integer hp, List<EntryDte> entries) {
    public VehicleControlDte {
        entries = entries == null ? null : List.copyOf(entries);
    }
}
