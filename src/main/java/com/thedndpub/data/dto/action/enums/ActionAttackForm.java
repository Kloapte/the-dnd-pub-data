package com.thedndpub.data.dto.action.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ActionAttackForm {
    @JsonProperty("melee") MELEE,
    @JsonProperty("ranged") RANGED,
    @JsonProperty("spell") SPELL,
    @JsonProperty("weapon") WEAPON,
    @JsonProperty("unarmed") UNARMED
}
