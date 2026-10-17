package com.thedndpub.data.dte.vehicle;

import java.util.List;

public record VehicleStringListDte(List<String> values) implements VehicleStringOrListDte {
    public VehicleStringListDte {
        values = values == null ? List.of() : List.copyOf(values);
    }
}
