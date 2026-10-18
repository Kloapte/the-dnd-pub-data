package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record FixedDuration(
        DurationUnit unit,
        @JsonProperty(required = true) long amount,
        Boolean upTo) implements DurationSpecification {
    public FixedDuration {
        ModelChecks.required(unit, "unit");
        ModelChecks.require(amount >= 0, "Duration cannot be negative");
    }
}
