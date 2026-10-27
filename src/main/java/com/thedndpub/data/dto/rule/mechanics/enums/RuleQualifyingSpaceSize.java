package com.thedndpub.data.dto.rule.mechanics.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RuleQualifyingSpaceSize {
    @JsonProperty("same-or-larger-than-creature") SAME_OR_LARGER_THAN_CREATURE,
    @JsonProperty("one-size-smaller-than-creature") ONE_SIZE_SMALLER_THAN_CREATURE
}
