package com.thedndpub.data.dte.vehicle;

import java.util.List;

public record VehicleFileDte(List<VehicleDte> vehicle, List<VehicleUpgradeDte> vehicleUpgrade) {
    public VehicleFileDte {
        vehicle = vehicle == null ? null : List.copyOf(vehicle);
        vehicleUpgrade = vehicleUpgrade == null ? null : List.copyOf(vehicleUpgrade);
    }
}
