package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record FixedAbility(
        Ability ability) implements AbilitySelection {
    public FixedAbility {
        ModelChecks.required(ability, "ability");
    }
}
