package com.thedndpub.data.mechanics.benchmark;

import com.thedndpub.data.mechanics.model.MechanicsOwnerReference;
import com.thedndpub.data.mechanics.model.ModelChecks;

import java.util.Comparator;
import java.util.List;

/** Candidate metrics are diagnostics only. Only APPROVED_EVALUATION can carry acceptance. */
public record BenchmarkReport(
        String fixtureId,
        FixtureRevision fixtureRevision,
        MechanicsOwnerReference owner,
        ExtractionScope scope,
        BenchmarkRunState state,
        BenchmarkMetric semanticPrecision,
        BenchmarkMetric semanticRecall,
        BenchmarkMetric evidenceAccuracy,
        int truePositives,
        int falsePositives,
        int falseNegatives,
        int expectedAbstentions,
        int honoredAbstentions,
        int unjustifiedAbstentions,
        int forbiddenAssertions,
        int completenessErrors,
        int duplicateEffects,
        int scopeViolations,
        int formulaControlsMatched,
        List<BenchmarkPatternResult> patterns,
        List<BenchmarkClaimResult> claims,
        List<String> diagnostics,
        Boolean accepted) {
    public BenchmarkReport {
        patterns = ModelChecks.list(patterns);
        claims = ModelChecks.list(claims);
        diagnostics = ModelChecks.list(diagnostics);
        ModelChecks.require((state == BenchmarkRunState.APPROVED_EVALUATION) == (accepted != null),
                "Pending fixtures cannot produce benchmark acceptance");
    }

    public static List<BenchmarkReport> ordered(List<BenchmarkReport> reports) {
        return reports.stream().sorted(Comparator
                .comparing((BenchmarkReport report) -> report.owner().id())
                .thenComparing(BenchmarkReport::fixtureId)
                .thenComparingInt(report -> report.fixtureRevision().number())
                .thenComparing(report -> report.scope().id())
                .thenComparing(report -> report.scope().version())).toList();
    }
}
