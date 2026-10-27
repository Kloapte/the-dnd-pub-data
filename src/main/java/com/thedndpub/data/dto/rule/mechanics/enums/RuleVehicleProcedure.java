package com.thedndpub.data.dto.rule.mechanics.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RuleVehicleProcedure {
    @JsonProperty("initiative") INITIATIVE,
    @JsonProperty("movement") MOVEMENT,
    @JsonProperty("attack") ATTACK,
    @JsonProperty("damage") DAMAGE,
    @JsonProperty("repair") REPAIR,
    @JsonProperty("crew-role") CREW_ROLE
}
