package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/** A fraction derived from the amount on one exact, revision-bound damage claim. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record FractionOfAmount(
        int numerator,
        int denominator,
        MechanicRevisionReference baseAmount) implements AmountExpression {
    public FractionOfAmount {
        ModelChecks.require(numerator > 0, "Fraction numerator must be positive");
        ModelChecks.require(denominator > 0, "Fraction denominator must be positive");
        ModelChecks.require(numerator <= denominator, "Damage fraction cannot exceed the base amount");
        ModelChecks.required(baseAmount, "baseAmount");
    }
}
