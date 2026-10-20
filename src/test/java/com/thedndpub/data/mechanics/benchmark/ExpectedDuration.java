package com.thedndpub.data.mechanics.benchmark;

import com.thedndpub.data.mechanics.model.DurationSpecification;
import com.thedndpub.data.mechanics.model.ModelChecks;

public record ExpectedDuration(DurationSpecification duration) implements ExpectedBinding {
    public ExpectedDuration {
        ModelChecks.required(duration, "duration");
    }
}
