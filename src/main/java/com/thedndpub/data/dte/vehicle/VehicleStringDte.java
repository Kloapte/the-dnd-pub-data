package com.thedndpub.data.dte.vehicle;

import java.util.List;

public record VehicleStringDte(String value) implements VehicleStringOrListDte {
    @Override
    public List<String> values() {
        return List.of(value);
    }
}
