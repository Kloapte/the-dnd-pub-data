package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ActivatedBy.class, name = "ACTIVATED_BY"),
        @JsonSubTypes.Type(value = AppliesForDuration.class, name = "APPLIES_FOR_DURATION"),
        @JsonSubTypes.Type(value = AppliesOnSaveOutcome.class, name = "APPLIES_ON_SAVE_OUTCOME"),
        @JsonSubTypes.Type(value = IncludesTriggeringAttack.class, name = "INCLUDES_TRIGGERING_ATTACK")
})
public sealed interface MechanicContextBinding permits
        ActivatedBy,
        AppliesForDuration,
        AppliesOnSaveOutcome,
        IncludesTriggeringAttack {

}
