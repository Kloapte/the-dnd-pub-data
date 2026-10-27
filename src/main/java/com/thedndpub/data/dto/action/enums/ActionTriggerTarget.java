package com.thedndpub.data.dto.action.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ActionTriggerTarget {
    @JsonProperty("spell-caster") SPELL_CASTER,
    @JsonProperty("creature") CREATURE,
    @JsonProperty("marked-target") MARKED_TARGET,
    @JsonProperty("affected-creature") AFFECTED_CREATURE,
    @JsonProperty("opportunity-attacker") OPPORTUNITY_ATTACKER,
    @JsonProperty("grappled-creature") GRAPPLED_CREATURE,
    @JsonProperty("observer") OBSERVER,
    @JsonProperty("hidden-creature") HIDDEN_CREATURE
}
