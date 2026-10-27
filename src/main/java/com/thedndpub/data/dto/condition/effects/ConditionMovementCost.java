package com.thedndpub.data.dto.condition.effects;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "kind",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SpeedMultiplierCost.class, name = "SPEED_MULTIPLIER"),
        @JsonSubTypes.Type(value = ExtraFeetPerFootCost.class, name = "EXTRA_FEET_PER_FOOT")
})
public sealed interface ConditionMovementCost permits SpeedMultiplierCost, ExtraFeetPerFootCost {
    String kind();
}
