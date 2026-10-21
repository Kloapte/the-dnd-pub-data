package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ChainTargetDistance(ChainDistanceOrigin origin, int maximumFeet) {
    public ChainTargetDistance {
        ModelChecks.required(origin, "origin");
        ModelChecks.require(maximumFeet > 0, "Chain target distance must be positive");
    }
}
