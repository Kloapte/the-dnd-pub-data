package com.thedndpub.data.mechanics.benchmark;

import com.thedndpub.data.mechanics.model.ModelChecks;

public record FixtureReview(FixtureLifecycle state, BenchmarkHumanDecision humanDecision) {
    public FixtureReview {
        ModelChecks.required(state, "state");
        boolean decided = state == FixtureLifecycle.APPROVED || state == FixtureLifecycle.REJECTED;
        ModelChecks.require(decided == (humanDecision != null), "Only decided fixtures carry a human decision");
        if (decided) {
            ModelChecks.require(state == humanDecision.decision(), "Lifecycle conflicts with human decision");
        }
    }
}
