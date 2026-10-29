package com.thedndpub.data.dto.status.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum StatusTurnApplicability {
    @JsonProperty("first-turn-of-combat") FIRST_TURN_OF_COMBAT,
    @JsonProperty("until-first-turn-ends") UNTIL_FIRST_TURN_ENDS,
    @JsonProperty("when-affected-creature-joins-combat") WHEN_AFFECTED_CREATURE_JOINS_COMBAT
}
