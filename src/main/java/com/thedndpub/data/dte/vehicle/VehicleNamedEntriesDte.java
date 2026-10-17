package com.thedndpub.data.dte.vehicle;

import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

public record VehicleNamedEntriesDte(String name, String type, List<EntryDte> entries) {
    public VehicleNamedEntriesDte {
        entries = entries == null ? null : List.copyOf(entries);
    }
}
