package com.thedndpub.data.dte.vehicle;

public record VehicleTextDte(String value) implements VehicleNumberOrTextDte {
    @Override
    public String displayValue() {
        return value;
    }
}
