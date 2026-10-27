package com.thedndpub.data.dto.disease;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "kind",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = HitDiceRecoveryMultiplier.class, name = "HIT_DICE_RECOVERY_MULTIPLIER"),
        @JsonSubTypes.Type(value = LongRestPreventedRecovery.class, name = "LONG_REST_PREVENTED_RECOVERY")
})
public sealed interface DiseaseConditionGatedRestriction
        permits HitDiceRecoveryMultiplier, LongRestPreventedRecovery {
    String kind();
}
