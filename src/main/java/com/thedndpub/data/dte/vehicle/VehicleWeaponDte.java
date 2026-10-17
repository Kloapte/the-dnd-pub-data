package com.thedndpub.data.dte.vehicle;

import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

public record VehicleWeaponDte(
        String name,
        Integer ac,
        Integer hp,
        Integer dt,
        Integer crew,
        Integer count,
        VehicleStringOrListDte size,
        List<VehicleWeaponCostDte> costs,
        List<VehicleNamedEntriesDte> action,
        List<EntryDte> entries) {
    public VehicleWeaponDte {
        costs = costs == null ? null : List.copyOf(costs);
        action = action == null ? null : List.copyOf(action);
        entries = entries == null ? null : List.copyOf(entries);
    }
}
