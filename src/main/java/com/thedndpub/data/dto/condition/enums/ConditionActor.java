package com.thedndpub.data.dto.condition.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ConditionActor {
    @JsonProperty("affected-creature") AFFECTED_CREATURE,
    @JsonProperty("attacker") ATTACKER,
    @JsonProperty("target") TARGET,
    @JsonProperty("charmer") CHARMER,
    @JsonProperty("fear-source") FEAR_SOURCE,
    @JsonProperty("observer") OBSERVER,
    @JsonProperty("effect-creator") EFFECT_CREATOR,
    @JsonProperty("grappler") GRAPPLER
}
