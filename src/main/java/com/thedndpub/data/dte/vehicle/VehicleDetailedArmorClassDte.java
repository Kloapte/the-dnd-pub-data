package com.thedndpub.data.dte.vehicle;

import java.util.List;

public record VehicleDetailedArmorClassDte(List<VehicleArmorClassComponentDte> values)
        implements VehicleArmorClassDte {
    public VehicleDetailedArmorClassDte {
        values = values == null ? List.of() : List.copyOf(values);
    }
}
