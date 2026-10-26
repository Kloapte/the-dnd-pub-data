package com.thedndpub.data.dto.corerules.status;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MaintenanceDc(
        String formula,
        int base,
        Double damageFraction,
        String rounding,
        Integer maximum) {
    public MaintenanceDc {
        if (formula == null || base < 1) throw new IllegalArgumentException("Invalid maintenance save DC");
        if (damageFraction != null && damageFraction <= 0) {
            throw new IllegalArgumentException("Damage fraction must be positive");
        }
        if (maximum != null && maximum < 1) throw new IllegalArgumentException("Maximum must be positive");
    }
}
