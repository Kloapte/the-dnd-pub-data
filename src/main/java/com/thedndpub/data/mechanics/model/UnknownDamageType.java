package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record UnknownDamageType(
        UnknownValueReason reason) implements DamageTypeSelection {
    public UnknownDamageType {
        ModelChecks.required(reason, "reason");
    }
}
