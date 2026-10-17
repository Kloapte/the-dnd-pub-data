package com.thedndpub.data.dte.vehicle;

import java.util.List;

public record VehicleHullDte(Integer ac, List<String> acFrom, Integer hp, Integer dt) {
    public VehicleHullDte {
        acFrom = acFrom == null ? null : List.copyOf(acFrom);
    }
}
