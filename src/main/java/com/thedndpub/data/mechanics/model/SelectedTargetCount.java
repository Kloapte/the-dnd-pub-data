package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/** Attack count is the number of distinct targets selected, with exactly one attack per target. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SelectedTargetCount(int maximumSelectedTargets) {
    public SelectedTargetCount {
        ModelChecks.require(maximumSelectedTargets > 0,
                "Maximum selected target count must be positive");
    }
}
