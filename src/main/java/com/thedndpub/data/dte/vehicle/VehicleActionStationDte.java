package com.thedndpub.data.dte.vehicle;

import java.util.List;

public record VehicleActionStationDte(String name, List<String> entries) {
    public VehicleActionStationDte {
        entries = entries == null ? null : List.copyOf(entries);
    }
}
