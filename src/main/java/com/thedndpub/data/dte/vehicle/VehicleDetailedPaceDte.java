package com.thedndpub.data.dte.vehicle;

public record VehicleDetailedPaceDte(
        VehicleNumberOrTextDte fly,
        VehicleNumberOrTextDte swim,
        VehicleNumberOrTextDte walk) implements VehiclePaceDte {
}
