package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thedndpub.data.mechanics.review.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.thedndpub.data.mechanics.model.MechanicsModelFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

class MechanicsReviewAndLicensingTests {
    @Test
    void pendingAndRightsUnknownAreIndependentDefaults() {
        var record = ac();
        assertEquals(MechanicsReviewStatus.PENDING_REVIEW, record.review().recordedStatus());
        assertEquals(MechanicsReviewStatus.PENDING_REVIEW, record.review().effectiveStatus());
        assertEquals(MechanicsLicensingStatus.NEEDS_REVIEW, record.licensing().status());
        assertNull(record.review().humanDecision());
    }

    @Test
    void onlyATrustedHumanDecisionCanVerifyAnExactClaim() {
        var record = ac();
        var verified = reviewed(record, MechanicsReviewAction.VERIFY, false);
        assertEquals(MechanicsReviewStatus.VERIFIED, verified.review().effectiveStatus());
        assertEquals(record.revision(), verified.revision());
        assertEquals(MechanicsLicensingStatus.NEEDS_REVIEW, verified.licensing().status());
        assertThrows(IllegalArgumentException.class, () -> ValidatedHumanMechanicsReview.validate(
                verified.review().humanDecision().decision(), MechanicsReviewPolicy.none()));
        assertThrows(IllegalArgumentException.class, () -> ValidatedHumanMechanicsReview.validate(
                verified.review().humanDecision().decision(), new MechanicsReviewPolicy(List.of(
                        new MechanicsReviewerAuthority("fixture-reviewer", "wrong-mandate", HASH)))));
    }

    @Test
    void deserializationCannotSupplyItsOwnReviewerAuthorityOrVerifiedFlag() throws Exception {
        var verified = reviewed(ac(), MechanicsReviewAction.VERIFY, false);
        var trusted = MechanicsJson.mapper(REVIEW_POLICY);
        String json = trusted.writeValueAsString(verified);
        assertThrows(Exception.class, () -> MechanicsJson.mapper().readValue(json, MechanicRecord.class));
        assertEquals(verified, trusted.readValue(json, MechanicRecord.class));
        assertThrows(Exception.class, () -> new com.fasterxml.jackson.databind.ObjectMapper()
                .readValue(json, MechanicRecord.class));

        ObjectNode forged = MechanicsJson.mapper().valueToTree(ac());
        ((ObjectNode) forged.path("review")).put("effectiveStatus", "VERIFIED");
        ObjectNode forgedStatus = forged;
        assertThrows(Exception.class, () -> MechanicsJson.mapper().treeToValue(forgedStatus, MechanicRecord.class));
        forged = MechanicsJson.mapper().valueToTree(ac());
        ((ObjectNode) forged.path("review")).putObject("policy").put("reviewerId", "fixture-reviewer");
        ObjectNode selfAssigned = forged;
        assertThrows(Exception.class, () -> MechanicsJson.mapper().treeToValue(selfAssigned, MechanicRecord.class));
    }

    @Test
    void changedSubstantiveClaimInvalidatesVerificationWithoutDeletingTheDecision() {
        var original = reviewed(ac(), MechanicsReviewAction.VERIFY, false);
        var changed = revise(original, new ArmorClassModifierMechanic(new IntegerModifier(6, "+6")));
        assertEquals(original.id(), changed.id());
        assertEquals(original.revision().reference(original.id()), changed.revision().supersedes());
        assertNotEquals(original.revision().contentSha256(), changed.revision().contentSha256());
        assertEquals(MechanicsReviewStatus.VERIFIED, changed.review().recordedStatus());
        assertEquals(MechanicsReviewStatus.NEEDS_MECHANICS_REVIEW, changed.review().effectiveStatus());
        assertEquals(original.review().humanDecision(), changed.review().humanDecision());
    }

    @Test
    void rejectedAndRequestedReviewStatesArePreserved() {
        assertEquals(MechanicsReviewStatus.REJECTED, reviewed(ac(), MechanicsReviewAction.REJECT, false)
                .review().effectiveStatus());
        assertEquals(MechanicsReviewStatus.NEEDS_MECHANICS_REVIEW,
                reviewed(ac(), MechanicsReviewAction.REQUEST_MECHANICS_REVIEW, false).review().effectiveStatus());
        var unknown = proposal(PHB, "unknown-target", new ArmorClassModifierMechanic(new IntegerModifier(5, "+5")),
                new MechanicContext(new UnknownSubject(UnknownValueReason.NOT_INTERPRETED), List.of()), complete(), shieldBody());
        assertEquals(MechanicsReviewStatus.NEEDS_MECHANICS_REVIEW,
                reviewed(unknown, MechanicsReviewAction.VERIFY, false).review().effectiveStatus());
        assertEquals(MechanicsReviewStatus.REJECTED,
                reviewed(unknown, MechanicsReviewAction.REJECT, false).review().effectiveStatus());
    }

