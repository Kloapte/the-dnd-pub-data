package com.thedndpub.data.dte.vehicle;

import java.math.BigDecimal;

public record VehicleNumberDte(BigDecimal value) implements VehicleNumberOrTextDte {
    @Override
    public String displayValue() {
        return value == null ? null : value.toString();
    }
}
