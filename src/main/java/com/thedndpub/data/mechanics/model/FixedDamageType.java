package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record FixedDamageType(
        String authoredCode) implements DamageTypeSelection {
    public FixedDamageType {
        ModelChecks.text(authoredCode, "authoredCode");
    }
}
