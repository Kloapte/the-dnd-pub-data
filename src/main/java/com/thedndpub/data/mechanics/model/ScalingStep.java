package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ScalingStep(int minimumLevel, AmountExpression value) {
    public ScalingStep {
        ModelChecks.require(minimumLevel > 0, "Scaling threshold must be positive");
        ModelChecks.required(value, "value");
    }
}
