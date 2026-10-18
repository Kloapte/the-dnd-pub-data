package com.thedndpub.data.mechanics.benchmark;

import com.thedndpub.data.mechanics.model.MechanicsHashes;
import com.thedndpub.data.mechanics.model.MechanicsJson;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BenchmarkReviewTests {
    private static SpellBenchmarkFixture pending;
    private static final BenchmarkReviewPolicy SYNTHETIC_POLICY = new BenchmarkReviewPolicy(List.of(
            new BenchmarkReviewerAuthority("synthetic-reviewer", "synthetic-independent-review-mandate",
                    MechanicsHashes.sha256("test-only; no actual fixture approval"))));

    @BeforeAll
    static void prepare() throws IOException {
        pending = BenchmarkFixtureAuthoring.proposed().getFirst();
    }

    static BenchmarkHumanDecision decision(SpellBenchmarkFixture fixture, FixtureLifecycle decision) {
        return new BenchmarkHumanDecision("synthetic-decision", fixture.id(), fixture.revision(),
                fixture.source().resourceSha256(), decision, "synthetic-reviewer", "synthetic-independent-review-mandate",
                true, "2026-10-18T00:00:00Z", "Synthetic harness test only; no real gold approval.");
    }

    static SpellBenchmarkFixture reviewed(SpellBenchmarkFixture fixture, BenchmarkHumanDecision decision) {
        return withReview(fixture, new FixtureReview(decision.decision(), decision));
    }

    static SpellBenchmarkFixture withReview(SpellBenchmarkFixture fixture, FixtureReview review) {
        return new SpellBenchmarkFixture(fixture.schemaVersion(), fixture.id(), fixture.revision(), fixture.owner(),
                fixture.source(), fixture.ownerPointer(), fixture.mechanics(), fixture.formulas(), fixture.deferred(),
                fixture.prohibitions(), review);
    }

    @Test
    void approvedRequiresAnAttributableIndependentHumanDecision() {
        assertThrows(IllegalArgumentException.class, () -> new FixtureReview(FixtureLifecycle.APPROVED, null));
        assertThrows(IllegalArgumentException.class, () -> new BenchmarkHumanDecision("synthetic", pending.id(),
                pending.revision(), pending.source().resourceSha256(), FixtureLifecycle.APPROVED,
                "synthetic-reviewer", "synthetic-independent-review-mandate", false,
                "2026-10-18T00:00:00Z", "Not independent"));
    }

    @Test
    void jsonCannotGrantItsOwnReviewerAuthority() throws IOException {
        var fixture = reviewed(pending, decision(pending, FixtureLifecycle.APPROVED));
        byte[] bytes = MechanicsJson.mapper().writeValueAsBytes(fixture);
        assertThrows(IllegalArgumentException.class, () -> BenchmarkFixtures.parse(bytes, BenchmarkReviewPolicy.none()));
        assertEquals(fixture, BenchmarkFixtures.parse(bytes, SYNTHETIC_POLICY));
    }

    @Test
    void staleRevisionDoesNotInheritReview() {
        var changed = BenchmarkTestDocuments.withContent(pending, pending.mechanics(), pending.deferred());
        var stale = reviewed(changed, decision(pending, FixtureLifecycle.APPROVED));
        assertThrows(IllegalArgumentException.class, () -> SYNTHETIC_POLICY.validate(stale));
    }

    @Test
    void changedFingerprintOrFixtureIdentityInvalidatesReview() {
        BenchmarkHumanDecision valid = decision(pending, FixtureLifecycle.APPROVED);
        for (boolean wrongSource : List.of(true, false)) {
            var invalid = new BenchmarkHumanDecision(valid.decisionId(), wrongSource ? valid.fixtureId() : "another-fixture",
                    valid.revision(), wrongSource ? MechanicsHashes.sha256("another source") : valid.sourceSha256(),
                    valid.decision(), valid.reviewerId(), valid.authorityReference(), true, valid.reviewedAt(), valid.rationale());
            assertThrows(IllegalArgumentException.class, () -> SYNTHETIC_POLICY.validate(reviewed(pending, invalid)));
        }
    }

    @Test
    void pendingAndDraftCannotCarryReviewDecisions() {
        for (FixtureLifecycle state : List.of(FixtureLifecycle.DRAFT, FixtureLifecycle.PENDING_HUMAN_REVIEW)) {
            assertThrows(IllegalArgumentException.class, () -> new FixtureReview(state, decision(pending, FixtureLifecycle.APPROVED)));
        }
    }

    @Test
    void rejectedAndUnknownLifecycleCannotProduceApprovedEvaluation() throws IOException {
        var rejected = reviewed(pending, decision(pending, FixtureLifecycle.REJECTED));
        var engine = new SpellMechanicsBenchmark(SYNTHETIC_POLICY);
        var report = engine.compare(rejected, BenchmarkTestDocuments.empty(rejected), BenchmarkTestDocuments.scope(rejected),
                BenchmarkMode.APPROVED_EVALUATION);
        assertEquals(BenchmarkRunState.NO_APPROVED_GOLD_SET, report.state());
        assertNull(report.accepted());
        String invalid = MechanicsJson.mapper().writeValueAsString(pending).replace("PENDING_HUMAN_REVIEW", "SELF_APPROVED");
        assertThrows(IOException.class, () -> BenchmarkFixtures.parse(invalid.getBytes(StandardCharsets.UTF_8),
                SYNTHETIC_POLICY));
    }

    @Test
    void passingCandidateDiagnosticsCannotApproveAnything() {
        var report = new SpellMechanicsBenchmark(BenchmarkReviewPolicy.none()).compare(pending,
                BenchmarkTestDocuments.materialize(pending), BenchmarkTestDocuments.scope(pending), BenchmarkMode.CANDIDATE_VALIDATION);
        assertEquals(BenchmarkRunState.CANDIDATE_DIAGNOSTICS, report.state());
        assertNull(report.accepted());
        assertEquals(FixtureLifecycle.PENDING_HUMAN_REVIEW, pending.review().state());
    }

    @Test
    void validSyntheticApprovalOnlyEnablesApprovedModeNotMechanicVerification() {
        var fixture = reviewed(pending, decision(pending, FixtureLifecycle.APPROVED));
        var actual = BenchmarkTestDocuments.materialize(fixture);
        var report = new SpellMechanicsBenchmark(SYNTHETIC_POLICY).compare(fixture, actual, BenchmarkTestDocuments.scope(fixture),
                BenchmarkMode.APPROVED_EVALUATION);
        assertEquals(Boolean.TRUE, report.accepted());
        assertEquals(BenchmarkRunState.APPROVED_EVALUATION, report.state());
        assertEquals(0, actual.reviewSummary().verified());
    }

    @Test
    void candidateModeDoesNotSilentlyEvaluateDecidedFixtures() {
        var fixture = reviewed(pending, decision(pending, FixtureLifecycle.APPROVED));
        assertThrows(IllegalArgumentException.class, () -> new SpellMechanicsBenchmark(SYNTHETIC_POLICY).compare(fixture,
                BenchmarkTestDocuments.empty(fixture), BenchmarkTestDocuments.scope(fixture), BenchmarkMode.CANDIDATE_VALIDATION));
    }

    @Test
    void draftFixturesCanOnlyProduceCandidateDiagnostics() {
        var draft = withReview(pending, new FixtureReview(FixtureLifecycle.DRAFT, null));
        var engine = new SpellMechanicsBenchmark(BenchmarkReviewPolicy.none());
        var actual = BenchmarkTestDocuments.materialize(draft);
        assertNull(engine.compare(draft, actual, BenchmarkTestDocuments.scope(draft), BenchmarkMode.CANDIDATE_VALIDATION).accepted());
        assertEquals(BenchmarkRunState.NO_APPROVED_GOLD_SET,
                engine.compare(draft, actual, BenchmarkTestDocuments.scope(draft), BenchmarkMode.APPROVED_EVALUATION).state());
    }

    @Test
    void authorityAndMetricCollectionsAreImmutableAndInvalidCountsFail() {
        var list = new ArrayList<>(SYNTHETIC_POLICY.authorities());
        var policy = new BenchmarkReviewPolicy(list);
        list.clear();
        assertEquals(1, policy.authorities().size());
        assertThrows(UnsupportedOperationException.class, () -> policy.authorities().clear());
        assertThrows(IllegalArgumentException.class, () -> BenchmarkMetric.of(1, 0));
        assertEquals(MetricAvailability.NOT_MEASURED, BenchmarkMetric.of(0, 0).availability());
    }
}
