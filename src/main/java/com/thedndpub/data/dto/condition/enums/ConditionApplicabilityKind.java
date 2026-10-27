package com.thedndpub.data.dto.condition.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ConditionApplicabilityKind {
    @JsonProperty("WHILE_CONDITION_ACTIVE") WHILE_CONDITION_ACTIVE,
    @JsonProperty("FEAR_SOURCE_IN_LINE_OF_SIGHT") FEAR_SOURCE_IN_LINE_OF_SIGHT,
    @JsonProperty("CHARMER_SOCIAL_INTERACTION") CHARMER_SOCIAL_INTERACTION,
    @JsonProperty("OBSERVER_CANNOT_SEE_AFFECTED") OBSERVER_CANNOT_SEE_AFFECTED,
    @JsonProperty("TARGET_IS_NOT_CREATURE") TARGET_IS_NOT_CREATURE,
    @JsonProperty("OBSERVER_CAN_SEE_AFFECTED") OBSERVER_CAN_SEE_AFFECTED,
    @JsonProperty("CREATURE_DISTANCE") CREATURE_DISTANCE
}