    @Test
    void passingBenchmarksNeverVerifyAndFailuresRequireReview() {
        for (boolean passed : List.of(true, false)) {
            var assessment = new MechanicsAssessment(MechanicsCompleteness.COMPLETE_WITHIN_CLAIM,
                    MechanicsSupport.BOUNDED_CONTEXT, List.of(),
                    List.of(new MechanicsBenchmarkReference("synthetic-suite", "model-only", "v1", HASH, passed)));
            var record = proposal(PHB, "ac-bonus", ac().payload(), ac().context(), assessment, shieldBody());
            assertEquals(passed ? MechanicsReviewStatus.PENDING_REVIEW : MechanicsReviewStatus.NEEDS_MECHANICS_REVIEW,
                    record.review().effectiveStatus());
            if (!passed) {
                assertEquals(MechanicsReviewStatus.NEEDS_MECHANICS_REVIEW,
                        reviewed(record, MechanicsReviewAction.VERIFY, false).review().effectiveStatus());
            }
        }
    }

    @Test
    void humanReviewMustExplicitlyAcceptABoundedPartialScope() {
        var partial = new MechanicsAssessment(MechanicsCompleteness.PARTIAL,
                MechanicsSupport.PROSE_INTERPRETATION, List.of(), List.of());
        var record = proposal(PHB, "ac-bonus", ac().payload(), ac().context(), partial, shieldBody());
        assertEquals(MechanicsReviewStatus.NEEDS_MECHANICS_REVIEW,
                reviewed(record, MechanicsReviewAction.VERIFY, false).review().effectiveStatus());
        assertEquals(MechanicsReviewStatus.VERIFIED,
                reviewed(record, MechanicsReviewAction.VERIFY, true).review().effectiveStatus());
        assertEquals(MechanicsCompleteness.PARTIAL, record.assessment().completeness());
    }

    @Test
    void essentialUnknownsAndUnresolvedReferencesCannotBeSilentlyVerified() {
        var unknownTarget = new MechanicContext(new UnknownSubject(UnknownValueReason.NOT_INTERPRETED), List.of());
        var cases = List.of(
                proposal(PHB, "unknown-amount", new HealingMechanic(new UnknownAmount(UnknownValueReason.UNSUPPORTED_SYNTAX),
                        HealingMode.RESTORE_HIT_POINTS), ac().context(), complete(), shieldBody()),
                proposal(PHB, "unknown-type", new DamageMechanic(new DiceAmount(1, 4, 0),
                        new UnknownDamageType(UnknownValueReason.AMBIGUOUS)), ac().context(), complete(), shieldBody()),
                proposal(PHB, "unknown-target", ac().payload(), unknownTarget, complete(), shieldBody()),
                proposal(PHB, "unknown-save", new SavingThrowRequirementMechanic(
                        new UnknownAbility(UnknownValueReason.NOT_INTERPRETED),
                        new UnknownSaveDifficulty(UnknownValueReason.NOT_AUTHORED)), ac().context(), complete(), shieldBody()),
                proposal(PHB, "unknown-duration", new EffectDurationMechanic(new UnknownDuration(UnknownValueReason.NOT_INTERPRETED)),
                        ac().context(), complete(), shieldBody()),
                proposal(PHB, "spell-protection", new SpellDamagePreventionMechanic(MechanicsValuesTests.unresolvedSpell()),
                        ac().context(), complete(), shieldBody()));
        for (var record : cases) {
            assertEquals(MechanicsReviewStatus.NEEDS_MECHANICS_REVIEW,
                    reviewed(record, MechanicsReviewAction.VERIFY, true).review().effectiveStatus(), record.id().value());
        }
        assertThrows(IllegalArgumentException.class, () -> new MechanicsFinding(
                MechanicsFindingCode.CONTRADICTORY_RELATIONSHIPS, FindingSeverity.INFORMATION, "/payload", List.of()));
    }

    @Test
    void summaryAggregationKeepsEveryIndividualStateAndZeroCounts() throws Exception {
        var pending = ac().review();
        var verified = reviewed(ac(), MechanicsReviewAction.VERIFY, false).review();
        var rejected = reviewed(ac(), MechanicsReviewAction.REJECT, false).review();
        var needs = reviewed(ac(), MechanicsReviewAction.REQUEST_MECHANICS_REVIEW, false).review();
        assertNull(MechanicsReviewSummary.from(List.of()).aggregateStatus());
        assertEquals(MechanicsReviewStatus.VERIFIED, MechanicsReviewSummary.from(List.of(verified)).aggregateStatus());
        assertEquals(MechanicsReviewStatus.REJECTED, MechanicsReviewSummary.from(List.of(rejected)).aggregateStatus());
        assertEquals(MechanicsReviewStatus.PENDING_REVIEW, MechanicsReviewSummary.from(List.of(pending, verified)).aggregateStatus());
        assertEquals(MechanicsReviewStatus.NEEDS_MECHANICS_REVIEW,
                MechanicsReviewSummary.from(List.of(rejected, verified)).aggregateStatus());
        var summary = MechanicsReviewSummary.from(List.of(pending, verified, rejected, needs));
        assertEquals(new MechanicsReviewSummary(4, 1, 1, 1, 1, MechanicsReviewStatus.NEEDS_MECHANICS_REVIEW), summary);
        var mapper = MechanicsJson.mapper();
        var empty = MechanicsReviewSummary.from(List.of());
        assertEquals(empty, mapper.readValue(mapper.writeValueAsBytes(empty), MechanicsReviewSummary.class));
        assertTrue(mapper.readTree(mapper.writeValueAsBytes(empty)).has("verified"));
        assertThrows(IllegalArgumentException.class, () -> new MechanicsReviewSummary(4, 0, 0, 4, 0,
                MechanicsReviewStatus.PENDING_REVIEW));
    }

