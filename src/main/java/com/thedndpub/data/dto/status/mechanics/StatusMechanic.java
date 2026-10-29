package com.thedndpub.data.dto.status.mechanics;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = HitPointThresholdMechanic.class, name = "HIT_POINT_THRESHOLD"),
        @JsonSubTypes.Type(value = TurnRestrictionMechanic.class, name = "TURN_RESTRICTION"),
        @JsonSubTypes.Type(value = RollModifierMechanic.class, name = "ROLL_MODIFIER"),
        @JsonSubTypes.Type(value = MaintenanceSaveMechanic.class, name = "MAINTENANCE_SAVE"),
        @JsonSubTypes.Type(value = EndingConditionMechanic.class, name = "ENDING_CONDITION")
})
public sealed interface StatusMechanic permits HitPointThresholdMechanic, TurnRestrictionMechanic,
        RollModifierMechanic, MaintenanceSaveMechanic, EndingConditionMechanic {
}
