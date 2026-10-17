package com.thedndpub.data.dte.vehicle;

public record VehicleDetailedHitPointsDte(Integer average, String formula, Integer hp, Integer dt, Integer mt)
        implements VehicleHitPointsDte {
}
