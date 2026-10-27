package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RuleFailedSaveForcedMovement.class, name = "FORCED_MOVEMENT"),
        @JsonSubTypes.Type(value = RuleFailedSaveOutcomeApplyCondition.class, name = "APPLY_CONDITION")
})
public sealed interface RuleFailedSaveOutcome permits RuleFailedSaveForcedMovement, RuleFailedSaveOutcomeApplyCondition {
    String kind();
}
