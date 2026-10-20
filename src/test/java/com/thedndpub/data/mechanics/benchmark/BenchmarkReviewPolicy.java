package com.thedndpub.data.mechanics.benchmark;

import com.thedndpub.data.mechanics.model.ModelChecks;

import java.util.List;

public record BenchmarkReviewPolicy(List<BenchmarkReviewerAuthority> authorities) {
    public BenchmarkReviewPolicy {
        authorities = ModelChecks.list(authorities);
        ModelChecks.require(authorities.stream().map(BenchmarkReviewerAuthority::reviewerId).distinct().count()
                == authorities.size(), "Duplicate benchmark reviewer authority");
    }

    public static BenchmarkReviewPolicy none() {
        return new BenchmarkReviewPolicy(List.of());
    }

    public void validate(SpellBenchmarkFixture fixture) {
        BenchmarkHumanDecision decision = fixture.review().humanDecision();
        if (decision == null) {
            return;
        }
        ModelChecks.require(decision.fixtureId().equals(fixture.id())
                        && decision.revision().equals(fixture.revision())
                        && decision.sourceSha256().equals(fixture.source().resourceSha256()),
                "Human review is stale or addresses a different fixture/source revision");
        ModelChecks.require(authorities.stream().anyMatch(authority ->
                        authority.reviewerId().equals(decision.reviewerId())
                                && authority.authorityReference().equals(decision.authorityReference())),
                "Human benchmark decision has no independently configured reviewer authority");
    }
}
