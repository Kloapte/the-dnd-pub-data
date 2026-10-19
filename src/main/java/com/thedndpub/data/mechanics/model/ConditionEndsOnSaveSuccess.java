package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/** A source-backed successful repeat save that ends a condition effect. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ConditionEndsOnSaveSuccess(
        MechanicRevisionReference savingThrow,
        RepeatSaveTiming timing,
        List<String> evidenceIds) implements MechanicContextBinding {
    public ConditionEndsOnSaveSuccess {
        ModelChecks.required(savingThrow, "savingThrow");
        ModelChecks.required(timing, "timing");
        evidenceIds = ModelChecks.list(evidenceIds);
        ModelChecks.require(!evidenceIds.isEmpty(), "Condition ending requires evidence");
    }
}
