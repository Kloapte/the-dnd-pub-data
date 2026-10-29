package com.thedndpub.data.dto.status.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.status.enums.StatusDcFormula;
import com.thedndpub.data.dto.status.enums.StatusDcRounding;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record MaintenanceDc(
        StatusDcFormula formula,
        Integer base,
        Double damageFraction,
        StatusDcRounding rounding,
        Integer maximum) {
    public MaintenanceDc {
        Objects.requireNonNull(formula, "Maintenance DC formula is required");
        Objects.requireNonNull(base, "Maintenance DC base is required");
        if (base < 1) throw new IllegalArgumentException("Maintenance DC base must be at least one");
        if (formula == StatusDcFormula.HIGHER_OF_BASE_OR_DAMAGE_FRACTION && damageFraction == null) {
            throw new IllegalArgumentException("Damage-fraction formula requires damageFraction");
        }
        if (damageFraction != null && (!Double.isFinite(damageFraction) || damageFraction <= 0)) {
            throw new IllegalArgumentException("Maintenance DC damageFraction must be positive");
        }
        if (maximum != null && maximum < 1) throw new IllegalArgumentException("Maintenance DC maximum must be positive");
    }
}
