package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record UnknownDuration(
        UnknownValueReason reason) implements DurationSpecification {
    public UnknownDuration {
        ModelChecks.required(reason, "reason");
    }
}
