package com.thedndpub.data.mechanics.experimental;

import com.thedndpub.data.mechanics.model.ModelChecks;
import com.thedndpub.data.mechanics.model.SaveOutcome;

public record ConditionSaveOutcomeRule(
        SaveOutcome outcome,
        ConditionSaveEffect effect,
        ConditionSaveTiming timing) {
    public ConditionSaveOutcomeRule {
        ModelChecks.required(outcome, "outcome");
        ModelChecks.required(effect, "effect");
        ModelChecks.required(timing, "timing");
    }
}
