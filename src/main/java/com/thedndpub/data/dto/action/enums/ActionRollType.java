package com.thedndpub.data.dto.action.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ActionRollType {
    @JsonProperty("attack") ATTACK,
    @JsonProperty("ability-check") ABILITY_CHECK,
    @JsonProperty("saving-throw") SAVING_THROW,
    @JsonProperty("d20-test") D20_TEST
}
