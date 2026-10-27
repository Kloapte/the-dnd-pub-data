package com.thedndpub.data.dto.rule.mechanics.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RuleDamageType {
    @JsonProperty("acid") ACID,
    @JsonProperty("bludgeoning") BLUDGEONING,
    @JsonProperty("cold") COLD,
    @JsonProperty("fire") FIRE,
    @JsonProperty("force") FORCE,
    @JsonProperty("lightning") LIGHTNING,
    @JsonProperty("necrotic") NECROTIC,
    @JsonProperty("piercing") PIERCING,
    @JsonProperty("poison") POISON,
    @JsonProperty("psychic") PSYCHIC,
    @JsonProperty("radiant") RADIANT,
    @JsonProperty("slashing") SLASHING,
    @JsonProperty("thunder") THUNDER
}
