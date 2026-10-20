package com.thedndpub.data.mechanics.benchmark;

import com.thedndpub.data.mechanics.model.ModelChecks;

public record ExpectedActivation(String claimKey) implements ExpectedBinding {
    public ExpectedActivation {
        ModelChecks.text(claimKey, "claimKey");
    }
}
