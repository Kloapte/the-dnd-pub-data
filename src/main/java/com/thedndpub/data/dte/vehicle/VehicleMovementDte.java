package com.thedndpub.data.dte.vehicle;

import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

public record VehicleMovementDte(
        String name,
        Integer ac,
        Integer hp,
        String hpNote,
        Boolean isControl,
        List<VehicleMovementSpeedDte> speed) {
    public VehicleMovementDte {
        speed = speed == null ? null : List.copyOf(speed);
    }
}
