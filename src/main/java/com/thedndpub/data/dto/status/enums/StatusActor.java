package com.thedndpub.data.dto.status.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum StatusActor {
    @JsonProperty("acting-creature") ACTING_CREATURE,
    @JsonProperty("affected-creature") AFFECTED_CREATURE,
    @JsonProperty("attacker") ATTACKER,
    @JsonProperty("beneficiary") BENEFICIARY,
    @JsonProperty("enemy") ENEMY,
    @JsonProperty("grappled-creature") GRAPPLED_CREATURE,
    @JsonProperty("grappler") GRAPPLER,
    @JsonProperty("helper") HELPER,
    @JsonProperty("hidden-creature") HIDDEN_CREATURE,
    @JsonProperty("larger-creature") LARGER_CREATURE,
    @JsonProperty("moving-creature") MOVING_CREATURE,
    @JsonProperty("opportunity-attacker") OPPORTUNITY_ATTACKER,
    @JsonProperty("observer") OBSERVER,
    @JsonProperty("source") SOURCE,
    @JsonProperty("spell-caster") SPELL_CASTER,
    @JsonProperty("smaller-creature") SMALLER_CREATURE,
    @JsonProperty("target") TARGET
}
