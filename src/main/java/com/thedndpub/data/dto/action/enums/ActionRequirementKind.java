package com.thedndpub.data.dto.action.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ActionRequirementKind {
    @JsonProperty("target") TARGET,
    @JsonProperty("weapon") WEAPON,
    @JsonProperty("free-hand") FREE_HAND,
    @JsonProperty("movement") MOVEMENT,
    @JsonProperty("check") CHECK,
    @JsonProperty("save") SAVE,
    @JsonProperty("condition") CONDITION,
    @JsonProperty("item") ITEM
}
