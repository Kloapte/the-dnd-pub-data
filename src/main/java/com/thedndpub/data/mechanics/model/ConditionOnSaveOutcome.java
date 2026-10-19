package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/** A condition interpretation that depends on a specified saving throw outcome. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ConditionOnSaveOutcome(
        MechanicRevisionReference savingThrow,
        SaveOutcome outcome,
        List<String> evidenceIds) implements MechanicContextBinding {
    public ConditionOnSaveOutcome {
        ModelChecks.required(savingThrow, "savingThrow");
        ModelChecks.required(outcome, "outcome");
        evidenceIds = ModelChecks.list(evidenceIds);
        ModelChecks.require(!evidenceIds.isEmpty(), "Condition save dependency requires evidence");
    }
}
