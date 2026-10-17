package com.thedndpub.data.dto.vehicle;

import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.vehicle.VehicleDte;

/** Normalized identity, source, mechanics, and retained typed import record. */
public record VehicleDefinition(
        VehicleId id,
        String name,
        SourceReference source,
        String vehicleType,
        VehicleMechanics mechanics,
        VehicleDte authoredData) {
}
