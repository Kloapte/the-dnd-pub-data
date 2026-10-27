package com.thedndpub.data.dto.condition.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ConditionRollType {
    @JsonProperty("ability-check") ABILITY_CHECK,
    @JsonProperty("saving-throw") SAVING_THROW,
    @JsonProperty("attack") ATTACK,
    @JsonProperty("d20-test") D20_TEST,
    @JsonProperty("initiative") INITIATIVE
}
