package com.thedndpub.data.mechanics.experimental;

import com.thedndpub.data.mechanics.model.ModelChecks;

public record SaveDamageOutcomeRule(
        SaveDamageOutcomeCondition outcome,
        DamageApplicationBehavior application) {
    public SaveDamageOutcomeRule {
        ModelChecks.required(outcome, "outcome");
        ModelChecks.required(application, "application");
    }
}
