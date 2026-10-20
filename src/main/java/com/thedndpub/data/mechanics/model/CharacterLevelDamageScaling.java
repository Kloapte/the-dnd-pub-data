package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/** Character-level thresholds for a damage amount; no spell-slot level is implied or evaluated. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record CharacterLevelDamageScaling(List<CharacterLevelDamageTier> tiers) {
    public CharacterLevelDamageScaling {
        tiers = ModelChecks.list(tiers);
        ModelChecks.require(!tiers.isEmpty(), "Character-level damage scaling requires at least one tier");
        int previousLevel = 1;
        for (CharacterLevelDamageTier tier : tiers) {
            ModelChecks.require(tier.minimumCharacterLevel() > previousLevel,
                    "Character-level damage tiers must be strictly increasing");
            previousLevel = tier.minimumCharacterLevel();
        }
    }
}
