package com.thedndpub.data.dto.corerules.status;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "kind",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = HitPointThreshold.class, name = "HIT_POINT_THRESHOLD"),
        @JsonSubTypes.Type(value = StatusTurnRestriction.class, name = "TURN_RESTRICTION"),
        @JsonSubTypes.Type(value = StatusRollModifier.class, name = "ROLL_MODIFIER"),
        @JsonSubTypes.Type(value = MaintenanceSave.class, name = "MAINTENANCE_SAVE"),
        @JsonSubTypes.Type(value = StatusEndingCondition.class, name = "ENDING_CONDITION")
})
public sealed interface StatusMechanic permits
        HitPointThreshold,
        StatusTurnRestriction,
        StatusRollModifier,
        MaintenanceSave,
        StatusEndingCondition {
    String kind();
}
