package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record UnknownAbility(
        UnknownValueReason reason) implements AbilitySelection {
    public UnknownAbility {
        ModelChecks.required(reason, "reason");
    }
}
