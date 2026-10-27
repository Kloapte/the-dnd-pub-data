package com.thedndpub.data.dto.rule.mechanics.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RuleTurnTimingKind {
    @JsonProperty("action") ACTION,
    @JsonProperty("bonus-action") BONUS_ACTION,
    @JsonProperty("free") FREE,
    @JsonProperty("reaction") REACTION,
    @JsonProperty("fixed_gametime") FIXED_GAMETIME,
    @JsonProperty("varied_gametime") VARIED_GAMETIME,
    @JsonProperty("alternatives") ALTERNATIVES
}
