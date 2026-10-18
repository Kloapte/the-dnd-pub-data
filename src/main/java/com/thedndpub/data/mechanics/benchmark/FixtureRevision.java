package com.thedndpub.data.mechanics.benchmark;

import com.thedndpub.data.mechanics.model.ModelChecks;

public record FixtureRevision(int number, String contentSha256) {
    public FixtureRevision {
        ModelChecks.require(number > 0, "Fixture revision must be positive");
        ModelChecks.digest(contentSha256);
    }
}
