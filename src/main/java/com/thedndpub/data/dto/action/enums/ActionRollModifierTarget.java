package com.thedndpub.data.dto.action.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ActionRollModifierTarget {
    @JsonProperty("affected-creature") AFFECTED_CREATURE,
    @JsonProperty("attacker") ATTACKER,
    @JsonProperty("marked-target") MARKED_TARGET,
    @JsonProperty("opposing-creature") OPPOSING_CREATURE,
    @JsonProperty("beneficiary") BENEFICIARY,
    @JsonProperty("target") TARGET,
    @JsonProperty("acting-creature") ACTING_CREATURE
}
