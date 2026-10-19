package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ModifierOnSaveOutcome(
        MechanicRevisionReference savingThrow,
        SaveOutcome outcome,
        List<String> evidenceIds) implements MechanicContextBinding {
    public ModifierOnSaveOutcome {
        ModelChecks.required(savingThrow, "savingThrow");
        ModelChecks.required(outcome, "outcome");
        evidenceIds = ModelChecks.list(evidenceIds);
        ModelChecks.require(!evidenceIds.isEmpty(), "Save dependencies require source evidence");
    }
}
