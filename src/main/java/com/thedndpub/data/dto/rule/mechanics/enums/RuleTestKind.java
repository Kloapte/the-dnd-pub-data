package com.thedndpub.data.dto.rule.mechanics.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RuleTestKind {
    @JsonProperty("ability-check") ABILITY_CHECK,
    @JsonProperty("attack-roll") ATTACK_ROLL,
    @JsonProperty("saving-throw") SAVING_THROW,
    @JsonProperty("initiative") INITIATIVE,
    @JsonProperty("d20-test") D20_TEST
}
