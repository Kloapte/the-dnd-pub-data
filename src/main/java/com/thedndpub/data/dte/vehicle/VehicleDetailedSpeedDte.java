package com.thedndpub.data.dte.vehicle;

public record VehicleDetailedSpeedDte(
        VehicleSpeedModeDte fly,
        VehicleSpeedModeDte swim,
        Integer walk,
        Boolean canHover) implements VehicleSpeedDte {
}
