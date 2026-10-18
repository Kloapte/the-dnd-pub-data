package com.thedndpub.data.mechanics.benchmark;

import com.thedndpub.data.mechanics.model.ModelChecks;
import com.thedndpub.data.mechanics.model.SaveOutcome;

public record ExpectedSaveOutcome(String claimKey, SaveOutcome outcome) implements ExpectedBinding {
    public ExpectedSaveOutcome {
        ModelChecks.text(claimKey, "claimKey");
        ModelChecks.required(outcome, "outcome");
    }
}
