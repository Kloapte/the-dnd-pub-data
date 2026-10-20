package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/** Complete damage dice from this inclusive character-level threshold until the next tier. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record CharacterLevelDamageTier(int minimumCharacterLevel, DiceAmount amount) {
    public CharacterLevelDamageTier {
        ModelChecks.require(minimumCharacterLevel > 1 && minimumCharacterLevel <= 20,
                "minimumCharacterLevel must be between 2 and 20");
        ModelChecks.required(amount, "amount");
    }
}
