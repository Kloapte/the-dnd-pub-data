package com.thedndpub.data.mechanics.benchmark;

import com.thedndpub.data.mechanics.model.Ability;
import com.thedndpub.data.mechanics.model.AnyTrigger;
import com.thedndpub.data.mechanics.model.AppliesOnSaveOutcome;
import com.thedndpub.data.mechanics.model.ArmorClassModifierMechanic;
import com.thedndpub.data.mechanics.model.CasterSubject;
import com.thedndpub.data.mechanics.model.CoverageStatus;
import com.thedndpub.data.mechanics.model.DamageMechanic;
import com.thedndpub.data.mechanics.model.DamageOutcome;
import com.thedndpub.data.mechanics.model.DiceAmount;
import com.thedndpub.data.mechanics.model.DurationUnit;
import com.thedndpub.data.mechanics.model.EffectDurationMechanic;
import com.thedndpub.data.mechanics.model.EvidenceRole;
import com.thedndpub.data.mechanics.model.FixedAbility;
import com.thedndpub.data.mechanics.model.FixedDuration;
import com.thedndpub.data.mechanics.model.FixedSaveDifficulty;
import com.thedndpub.data.mechanics.model.HealingMechanic;
import com.thedndpub.data.mechanics.model.HealingMode;
import com.thedndpub.data.mechanics.model.IntegerModifier;
import com.thedndpub.data.mechanics.model.IncludesTriggeringAttack;
import com.thedndpub.data.mechanics.model.MechanicContext;
import com.thedndpub.data.mechanics.model.MechanicEvidenceAssociation;
import com.thedndpub.data.mechanics.model.MechanicFamily;
import com.thedndpub.data.mechanics.model.MechanicId;
import com.thedndpub.data.mechanics.model.MechanicRecord;
import com.thedndpub.data.mechanics.model.MechanicsCompleteness;
import com.thedndpub.data.mechanics.model.MechanicsCoverage;
import com.thedndpub.data.mechanics.model.MechanicsDocument;
import com.thedndpub.data.mechanics.model.MechanicsEvidence;
import com.thedndpub.data.mechanics.model.MechanicsHashes;
import com.thedndpub.data.mechanics.model.MechanicsJson;
import com.thedndpub.data.mechanics.model.MechanicsProcessing;
import com.thedndpub.data.mechanics.model.ProcessingState;
import com.thedndpub.data.mechanics.model.ReactionActivationMechanic;
import com.thedndpub.data.mechanics.model.SaveOutcome;
import com.thedndpub.data.mechanics.model.SavingThrowRequirementMechanic;
import com.thedndpub.data.mechanics.model.SourceArtifactRevision;
import com.thedndpub.data.mechanics.model.TextMechanicsEvidence;
import com.thedndpub.data.mechanics.model.UnknownSubject;
import com.thedndpub.data.mechanics.model.UnknownValueReason;
import com.thedndpub.data.mechanics.model.UnsupportedMechanicsConstruct;
import com.thedndpub.data.mechanics.model.UnsupportedReason;
import com.thedndpub.data.mechanics.model.UntilTurnBoundary;
import com.thedndpub.data.mechanics.syntax.SyntaxEvidence;
import com.thedndpub.data.mechanics.syntax.SyntaxSourceOwner;
import com.thedndpub.data.mechanics.syntax.SyntaxSpan;
import com.thedndpub.data.mechanics.syntax.SyntaxTextLocation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class BenchmarkComparisonTests {
    private static List<SpellBenchmarkFixture> fixtures;
    private final SpellMechanicsBenchmark engine = new SpellMechanicsBenchmark(BenchmarkReviewPolicy.none());

    @BeforeAll
    static void prepare() throws IOException {
        fixtures = BenchmarkFixtureAuthoring.proposed();
    }

    private SpellBenchmarkFixture shield() {
        return fixtures.getFirst();
    }

    private BenchmarkReport compare(SpellBenchmarkFixture fixture, MechanicsDocument document) {
        return engine.compare(fixture, document, BenchmarkTestDocuments.scope(fixture), BenchmarkMode.CANDIDATE_VALIDATION);
    }

    @Test
    void exactSemanticClaimsMatchInBothEditionsWithoutJsonEquality() {
        for (SpellBenchmarkFixture fixture : fixtures.subList(0, 2)) {
            var report = compare(fixture, BenchmarkTestDocuments.materialize(fixture));
            assertEquals(5, report.truePositives());
            assertEquals(0, report.falsePositives());
            assertEquals(0, report.falseNegatives());
            assertEquals(report.evidenceAccuracy().denominator(), report.evidenceAccuracy().correct());
            assertTrue(report.claims().stream().allMatch(BenchmarkClaimResult::evidenceMatch));
            assertNull(report.accepted());
        }
    }

    @Test
    void sourceEditionAndFingerprintMustMatchExactly() {
        assertThrows(IllegalArgumentException.class, () -> compare(shield(), BenchmarkTestDocuments.materialize(fixtures.get(1))));
        var fixture = shield();
        var changedSource = new SourceArtifactRevision(fixture.source().datasetVersion(), fixture.source().resourcePath(),
                MechanicsHashes.sha256("wrong revision"));
        var actual = MechanicsDocument.create(fixture.owner(), 1, List.of(changedSource),
                new MechanicsProcessing(ProcessingState.NOT_ATTEMPTED, null, null), List.of(), List.of(), List.of(), List.of(),
                new MechanicsCoverage(CoverageStatus.NOT_ASSESSED, null, null, List.of(), List.of(), 0));
        assertThrows(IllegalArgumentException.class, () -> compare(fixture, actual));
    }

    @Test
    void incorrectValueDoesNotMatchItsKindAlone() {
        var original = BenchmarkTestDocuments.materialize(shield());
        var expectation = ac();
        var actual = original.interpretations().stream().filter(value -> value.payload() instanceof ArmorClassModifierMechanic)
                .findFirst().orElseThrow();
        var wrong = BenchmarkTestDocuments.record(shield(), expectation, new ArmorClassModifierMechanic(new IntegerModifier(6, "+6")),
                actual.context(), expectation.evidence());
        var report = compare(shield(), replace(original, actual, wrong, original.evidence()));
        assertEquals(4, report.truePositives());
        assertEquals(1, report.falsePositives());
        assertEquals(1, report.falseNegatives());
        assertTrue(report.forbiddenAssertions() > 0);
    }

    @Test
    void absentContextQualifierIsACompletenessError() {
        var original = BenchmarkTestDocuments.materialize(shield());
        var actual = original.interpretations().stream().filter(value -> value.payload() instanceof ArmorClassModifierMechanic)
                .findFirst().orElseThrow();
        var context = new MechanicContext(actual.context().subject(), actual.context().bindings().subList(0, 2));
        var wrong = BenchmarkTestDocuments.record(shield(), ac(), actual.payload(), context,
                ac().evidence().stream().filter(value -> !value.claimPath().equals("/context/bindings/2")).toList());
        var report = compare(shield(), replace(original, actual, wrong, original.evidence()));
        assertEquals(4, report.truePositives());
        assertTrue(report.completenessErrors() > 0);
    }

    @Test
    void wrongSubjectIsNotAnEquivalentClaim() {
        var original = BenchmarkTestDocuments.materialize(shield());
        var actual = original.interpretations().stream().filter(value -> value.payload() instanceof ArmorClassModifierMechanic)
                .findFirst().orElseThrow();
        var wrong = BenchmarkTestDocuments.record(shield(), ac(), actual.payload(),
                new MechanicContext(new UnknownSubject(UnknownValueReason.AMBIGUOUS), actual.context().bindings()), ac().evidence());
        assertEquals(4, compare(shield(), replace(original, actual, wrong, original.evidence())).truePositives());
    }

    @Test
    void missingSupportedClaimsAreNotSuccessfulAbstentions() {
        var report = compare(shield(), BenchmarkTestDocuments.empty(shield()));
        assertEquals(5, report.falseNegatives());
        assertEquals(5, report.unjustifiedAbstentions());
        assertEquals(MetricAvailability.NOT_MEASURED, report.semanticPrecision().availability());
        assertEquals(0, report.semanticRecall().correct());
    }

    @Test
    void exactEvidenceIsSeparateFromSemanticAccuracy() {
        var fixture = shield();
        var original = BenchmarkTestDocuments.materialize(fixture);
        var actual = original.interpretations().stream().filter(value -> value.payload() instanceof ArmorClassModifierMechanic)
                .findFirst().orElseThrow();
        var expectedEvidence = ac().evidence();
        var old = (TextMechanicsEvidence) expectedEvidence.getFirst().evidence();
        var span = old.syntax().span();
        var shifted = new TextMechanicsEvidence("shifted-utf16", old.source(),
                new SyntaxEvidence(old.syntax().location(), new SyntaxSpan(span.startInclusive() + 1, span.endExclusive())),
                old.textSha256(), old.origin(), old.authoredOwner());
        List<ExpectedEvidence> changed = new ArrayList<>(expectedEvidence);
        changed.set(0, new ExpectedEvidence(shifted, "/payload", EvidenceRole.EFFECT_ROLE));
        var wrong = BenchmarkTestDocuments.record(fixture, ac(), actual.payload(), actual.context(), changed);
        List<MechanicsEvidence> evidence = new ArrayList<>(original.evidence());
        evidence.add(shifted);
        var report = compare(fixture, replace(original, actual, wrong, evidence));
        assertEquals(5, report.truePositives());
        assertEquals(report.evidenceAccuracy().denominator() - 1, report.evidenceAccuracy().correct());
        assertFalse(report.claims().stream()
                .filter(value -> value.claimKey().equals("ac-bonus"))
                .findFirst().orElseThrow().evidenceMatch());
    }

    @Test
    void missingEvidenceAssociationIsNotCredited() {
        var original = BenchmarkTestDocuments.materialize(shield());
        var actual = original.interpretations().stream().filter(value -> value.payload() instanceof ArmorClassModifierMechanic)
                .findFirst().orElseThrow();
        var wrong = BenchmarkTestDocuments.record(shield(), ac(), actual.payload(), actual.context(),
                ac().evidence().stream().filter(value -> !value.claimPath().equals("/context/subject")).toList());
        var report = compare(shield(), replace(original, actual, wrong, original.evidence()));
        assertEquals(5, report.truePositives());
        assertFalse(report.claims().stream()
                .filter(value -> value.claimKey().equals("ac-bonus"))
                .findFirst().orElseThrow().evidenceMatch());
    }

    @Test
    void extraAndForbiddenMechanicsReducePrecision() {
        var original = BenchmarkTestDocuments.materialize(shield());
        var extraExpected = new ExpectedMechanic("invented-save", MechanicFamily.SAVING_THROW_REQUIREMENT,
                "shield-ac-v1", new SavingThrowRequirementMechanic(new FixedAbility(Ability.DEXTERITY), new FixedSaveDifficulty(10)),
                new ExpectedContext(new CasterSubject(), List.of()), MechanicsCompleteness.PARTIAL,
                List.of(ac().evidence().getFirst()));
        var extra = BenchmarkTestDocuments.record(shield(), extraExpected, extraExpected.payload(),
                new MechanicContext(new CasterSubject(), List.of()), extraExpected.evidence());
        var records = new ArrayList<>(original.interpretations());
        records.add(extra);
        var scope = new ExtractionScope("extra-test", "1", Stream.concat(
                BenchmarkTestDocuments.scope(shield()).patterns().stream(),
                Stream.of(new ExtractionPattern(MechanicFamily.SAVING_THROW_REQUIREMENT, "shield-ac-v1"))).toList());
        var actual = BenchmarkTestDocuments.document(shield(), records, original.evidence(), List.of(), CoverageStatus.PARTIAL);
        var report = engine.compare(shield(), actual, scope, BenchmarkMode.CANDIDATE_VALIDATION);
        assertEquals(1, report.falsePositives());
        assertEquals(6, report.semanticPrecision().denominator());
        assertEquals(2, report.forbiddenAssertions());
    }

    @Test
    void duplicateEffectsWithDistinctIdsAreStillErrors() {
        var original = BenchmarkTestDocuments.materialize(shield());
        var ac = original.interpretations().stream().filter(value -> value.payload() instanceof ArmorClassModifierMechanic)
                .findFirst().orElseThrow();
        var duplicate = MechanicRecord.propose(MechanicId.of(shield().owner(), "duplicate-ac"), ac.payload(), ac.context(),
                ac.evidenceAssociations(), ac.derivation(), ac.assessment());
        var records = new ArrayList<>(original.interpretations());
        records.add(duplicate);
        var report = compare(shield(), BenchmarkTestDocuments.document(shield(), records,
                original.evidence(), List.of(), CoverageStatus.PARTIAL));
        assertEquals(1, report.duplicateEffects());
        assertEquals(1, report.falsePositives());
        assertThrows(IllegalArgumentException.class, () -> BenchmarkTestDocuments.document(shield(),
                List.of(ac, ac), original.evidence(), List.of(), CoverageStatus.PARTIAL));
        assertTrue(BenchmarkSemantics.equivalentContext(
                new MechanicContext(new CasterSubject(), List.of(new IncludesTriggeringAttack(List.of("pool-a")))),
                new MechanicContext(new CasterSubject(), List.of(new IncludesTriggeringAttack(List.of("pool-b"))))));
    }

    @Test
    void controlsDoNotInflateSemanticDenominators() {
        var fixture = fixtures.get(4);
        var report = compare(fixture, BenchmarkTestDocuments.materialize(fixture));
        assertEquals(1, report.formulaControlsMatched());
        assertEquals(MetricAvailability.NOT_MEASURED, report.semanticRecall().availability());
        assertEquals(MetricAvailability.NOT_MEASURED, report.semanticPrecision().availability());
        assertEquals(3, report.honoredAbstentions());
    }

    @Test
    void explicitExpectedAbstentionsAreMeasuredSeparatelyFromSilence() {
        var fixture = fixtures.get(5);
        var actual = BenchmarkTestDocuments.materialize(fixture);
        var complete = compare(fixture, actual);
        assertEquals(8, complete.honoredAbstentions());
        assertEquals(0, complete.unjustifiedAbstentions());
        var missing = compare(fixture, BenchmarkTestDocuments.empty(fixture));
        assertEquals(0, missing.honoredAbstentions());
        assertEquals(8, missing.expectedAbstentions());
        assertTrue(missing.diagnostics().stream().anyMatch(value -> value.startsWith("UNREPORTED_OR_AMBIGUOUS_ABSTENTION")));
    }

    @Test
    void unexpectedAbstentionsAreNotCredited() {
        var original = BenchmarkTestDocuments.materialize(shield());
        var unsupported = new UnsupportedMechanicsConstruct("unjustified", MechanicFamily.ARMOR_CLASS_MODIFIER,
                UnsupportedReason.NO_SUPPORTED_PATTERN, List.of(original.evidence().getFirst().id()), List.of());
        var report = compare(shield(), BenchmarkTestDocuments.document(shield(), original.interpretations(), original.evidence(),
                List.of(unsupported), CoverageStatus.PARTIAL));
        assertEquals(1, report.unjustifiedAbstentions());
    }

    @Test
    void futureControlsRetainUnknownSubjectsModifiersAndOutcomeRelationships() {
        for (SpellBenchmarkFixture fixture : fixtures.subList(2, 4)) {
            var actual = BenchmarkTestDocuments.materialize(fixture);
            var report = compare(fixture, actual);
            assertEquals(fixture.mechanics().size(), report.truePositives());
            assertEquals(fixture.deferred().size(), report.honoredAbstentions());
            assertTrue(actual.interpretations().stream().allMatch(value ->
                    value.assessment().completeness() == MechanicsCompleteness.PARTIAL));
        }
    }

    @Test
    void declarationOfUnsupportedScopeIsNotAFalseSuccess() {
        var actual = BenchmarkTestDocuments.materialize(shield());
        var scope = new ExtractionScope("nothing-supported", "1", List.of());
        var report = engine.compare(shield(), actual, scope, BenchmarkMode.CANDIDATE_VALIDATION);
        assertEquals(5, report.scopeViolations());
        assertEquals(MetricAvailability.NOT_MEASURED, report.semanticRecall().availability());
        assertNull(report.accepted());
    }

    @Test
    void completeCoverageWithMissingClaimsIsAnErrorEvenWhenTheCanonicalSnapshotIsValid() {
        var original = BenchmarkTestDocuments.materialize(shield());
        var turnBoundary = original.interpretations().stream().filter(value ->
                value.payload() instanceof EffectDurationMechanic duration
                        && duration.specification() instanceof UntilTurnBoundary).findFirst().orElseThrow();
        var document = BenchmarkTestDocuments.document(shield(), List.of(turnBoundary), original.evidence(),
                List.of(), CoverageStatus.COMPLETE_WITHIN_SCOPE);
        var report = compare(shield(), document);
        assertEquals(4, report.falseNegatives());
        assertTrue(report.completenessErrors() > 0);
        assertTrue(report.diagnostics().contains("FALSE_COMPLETE_COVERAGE"));
    }

    @Test
    void partialClaimCannotMasqueradeAsACompleteExpectedClaim() {
        var original = BenchmarkTestDocuments.materialize(shield());
        var actual = original.interpretations().stream().filter(value -> value.payload() instanceof ArmorClassModifierMechanic)
                .findFirst().orElseThrow();
        var partial = new ExpectedMechanic(ac().key(), ac().family(), ac().patternId(), ac().payload(), ac().context(),
                MechanicsCompleteness.PARTIAL, ac().evidence());
        var wrong = BenchmarkTestDocuments.record(shield(), partial, actual.payload(), actual.context(), partial.evidence());
        var report = compare(shield(), replace(original, actual, wrong, original.evidence()));
        assertEquals(4, report.truePositives());
        assertTrue(report.completenessErrors() > 0);
    }

    @Test
    void missingObservationControlsDoNotBecomeMissingEffects() {
        var fixture = fixtures.get(4);
        var original = BenchmarkTestDocuments.materialize(fixture);
        var document = MechanicsDocument.create(fixture.owner(), 1, List.of(fixture.source()), original.processing(),
                List.of(), List.of(), original.unsupported(), original.evidence(), original.coverage());
        var report = compare(fixture, document);
        assertEquals(0, report.formulaControlsMatched());
        assertEquals(0, report.falseNegatives());
        assertTrue(report.diagnostics().contains("MISSING_OR_INCORRECT_FORMULA_CONTROL"));
    }

    @Test
    void meaningfulZeroAndFalseRemainValuesRatherThanAbsentData() throws IOException {
        var original = shield();
        var claim = new ExpectedMechanic("zero", MechanicFamily.ARMOR_CLASS_MODIFIER, "zero-model-test",
                new ArmorClassModifierMechanic(new IntegerModifier(0, "+0")),
                new ExpectedContext(new CasterSubject(), List.of(
                        new ExpectedDuration(new FixedDuration(DurationUnit.ROUND, 0, false)))),
                MechanicsCompleteness.COMPLETE_WITHIN_CLAIM, List.of(
                        ac().evidence().getFirst(),
                        new ExpectedEvidence(ac().evidence().getFirst().evidence(), "/context/bindings/0", EvidenceRole.QUALIFIER)));
        var fixture = BenchmarkTestDocuments.withContent(original, List.of(claim), List.of());
        var report = compare(fixture, BenchmarkTestDocuments.materialize(fixture));
        assertEquals(1, report.truePositives());
        String json = MechanicsJson.mapper().writeValueAsString(fixture);
        assertTrue(json.contains("\"upTo\":false"));
        assertTrue(json.contains("\"value\":0"));
    }

    @Test
    void numericHealingCannotReplaceASymbolicModifier() {
        var fixture = fixtures.get(3);
        var original = BenchmarkTestDocuments.materialize(fixture);
        var expected = fixture.mechanics().getFirst();
        var actual = original.interpretations().getFirst();
        var wrong = BenchmarkTestDocuments.record(fixture, expected,
                new HealingMechanic(new DiceAmount(1, 4, 0), HealingMode.RESTORE_HIT_POINTS), actual.context(), expected.evidence());
        var report = compare(fixture, BenchmarkTestDocuments.document(fixture, List.of(wrong), original.evidence(),
                original.unsupported(), CoverageStatus.PARTIAL));
        assertEquals(0, report.truePositives());
        assertEquals(1, report.falsePositives());
    }

    @Test
    void reportsAreDeterministicAndDefensivelyCopied() throws IOException {
        var actual = BenchmarkTestDocuments.materialize(shield());
        var report = compare(shield(), actual);
        var reversed = new ArrayList<>(actual.interpretations());
        Collections.reverse(reversed);
        var second = compare(shield(), BenchmarkTestDocuments.document(shield(), reversed,
                actual.evidence(), List.of(), CoverageStatus.PARTIAL));
        assertEquals(report, second);
        assertArrayEquals(MechanicsJson.mapper().writeValueAsBytes(report), MechanicsJson.mapper().writeValueAsBytes(second));
        assertThrows(UnsupportedOperationException.class, () -> report.claims().clear());
        assertThrows(UnsupportedOperationException.class, () -> report.diagnostics().clear());
        assertEquals(report, BenchmarkReport.ordered(List.of(second, report)).getFirst());
        assertEquals(5, report.patterns().size());
        assertTrue(report.patterns().stream().allMatch(value ->
                value.semanticRecall().correct() == value.semanticRecall().denominator()));
        assertThrows(UnsupportedOperationException.class, () -> report.patterns().clear());
    }

    @Test
    void wrongAbstentionReasonIsNotAnHonoredAbstention() {
        var fixture = fixtures.get(4);
        var original = BenchmarkTestDocuments.materialize(fixture);
        var changed = original.unsupported().stream().map(value -> new UnsupportedMechanicsConstruct(value.id(),
                value.candidateFamily(), UnsupportedReason.PROVENANCE_UNAVAILABLE, value.evidenceIds(), value.alternatives())).toList();
        var report = compare(fixture, BenchmarkTestDocuments.document(fixture, List.of(), original.evidence(),
                changed, CoverageStatus.PARTIAL));
        assertEquals(0, report.honoredAbstentions());
        assertEquals(3, report.unjustifiedAbstentions());
    }

    @Test
    void wrongSaveOutcomeDoesNotMatchDamageOnFailure() {
        var fixture = fixtures.get(2);
        var original = BenchmarkTestDocuments.materialize(fixture);
        var expectation = fixture.mechanics().get(1);
        var damage = original.interpretations().stream().filter(value -> value.payload() instanceof DamageMechanic)
                .findFirst().orElseThrow();
        var outcome = (AppliesOnSaveOutcome) damage.context().bindings().getFirst();
        var wrong = BenchmarkTestDocuments.record(fixture, expectation, damage.payload(), new MechanicContext(
                damage.context().subject(), List.of(new AppliesOnSaveOutcome(outcome.savingThrow(), SaveOutcome.SUCCESS,
                DamageOutcome.HALF_DAMAGE, outcome.evidenceIds()))), expectation.evidence());
        var records = original.interpretations().stream().map(value -> value.id().equals(damage.id()) ? wrong : value).toList();
        var report = compare(fixture, BenchmarkTestDocuments.document(fixture, records, original.evidence(),
                original.unsupported(), CoverageStatus.PARTIAL));
        assertEquals(1, report.truePositives());
        assertEquals(1, report.falseNegatives());
        assertTrue(report.completenessErrors() > 0);
    }

    @Test
    void reactionAlternativesAreOrderedAndCannotBeCollapsed() {
        var fixture = shield();
        var expected = fixture.mechanics().getFirst();
        var reaction = (ReactionActivationMechanic) expected.payload();
        var alternatives = ((AnyTrigger) reaction.trigger()).alternatives();
        var collapsed = BenchmarkTestDocuments.record(fixture, expected, new ReactionActivationMechanic(reaction.cost(),
                alternatives.getFirst()), new MechanicContext(new CasterSubject(), List.of()), expected.evidence());
        var report = compare(fixture, BenchmarkTestDocuments.document(fixture, List.of(collapsed),
                BenchmarkTestDocuments.evidence(fixture), List.of(), CoverageStatus.PARTIAL));
        assertEquals(0, report.truePositives());
        assertEquals(1, report.falsePositives());
        var reversed = new ArrayList<>(alternatives);
        Collections.reverse(reversed);
        assertNotEquals(reaction.trigger(), new AnyTrigger(reversed));
    }

    @Test
    void reportOrderingKeepsSpellEditionAndFixtureRevisionExplicit() {
        var phb = compare(shield(), BenchmarkTestDocuments.materialize(shield()));
        var xphb = compare(fixtures.get(1), BenchmarkTestDocuments.materialize(fixtures.get(1)));
        assertEquals(List.of(phb, xphb), BenchmarkReport.ordered(List.of(xphb, phb)));
        assertEquals("PHB", phb.owner().source().identity().code());
        assertEquals(shield().revision(), phb.fixtureRevision());
    }

    @Test
    void evidenceRequiresExactPointerResourceFingerprintAndTextHash() {
        var expected = (TextMechanicsEvidence) ac().evidence().getFirst().evidence();
        var location = expected.syntax().location();
        var sourceOwner = location.owner();
        var wrongOwners = List.of(
                new SyntaxSourceOwner("data/spells/another.json", sourceOwner.resourceSha256(),
                        sourceOwner.collection(), sourceOwner.jsonPointer()),
                new SyntaxSourceOwner(sourceOwner.resourcePath(), MechanicsHashes.sha256("wrong fingerprint"),
                        sourceOwner.collection(), sourceOwner.jsonPointer()));
        for (var wrongOwner : wrongOwners) {
            var wrong = new TextMechanicsEvidence("wrong-owner", expected.source(), new SyntaxEvidence(
                    new SyntaxTextLocation(wrongOwner, location.jsonPointer(), location.hierarchy(), location.origin()),
                    expected.syntax().span()), expected.textSha256(), expected.origin(), expected.authoredOwner());
            assertFalse(BenchmarkSemantics.evidenceMatches(expected, wrong));
        }
        var otherPointer = new SyntaxTextLocation(sourceOwner, sourceOwner.jsonPointer() + "/entries/1",
                List.of(), location.origin());
        var wrongPointer = new TextMechanicsEvidence("wrong-pointer", expected.source(),
                new SyntaxEvidence(otherPointer, expected.syntax().span()), expected.textSha256(),
                expected.origin(), expected.authoredOwner());
        assertFalse(BenchmarkSemantics.evidenceMatches(expected, wrongPointer));
        var wrongText = new TextMechanicsEvidence("wrong-text", expected.source(), expected.syntax(),
                MechanicsHashes.sha256("different text"), expected.origin(), expected.authoredOwner());
        assertFalse(BenchmarkSemantics.evidenceMatches(expected, wrongText));
    }

    @Test
    void evidencePoolIdsAreNotSemanticIdentity() {
        var expected = (TextMechanicsEvidence) ac().evidence().getFirst().evidence();
        var renamed = new TextMechanicsEvidence("another-local-pool-id", expected.source(), expected.syntax(),
                expected.textSha256(), expected.origin(), expected.authoredOwner());
        assertTrue(BenchmarkSemantics.evidenceMatches(expected, renamed));
    }

    @Test
    void staleAssociationFingerprintsCannotEarnEvidenceCredit() {
        var original = BenchmarkTestDocuments.materialize(shield());
        var actual = original.interpretations().stream().filter(value -> value.payload() instanceof ArmorClassModifierMechanic)
                .findFirst().orElseThrow();
        List<MechanicEvidenceAssociation> stale = new ArrayList<>(actual.evidenceAssociations());
        var first = stale.getFirst();
        stale.set(0, new MechanicEvidenceAssociation(first.evidenceId(), first.claimPath(), first.role(),
                MechanicsHashes.sha256("stale association fingerprint")));
        var wrong = MechanicRecord.propose(actual.id(), actual.payload(), actual.context(), stale,
                actual.derivation(), actual.assessment());
        // Canonical joining rejects this before the comparison engine can receive the document.
        assertThrows(IllegalArgumentException.class, () ->
                compare(shield(), replace(original, actual, wrong, original.evidence())));
    }

    private ExpectedMechanic ac() {
        return shield().mechanics().stream().filter(value -> value.family() == MechanicFamily.ARMOR_CLASS_MODIFIER)
                .findFirst().orElseThrow();
    }

    private MechanicsDocument replace(MechanicsDocument original, MechanicRecord replaced, MechanicRecord replacement,
                                       List<MechanicsEvidence> evidence) {
        return BenchmarkTestDocuments.document(shield(), original.interpretations().stream()
                        .map(value -> value.id().equals(replaced.id()) ? replacement : value).toList(),
                evidence, original.unsupported(), CoverageStatus.PARTIAL);
    }
}
