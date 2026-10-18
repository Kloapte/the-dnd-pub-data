package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SavingThrowRequirementMechanic(
        AbilitySelection ability,
        SaveDifficulty difficulty) implements MechanicPayload {
    public SavingThrowRequirementMechanic {
        ModelChecks.required(ability, "ability");
        ModelChecks.required(difficulty, "difficulty");
    }
}
