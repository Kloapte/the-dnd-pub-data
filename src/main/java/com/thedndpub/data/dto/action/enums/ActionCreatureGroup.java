package com.thedndpub.data.dto.action.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ActionCreatureGroup {
    @JsonProperty("ABERRATIONS") ABERRATIONS,
    @JsonProperty("BEASTS") BEASTS,
    @JsonProperty("CELESTIALS") CELESTIALS,
    @JsonProperty("CONSTRUCTS") CONSTRUCTS,
    @JsonProperty("DRAGONS") DRAGONS,
    @JsonProperty("ELEMENTALS") ELEMENTALS,
    @JsonProperty("FEY") FEY,
    @JsonProperty("FIENDS") FIENDS,
    @JsonProperty("GIANTS") GIANTS,
    @JsonProperty("MONSTROSITIES") MONSTROSITIES,
    @JsonProperty("OOZES") OOZES,
    @JsonProperty("PLANTS") PLANTS,
    @JsonProperty("UNDEAD") UNDEAD,
    @JsonProperty("HUMANOIDS") HUMANOIDS
}
