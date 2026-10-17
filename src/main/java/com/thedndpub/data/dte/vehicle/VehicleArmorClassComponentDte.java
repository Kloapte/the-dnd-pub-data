package com.thedndpub.data.dte.vehicle;

import java.util.List;

public record VehicleArmorClassComponentDte(Integer ac, List<String> from) {
    public VehicleArmorClassComponentDte {
        from = from == null ? null : List.copyOf(from);
    }
}
