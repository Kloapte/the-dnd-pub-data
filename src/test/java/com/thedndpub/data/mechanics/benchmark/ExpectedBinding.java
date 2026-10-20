package com.thedndpub.data.mechanics.benchmark;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/** Cross-claim expectations use fixture keys, never invented actual claim revision hashes. */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ExpectedActivation.class, name = "ACTIVATED_BY"),
        @JsonSubTypes.Type(value = ExpectedDuration.class, name = "APPLIES_FOR_DURATION"),
        @JsonSubTypes.Type(value = ExpectedSaveOutcome.class, name = "APPLIES_ON_SAVE_OUTCOME"),
        @JsonSubTypes.Type(value = ExpectedTriggeringAttack.class, name = "INCLUDES_TRIGGERING_ATTACK")
})
public sealed interface ExpectedBinding permits
        ExpectedActivation, ExpectedDuration, ExpectedSaveOutcome, ExpectedTriggeringAttack {
}
