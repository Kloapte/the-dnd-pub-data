package com.thedndpub.data.mechanics.benchmark;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.mechanics.model.ModelChecks;
import com.thedndpub.data.mechanics.model.SaveOutcome;
import com.thedndpub.data.mechanics.model.DamageOutcome;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ExpectedSaveOutcome(String claimKey, SaveOutcome outcome, DamageOutcome damageOutcome)
        implements ExpectedBinding {
    public ExpectedSaveOutcome {
        ModelChecks.text(claimKey, "claimKey");
        ModelChecks.required(outcome, "outcome");
    }

    public ExpectedSaveOutcome(String claimKey, SaveOutcome outcome) {
        this(claimKey, outcome, null);
    }
}