    @Test
    void formulaOnlyCoverageCannotClassifyAWholeMechanicAsLicensed() {
        var narrow = coverage(ac(), List.of("/payload/amount"));
        assertThrows(IllegalArgumentException.class, () -> new SrdMechanicsLicensing(List.of(narrow)));
        assertThrows(IllegalArgumentException.class, () -> new OtherMechanicsLicensing(List.of(narrow)));
        var partial = new UnassessedMechanicsLicensing(LicensingReviewReason.PARTIAL_CLAIM_COVERAGE,
                List.of(narrow), List.of());
        assertEquals(MechanicsLicensingStatus.NEEDS_REVIEW, withLicensing(ac(), partial).licensing().status());
        assertEquals(List.of(narrow), partial.partialCoverage());
        assertThrows(IllegalArgumentException.class, () -> new LicenseCoverage(narrow.scope(), narrow.document(), narrow.license(),
                List.of(), narrow.attribution(), narrow.decision()));
        assertThrows(IllegalArgumentException.class, () -> new LicenseCoverage(narrow.scope(), narrow.document(), narrow.license(),
                narrow.evidence(), List.of(), narrow.decision()));
    }

    @Test
    void allEstablishedRightsVariantsRequireDocumentedScopeButDoNotVerifyMechanics() throws Exception {
        var claim = ac();
        var coverage = coverage(claim, List.of("/payload", "/context"));
        var basis = new EstablishedReuseBasis("fixture-open-basis", coverage.scope(), coverage.evidence(), coverage.decision());
        var classifications = List.of(new SrdMechanicsLicensing(List.of(coverage)),
                new OtherMechanicsLicensing(List.of(coverage)), new OpenMechanicsLicensing(basis),
                UnassessedMechanicsLicensing.pending());
        var mapper = MechanicsJson.mapper();
        for (var rights : classifications) {
            assertEquals(rights, mapper.readValue(mapper.writerFor(MechanicsLicensing.class)
                    .writeValueAsBytes(rights), MechanicsLicensing.class));
            var record = withLicensing(claim, rights);
            assertEquals(rights.status(), record.licensing().status());
            assertEquals(MechanicsReviewStatus.PENDING_REVIEW, record.review().effectiveStatus());
        }
        assertThrows(Exception.class, () -> mapper.readValue("{\"status\":\"APPROVED_FOR_PUBLICATION\"}", MechanicsLicensing.class));
        assertThrows(Exception.class, () -> mapper.readValue("{\"status\":\"SRD\"}", MechanicsLicensing.class));
        assertThrows(IllegalArgumentException.class, () -> new EstablishedReuseBasis("numbers-are-open",
                coverage.scope(), List.of(), coverage.decision()));
    }

    @Test
    void staleRightsReturnToNeedsReviewWhileTheirEvidenceIsRetained() {
        var record = withLicensing(ac(), new SrdMechanicsLicensing(List.of(coverage(ac(), List.of("/payload", "/context")))));
        var revised = revise(record, new ArmorClassModifierMechanic(new IntegerModifier(6, "+6")));
        var rights = assertInstanceOf(UnassessedMechanicsLicensing.class, revised.licensing());
        assertEquals(LicensingReviewReason.STALE_CLAIM_REVISION, rights.reason());
        assertEquals(List.of(record.licensing()), rights.priorDeterminations());
        assertEquals(MechanicsLicensingStatus.NEEDS_REVIEW, rights.status());
    }

    @Test
    void reviewAndRightsCollectionsAreImmutable() {
        var list = new ArrayList<MechanicsFinding>();
        var state = new MechanicsReviewState(ac().revision().reference(ac().id()), null, list);
        list.add(new MechanicsFinding(MechanicsFindingCode.BENCHMARK_FAILURE,
                FindingSeverity.REQUIRES_REVIEW, "/payload", List.of()));
        assertTrue(state.findings().isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> state.findings().clear());
        var coverage = coverage(ac(), List.of("/payload", "/context"));
        assertThrows(UnsupportedOperationException.class, () -> coverage.evidence().clear());
        assertThrows(UnsupportedOperationException.class, () -> coverage.attribution().clear());
        assertThrows(UnsupportedOperationException.class, () -> coverage.scope().claimPaths().clear());
    }
}
