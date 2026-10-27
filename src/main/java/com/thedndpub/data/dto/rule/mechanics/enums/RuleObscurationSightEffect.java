package com.thedndpub.data.dto.rule.mechanics.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RuleObscurationSightEffect {
    @JsonProperty("disadvantage-on-sight-dependent-perception") DISADVANTAGE_ON_SIGHT_DEPENDENT_PERCEPTION,
    @JsonProperty("blocks-vision") BLOCKS_VISION
}
