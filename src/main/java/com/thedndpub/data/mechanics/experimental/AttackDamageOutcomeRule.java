package com.thedndpub.data.mechanics.experimental;

import com.thedndpub.data.mechanics.model.AttackRollOutcome;
import com.thedndpub.data.mechanics.model.ModelChecks;

public record AttackDamageOutcomeRule(
        AttackRollOutcome outcome,
        DamageApplicationBehavior application) {
    public AttackDamageOutcomeRule {
        ModelChecks.required(outcome, "outcome");
        ModelChecks.required(application, "application");
    }
}
