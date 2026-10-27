package com.thedndpub.data.dto.condition.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ConditionRestrictionTiming {
    @JsonProperty("on-own-turn") ON_OWN_TURN,
    @JsonProperty("first-turn-of-combat") FIRST_TURN_OF_COMBAT,
    @JsonProperty("until-first-turn-ends") UNTIL_FIRST_TURN_ENDS,
    @JsonProperty("at-or-above-level") AT_OR_ABOVE_LEVEL,
    @JsonProperty("while-grappled") WHILE_GRAPPLED
}
