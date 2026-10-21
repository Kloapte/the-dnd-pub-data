package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thedndpub.data.dto.spell.SpellId;
import com.thedndpub.data.mechanics.review.MechanicsReviewAction;
import com.thedndpub.data.mechanics.review.MechanicsReviewStatus;
import com.thedndpub.data.mechanics.syntax.SyntaxContext;
import com.thedndpub.data.mechanics.syntax.SyntaxEvidence;
import com.thedndpub.data.mechanics.syntax.SyntaxOrigin;
import com.thedndpub.data.mechanics.syntax.SyntaxSourceOwner;
import com.thedndpub.data.mechanics.syntax.SyntaxSpan;
import com.thedndpub.data.mechanics.syntax.SyntaxTextLocation;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.thedndpub.data.mechanics.model.MechanicsModelFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

class MechanicsDocumentTests {
    @Test
    void proposedShieldDocumentRoundTripsWithoutAnExtractorOrPublicationGate() throws Exception {
        var timing = new AppliesForDuration(new UntilTurnBoundary(TurnActor.CASTER, TurnBoundary.START_OF_NEXT_TURN),
                List.of("body"));
        var record = proposal(PHB, "ac-bonus", ac().payload(),
                new MechanicContext(new CasterSubject(), List.of(timing, new IncludesTriggeringAttack(List.of("body")))),
                complete(), shieldBody());
        var doc = document(List.of(record));
        var mapper = MechanicsJson.mapper();
        String json = mapper.writeValueAsString(doc);
        assertEquals(doc, mapper.readValue(json, MechanicsDocument.class));
        assertEquals(json, mapper.writeValueAsString(doc));
        assertEquals(MechanicsLicensingStatus.NEEDS_REVIEW, doc.interpretations().getFirst().licensing().status());
        assertEquals(MechanicsReviewStatus.PENDING_REVIEW, doc.reviewSummary().aggregateStatus());
        assertFalse(json.contains("This spell"));
    }

    @Test
    void xphbConstructionRetainsItsSourceAndDifferentAttackEventKind() throws Exception {
        var body = body(XPHB, 304);
        var trigger = new AnyTrigger(List.of(new HitByAttackTrigger(AttackEventKind.ATTACK_ROLL_HIT),
                new TargetedBySpellTrigger(MechanicsValuesTests.unresolvedSpell())));
        var record = proposal(XPHB, "reaction", new ReactionActivationMechanic(new ReactionActionCost(1), trigger),
                new MechanicContext(new CasterSubject(), List.of()), complete(), body);
        var doc = document(XPHB, List.of(record), List.of(body), List.of(), List.of());
        assertEquals(doc, MechanicsJson.mapper().readValue(MechanicsJson.mapper().writeValueAsBytes(doc), MechanicsDocument.class));
        assertTrue(record.id().value().contains(":xphb:"));
        assertEquals("XPHB", doc.owner().source().identity().code());
        assertEquals(MechanicsReviewStatus.NEEDS_MECHANICS_REVIEW, doc.reviewSummary().aggregateStatus());
    }

    @Test
    void fireballAndHealingWordArePartialModelConstructionsWithSymbolicValues() throws Exception {
        var fireballOwner = owner("PHB", "fireball", 241);
        var fireballBody = body(fireballOwner, 139);
        var save = proposal(fireballOwner, "dex-save", new SavingThrowRequirementMechanic(new FixedAbility(Ability.DEXTERITY),
                new UnknownSaveDifficulty(UnknownValueReason.NOT_AUTHORED)),
                new MechanicContext(new UnknownSubject(UnknownValueReason.NOT_INTERPRETED), List.of()), complete(), fireballBody);
        var damage = proposal(fireballOwner, "failed-save-damage", new DamageMechanic(new DiceAmount(8, 6, 0), new FixedDamageType("fire")),
                new MechanicContext(new UnknownSubject(UnknownValueReason.NOT_INTERPRETED), List.of(
                        new AppliesOnSaveOutcome(save.revision().reference(save.id()), SaveOutcome.FAILURE, List.of("body")))),
                new MechanicsAssessment(MechanicsCompleteness.PARTIAL, MechanicsSupport.PROSE_INTERPRETATION, List.of(), List.of()),
                fireballBody);
        var fireball = document(fireballOwner, List.of(save, damage), List.of(fireballBody), List.of(), List.of());
        assertEquals(fireball, MechanicsJson.mapper().readValue(MechanicsJson.mapper().writeValueAsBytes(fireball), MechanicsDocument.class));
        assertEquals(0, fireball.reviewSummary().verified());

        var healingOwner = owner("PHB", "healing-word", 250);
        var healingBody = body(healingOwner, 175);
        var amount = new SumAmount(List.of(new DiceAmount(1, 4, 0),
                new SymbolicAmount(AmountSymbol.CASTER_SPELLCASTING_ABILITY_MODIFIER)));
        var healing = proposal(healingOwner, "healing", new HealingMechanic(amount, HealingMode.RESTORE_HIT_POINTS),
                new MechanicContext(new UnknownSubject(UnknownValueReason.NOT_INTERPRETED), List.of()), complete(), healingBody);
        var doc = document(healingOwner, List.of(healing), List.of(healingBody), List.of(), List.of());
        assertEquals(doc, MechanicsJson.mapper().readValue(MechanicsJson.mapper().writeValueAsBytes(doc), MechanicsDocument.class));
        assertEquals(2, amount.terms().size());
        assertEquals(MechanicsReviewStatus.NEEDS_MECHANICS_REVIEW, doc.reviewSummary().aggregateStatus());
    }

    @Test
    void emptyUnattemptedAndUnsupportedScopesAreDifferent() throws Exception {
        var unattempted = MechanicsDocument.create(PHB, 1, List.of(artifact("PHB")),
                new MechanicsProcessing(ProcessingState.NOT_ATTEMPTED, null, null), List.of(), List.of(), List.of(), List.of(),
                new MechanicsCoverage(CoverageStatus.NOT_ASSESSED, null, null, List.of(), List.of(), 0));
        var unsupported = new UnsupportedMechanicsConstruct("body-not-interpreted", MechanicFamily.UNDETERMINED,
                UnsupportedReason.NO_SUPPORTED_PATTERN, List.of("body"), List.of());
        var scanned = document(PHB, List.of(), List.of(shieldBody()), List.of(), List.of(unsupported));
        assertEquals(CoverageStatus.NOT_ASSESSED, unattempted.coverage().status());
        assertEquals(CoverageStatus.NONE_SUPPORTED, scanned.coverage().status());
        assertNull(scanned.reviewSummary().aggregateStatus());
        assertEquals(1, scanned.coverage().unsupportedCount());
        var mapper = MechanicsJson.mapper();
        assertEquals(unattempted, mapper.readValue(mapper.writeValueAsBytes(unattempted), MechanicsDocument.class));
        assertEquals(scanned, mapper.readValue(mapper.writeValueAsBytes(scanned), MechanicsDocument.class));
        assertThrows(IllegalArgumentException.class, () -> MechanicsDocument.create(PHB, 1, List.of(artifact("PHB")),
                unattempted.processing(), List.of(ac()), List.of(), List.of(), List.of(shieldBody()), scanned.coverage()));
    }

    @Test
    void contentRevisionRejectsEditedPayloadContextOrProvenance() throws Exception {
        var mapper = MechanicsJson.mapper();
        ObjectNode claim = mapper.valueToTree(ac());
        ((ObjectNode) claim.path("payload").path("amount")).put("value", 6).put("authoredToken", "+6");
        assertThrows(Exception.class, () -> mapper.treeToValue(claim, MechanicRecord.class));
        var record = ac();
        assertThrows(IllegalArgumentException.class, () -> new MechanicRecord(record.id(), record.revision(), record.payload(),
                new MechanicContext(new UnknownSubject(UnknownValueReason.NOT_INTERPRETED), List.of()),
                record.evidenceAssociations(), record.derivation(), record.assessment(), record.review(), record.licensing()));
        var changedDerivation = new MechanicDerivation(record.derivation().method(), "different-pattern", "v2", "test-only",
                List.of(), record.derivation().sources());
        assertThrows(IllegalArgumentException.class, () -> new MechanicRecord(record.id(), record.revision(), record.payload(),
                record.context(), record.evidenceAssociations(), changedDerivation, record.assessment(), record.review(), null));
        ObjectNode doc = mapper.valueToTree(document(List.of(record)));
        ((ObjectNode) doc.path("evidence").get(0)).put("textSha256", "0".repeat(64));
        assertThrows(Exception.class, () -> mapper.treeToValue(doc, MechanicsDocument.class));
    }

    @Test
    void associationsRequireRealClaimPathsAndJoinedEvidenceFingerprints() {
        var record = ac();
        assertThrows(IllegalArgumentException.class, () -> MechanicRecord.propose(record.id(), record.payload(), record.context(),
                List.of(MechanicEvidenceAssociation.of(shieldBody(), "/payload/not-present", EvidenceRole.VALUE)),
                record.derivation(), complete()));
        assertThrows(IllegalArgumentException.class, () -> MechanicRecord.propose(record.id(), record.payload(), record.context(),
                List.of(), record.derivation(), complete()));
        var bad = MechanicRecord.propose(record.id(), record.payload(), record.context(),
                List.of(new MechanicEvidenceAssociation("missing", "/payload", EvidenceRole.EFFECT_ROLE, HASH)),
                record.derivation(), complete());
        assertThrows(IllegalArgumentException.class, () -> document(List.of(bad)));
        assertThrows(IllegalArgumentException.class, () -> new MechanicEvidenceAssociation("body", "/payload-misleading",
                EvidenceRole.VALUE, HASH));
        assertThrows(IllegalArgumentException.class, () -> proposal(PHB, "ac-bonus", record.payload(),
                new MechanicContext(new CasterSubject(), List.of(new IncludesTriggeringAttack(List.of("missing")))),
                complete(), shieldBody()));
    }

    @Test
    void unchangedInheritedEvidenceKeepsItsAuthoredOwner() throws Exception {
        var original = body(XPHB, 304);
        var inherited = new TextMechanicsEvidence("body", XPHB.source(), original.syntax(), original.textSha256(),
                MechanicsOrigin.INHERITED_UNCHANGED, XPHB);
        var record = proposal(PHB, "ac-bonus", ac().payload(), ac().context(), complete(), inherited);
        var doc = MechanicsDocument.create(PHB, 1, List.of(artifact("PHB"), artifact("XPHB")),
                new MechanicsProcessing(ProcessingState.PROCESSED, "synthetic-inheritance-model-only", "v1"), List.of(record),
                List.of(), List.of(), List.of(inherited), new MechanicsCoverage(CoverageStatus.PARTIAL, "fixture", "v1",
                        List.of(), List.of(), 0));
        assertEquals(XPHB, ((TextMechanicsEvidence) doc.evidence().getFirst()).authoredOwner());
        assertEquals(PHB, doc.owner());
        assertEquals(doc, MechanicsJson.mapper().readValue(MechanicsJson.mapper().writeValueAsBytes(doc), MechanicsDocument.class));
        assertThrows(NullPointerException.class, () -> new TextMechanicsEvidence("body", XPHB.source(), original.syntax(), HASH,
                MechanicsOrigin.INHERITED_UNCHANGED, null));
    }

    @Test
    void transformedEvidenceRequiresBoundInputsAndNeverClaimsAnAuthoredOutputSpan() throws Exception {
        var original = shieldBody();
        var transformed = new TransformedMechanicsEvidence("generated", PHB.source(), PHB, "/normalized/entries/0", HASH,
                List.of(EvidenceRevisionReference.of(original)),
                List.of(new MechanicsTransformationReference("fixture-replacement", "synthetic-v1", "body")));
        var record = proposal(PHB, "ac-bonus", ac().payload(), ac().context(), complete(), transformed);
        var doc = document(PHB, List.of(record), List.of(original, transformed), List.of(), List.of());
        var mapper = MechanicsJson.mapper();
        assertEquals(doc, mapper.readValue(mapper.writeValueAsBytes(doc), MechanicsDocument.class));
        var json = mapper.readTree(mapper.writerFor(MechanicsEvidence.class).writeValueAsBytes(transformed));
        assertFalse(json.has("span"));
        assertFalse(json.has("jsonPointer"));
        assertEquals("/normalized/entries/0", json.path("effectivePointer").asText());
        assertThrows(IllegalArgumentException.class,
                () -> document(PHB, List.of(record), List.of(transformed), List.of(), List.of()));
        assertThrows(IllegalArgumentException.class, () -> new TransformedMechanicsEvidence("generated", PHB.source(), PHB,
                "/normalized", HASH, List.of(EvidenceRevisionReference.of(original)),
                List.of(new MechanicsTransformationReference("unknown-instruction", "v1", "missing"))));
    }

    @Test
    void unavailableOriginInvalidatesEffectiveVerificationButRetainsItsHistory() throws Exception {
        var unknown = new UnavailableOriginMechanicsEvidence("unknown", PHB.source(), PHB,
                "/normalized/entries/0", HASH, UnknownValueReason.PROVENANCE_UNAVAILABLE);
        var record = proposal(PHB, "ac-bonus", ac().payload(), ac().context(), complete(), unknown);
        var reviewed = reviewed(record, MechanicsReviewAction.VERIFY, false);
        var doc = document(PHB, List.of(reviewed), List.of(unknown), List.of(), List.of());
        assertEquals(MechanicsReviewStatus.VERIFIED, doc.interpretations().getFirst().review().recordedStatus());
        assertEquals(MechanicsReviewStatus.NEEDS_MECHANICS_REVIEW, doc.reviewSummary().aggregateStatus());
        var mapper = MechanicsJson.mapper(REVIEW_POLICY);
        assertEquals(doc, mapper.readValue(mapper.writeValueAsBytes(doc), MechanicsDocument.class));
        assertFalse(mapper.readTree(mapper.writerFor(MechanicsEvidence.class).writeValueAsBytes(unknown)).has("span"));
    }

    @Test
    void structuredFieldsHaveHierarchyButNoFakeTextSpanAndUtf16OffsetsRemainExact() throws Exception {
        var body = shieldBody();
        var field = new StructuredFieldMechanicsEvidence("duration", PHB.source(), body.syntax().location().owner(),
                "/spell/294/duration/0", List.of(new SyntaxContext("/spell/294", null)), HASH,
                MechanicsOrigin.AUTHORED, PHB);
        var mapper = MechanicsJson.mapper();
        String json = mapper.writerFor(MechanicsEvidence.class).writeValueAsString(field);
        assertFalse(mapper.readTree(json).has("span"));
        assertEquals(field, mapper.readValue(json, MechanicsEvidence.class));
        String synthetic = "a😀 +5";
        var owner = new SyntaxSourceOwner("data/model-example.json", HASH, "spell", "/spell/0");
        var location = new SyntaxTextLocation(owner, "/spell/0/entries/0", List.of(), SyntaxOrigin.AUTHORED);
        var unicode = new TextMechanicsEvidence("unicode", PHB.source(), new SyntaxEvidence(location, new SyntaxSpan(4, 6)),
                MechanicsHashes.sha256(synthetic), MechanicsOrigin.AUTHORED, PHB);
        assertEquals("+5", unicode.syntax().span().extract(synthetic));
        assertEquals(unicode, mapper.readValue(mapper.writerFor(MechanicsEvidence.class).writeValueAsBytes(unicode), MechanicsEvidence.class));
        assertThrows(IllegalArgumentException.class, () -> new StructuredFieldMechanicsEvidence("bad", PHB.source(), owner,
                "/other/0", List.of(), HASH, MechanicsOrigin.AUTHORED, PHB));
    }

    @Test
    void observationReferencesBindFormulaLocationsAndDoNotCreateEffects() throws Exception {
        var body = shieldBody();
        var declaration = new StructuredDeclarationReference("duration-source", "durations[0]", artifact("PHB"), "/spell/294/duration/0");
        var syntax = new OtherSyntaxObservationReference("spell-mention", body.syntax(), "spell");
        for (var observation : List.of(declaration, syntax)) {
            var mapper = MechanicsJson.mapper();
            assertEquals(observation, mapper.readValue(mapper.writerFor(MechanicsObservationReference.class)
                    .writeValueAsBytes(observation), MechanicsObservationReference.class));
        }
        var owner = owner("PHB", "fireball", 241);
        var fireballBody = body(owner, 139);
        var location = fireballBody.syntax().location();
        var formula = new FormulaObservationReference("fireball-formula-0", new SpellId(owner.id()), 0, artifact("PHB"),
                new SyntaxEvidence(location, new SyntaxSpan(262, 275)));
        var doc = document(owner, List.of(), List.of(fireballBody), List.of(formula), List.of());
        assertTrue(doc.interpretations().isEmpty());
        assertEquals(doc, MechanicsJson.mapper().readValue(MechanicsJson.mapper().writeValueAsBytes(doc), MechanicsDocument.class));
        assertThrows(IllegalArgumentException.class,
                () -> document(PHB, List.of(), List.of(shieldBody()), List.of(formula), List.of()));
    }

    @Test
    void duplicateIdsSourceMismatchesAndInvalidCompletenessAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> document(List.of(ac(), ac())));
        assertThrows(IllegalArgumentException.class, () -> document(PHB, List.of(ac()),
                List.of(shieldBody(), shieldBody()), List.of(), List.of()));
        assertThrows(IllegalArgumentException.class, () -> MechanicsDocument.create(PHB, 1,
                List.of(new SourceArtifactRevision("5eTools v2.37.0", artifact("PHB").resourcePath(), "0".repeat(64))),
                new MechanicsProcessing(ProcessingState.PROCESSED, "test", "v1"), List.of(ac()), List.of(), List.of(),
                List.of(shieldBody()), new MechanicsCoverage(CoverageStatus.PARTIAL, "test", "v1", List.of(), List.of(), 0)));
        assertThrows(NullPointerException.class, () -> new MechanicsCoverage(CoverageStatus.COMPLETE_WITHIN_SCOPE,
                "test", "v1", List.of(), List.of("/entries"), 0));
        assertThrows(IllegalArgumentException.class, () -> new MechanicsCoverage(CoverageStatus.COMPLETE_WITHIN_SCOPE,
                "test", "v1", List.of(), List.of(), 0, CoverageScopeExtent.FULL_SPELL));
    }

    @Test
    void contextReferencesRequireExactRevisionsAndCorrectKinds() {
        var target = ac();
        var wrongBinding = proposal(PHB, "damage", new DamageMechanic(new DiceAmount(1, 4, 0), new FixedDamageType("fire")),
                new MechanicContext(new CasterSubject(), List.of(new AppliesOnSaveOutcome(
                        target.revision().reference(target.id()), SaveOutcome.FAILURE, List.of("body")))), complete(), shieldBody());
        assertThrows(IllegalArgumentException.class, () -> document(List.of(target, wrongBinding)));
        var validSave = proposal(PHB, "valid-save", new SavingThrowRequirementMechanic(
                new FixedAbility(Ability.DEXTERITY), new FixedSaveDifficulty(12)), ac().context(), complete(),
                shieldBody());
        var wrongEffect = proposal(PHB, "non-damage-save-outcome", ac().payload(),
                new MechanicContext(new CasterSubject(), List.of(new AppliesOnSaveOutcome(
                        validSave.revision().reference(validSave.id()), SaveOutcome.FAILURE,
                        DamageOutcome.FULL_DAMAGE, List.of("body")))), complete(), shieldBody());
        assertThrows(IllegalArgumentException.class, () -> document(List.of(validSave, wrongEffect)));
        var activation = proposal(PHB, "activation", new ReactionActivationMechanic(new ReactionActionCost(1),
                new HitByAttackTrigger(AttackEventKind.ATTACK_HIT)), ac().context(), complete(), shieldBody());
        var bound = proposal(PHB, "ac-bonus", ac().payload(), new MechanicContext(new CasterSubject(), List.of(
                new ActivatedBy(activation.revision().reference(activation.id()), List.of("body")))), complete(), shieldBody());
        var verified = reviewed(bound, MechanicsReviewAction.VERIFY, false);
        var changed = revise(activation, new ReactionActivationMechanic(new ReactionActionCost(1),
                new HitByAttackTrigger(AttackEventKind.ATTACK_ROLL_HIT)));
        var doc = document(List.of(changed, verified));
        assertEquals(MechanicsReviewStatus.NEEDS_MECHANICS_REVIEW,
                doc.interpretations().get(1).review().effectiveStatus());
        assertEquals(MechanicsReviewStatus.VERIFIED, doc.interpretations().get(1).review().recordedStatus());
    }

    @Test
    void distinctRollOutcomesAreValidButConflictingDamageResultsForOneRollCannotBeVerified() {
        var save = proposal(PHB, "save", new SavingThrowRequirementMechanic(new FixedAbility(Ability.DEXTERITY),
                new FixedSaveDifficulty(12)), ac().context(), complete(), shieldBody());
        assertThrows(IllegalArgumentException.class, () -> new AppliesOnSaveOutcome(
                save.revision().reference(save.id()), SaveOutcome.FAILURE,
                DamageOutcome.HALF_DAMAGE, List.of("body")));
        assertThrows(IllegalArgumentException.class, () -> new AppliesOnSaveOutcome(
                save.revision().reference(save.id()), SaveOutcome.SUCCESS,
                DamageOutcome.FULL_DAMAGE, List.of("body")));
        var context = new MechanicContext(new CasterSubject(), List.of(
                new AppliesOnSaveOutcome(save.revision().reference(save.id()), SaveOutcome.FAILURE,
                        DamageOutcome.FULL_DAMAGE, List.of("body")),
                new AppliesOnSaveOutcome(save.revision().reference(save.id()), SaveOutcome.SUCCESS,
                        DamageOutcome.HALF_DAMAGE, List.of("body"))));
        var damage = proposal(PHB, "damage", new DamageMechanic(new DiceAmount(1, 4, 0), new FixedDamageType("fire")),
                context, complete(), shieldBody());
        assertEquals(MechanicsReviewStatus.VERIFIED,
                reviewed(damage, MechanicsReviewAction.VERIFY, false).review().effectiveStatus());

        var conflicting = proposal(PHB, "conflicting-damage", damage.payload(), new MechanicContext(new CasterSubject(),
                List.of(new AppliesOnSaveOutcome(save.revision().reference(save.id()), SaveOutcome.SUCCESS,
                                DamageOutcome.HALF_DAMAGE, List.of("body")),
                        new AppliesOnSaveOutcome(save.revision().reference(save.id()), SaveOutcome.SUCCESS,
                                DamageOutcome.NO_DAMAGE, List.of("body")))), complete(), shieldBody());
        assertEquals(MechanicsReviewStatus.NEEDS_MECHANICS_REVIEW,
                reviewed(conflicting, MechanicsReviewAction.VERIFY, false).review().effectiveStatus());
    }

    @Test
    void rejectedContextDependencyInvalidatesVerificationWithoutErasingTheDecision() throws Exception {
        var activation = proposal(PHB, "activation",
                new ReactionActivationMechanic(new ReactionActionCost(1),
                        new HitByAttackTrigger(AttackEventKind.ATTACK_HIT)),
                ac().context(), complete(), shieldBody());
        var bound = proposal(PHB, "ac-bonus", ac().payload(),
                new MechanicContext(new CasterSubject(), List.of(
                        new ActivatedBy(activation.revision().reference(activation.id()), List.of("body")))),
                complete(), shieldBody());
        var doc = document(List.of(
                reviewed(bound, MechanicsReviewAction.VERIFY, false),
                reviewed(activation, MechanicsReviewAction.REJECT, false)));

        var dependent = doc.interpretations().getFirst();
        assertEquals(MechanicsReviewStatus.VERIFIED, dependent.review().recordedStatus());
        assertEquals(MechanicsReviewStatus.NEEDS_MECHANICS_REVIEW, dependent.review().effectiveStatus());
        assertTrue(dependent.review().findings().stream()
                .anyMatch(finding -> finding.code() == MechanicsFindingCode.DEPENDENCY_REQUIRES_REVIEW));
        var mapper = MechanicsJson.mapper(REVIEW_POLICY);
        assertEquals(doc, mapper.readValue(mapper.writeValueAsBytes(doc), MechanicsDocument.class));
    }

    @Test
    void scopeCannotBeCompleteWithUnknownEssentialValuesOrMissingRoleEvidence() {
        var unknown = proposal(PHB, "healing",
                new HealingMechanic(new UnknownAmount(UnknownValueReason.NOT_INTERPRETED),
                        HealingMode.RESTORE_HIT_POINTS),
                ac().context(), complete(), shieldBody());
        var coverage = new MechanicsCoverage(CoverageStatus.COMPLETE_WITHIN_SCOPE,
                "test-only-scope", "v1", List.of("/spell/294/entries"), List.of(), 1,
                CoverageScopeExtent.FULL_SPELL);
        var unsupported = new UnsupportedMechanicsConstruct("unknown-amount", MechanicFamily.DAMAGE,
                UnsupportedReason.SOURCE_UNSPECIFIED, List.of("body"), List.of());

        var accountedWithGap = MechanicsDocument.create(
                PHB, 1, List.of(artifact("PHB")),
                new MechanicsProcessing(ProcessingState.PROCESSED, "model-construction-only", "v1"),
                List.of(unknown), List.of(), List.of(unsupported), List.of(shieldBody()), coverage);
        assertEquals(CoverageStatus.COMPLETE_WITHIN_SCOPE, accountedWithGap.coverage().status());
        assertEquals(1, accountedWithGap.coverage().unsupportedCount());
        assertThrows(IllegalArgumentException.class, () -> MechanicEvidenceAssociation.of(
                shieldBody(), "/payload/amount", EvidenceRole.EFFECT_ROLE));

        var cleanCoverage = new MechanicsCoverage(CoverageStatus.COMPLETE_WITHIN_SCOPE,
                "test-only-scope", "v1", List.of("/spell/294/entries"), List.of(), 0,
                CoverageScopeExtent.FULL_SPELL);
        var completeDocument = MechanicsDocument.create(
                PHB, 1, List.of(artifact("PHB")),
                new MechanicsProcessing(ProcessingState.PROCESSED, "model-construction-only", "v1"),
                List.of(ac()), List.of(), List.of(), List.of(shieldBody()), cleanCoverage);
        assertEquals(CoverageStatus.COMPLETE_WITHIN_SCOPE, completeDocument.coverage().status());
        assertEquals(MechanicsReviewStatus.PENDING_REVIEW, completeDocument.reviewSummary().aggregateStatus());
    }

    @Test
    void documentHashIsPermutationIndependentAndClaimHashesRemainClaimLocal() {
        var first = ac();
        var second = proposal(PHB, "duration", new EffectDurationMechanic(new FixedDuration(DurationUnit.ROUND, 1, null)),
                ac().context(), complete(), shieldBody());
        var source = List.of(artifact("PHB"));
        var processing = new MechanicsProcessing(ProcessingState.PROCESSED, "fixture", "v1");
        var coverage = new MechanicsCoverage(CoverageStatus.PARTIAL, "fixture", "v1", List.of(), List.of(), 0);
        var summary = com.thedndpub.data.mechanics.review.MechanicsReviewSummary.from(
                List.of(first.review(), second.review()));
        var ordered = new MechanicsDocumentContent(PHB, source, processing, List.of(first, second), List.of(), List.of(),
                List.of(shieldBody()), coverage, summary);
        var permuted = new MechanicsDocumentContent(PHB, source, processing, List.of(second, first), List.of(), List.of(),
                List.of(shieldBody()), coverage, summary);
        assertEquals(MechanicsHashes.documentHash(ordered), MechanicsHashes.documentHash(permuted));
        assertNotEquals(MechanicsHashes.documentHash(ordered, MechanicsHashes.LEGACY_DOCUMENT_HASH_PROTOCOL),
                MechanicsHashes.documentHash(permuted, MechanicsHashes.LEGACY_DOCUMENT_HASH_PROTOCOL));
        assertEquals(first.revision().contentSha256(), ordered.interpretations().getFirst().revision().contentSha256());
        assertEquals(MechanicsHashes.DOCUMENT_HASH_PROTOCOL, new MechanicsDocumentRevision(1,
                MechanicsHashes.documentHash(ordered), MechanicsHashes.DOCUMENT_HASH_PROTOCOL)
                .effectiveHashProtocolVersion());
        assertEquals(MechanicsHashes.LEGACY_DOCUMENT_HASH_PROTOCOL,
                new MechanicsDocumentRevision(1, MechanicsHashes.documentHash(ordered, 1))
                        .effectiveHashProtocolVersion());
        MechanicsDocument current = document(List.of(first, second));
        MechanicsDocumentContent currentContent = new MechanicsDocumentContent(current.owner(), current.sources(),
                current.processing(), current.interpretations(), current.observations(), current.unsupported(),
                current.evidence(), current.coverage(), current.reviewSummary());
        MechanicsDocument legacy = new MechanicsDocument(current.owner(), new MechanicsDocumentRevision(1,
                MechanicsHashes.documentHash(currentContent, MechanicsHashes.LEGACY_DOCUMENT_HASH_PROTOCOL)),
                current.sources(), current.processing(), current.interpretations(), current.observations(),
                current.unsupported(), current.evidence(), current.coverage(), current.reviewSummary());
        try {
            String json = MechanicsJson.mapper().writeValueAsString(legacy);
            assertFalse(MechanicsJson.mapper().readTree(json).path("revision").has("hashProtocolVersion"));
            assertEquals(legacy, MechanicsJson.mapper().readValue(json, MechanicsDocument.class));
        } catch (java.io.IOException exception) {
            throw new AssertionError(exception);
        }
    }

    @Test
    void genericDependencyGraphRejectsCyclesAndSelfReferences() {
        MechanicId a = MechanicId.of(PHB, "a");
        MechanicId b = MechanicId.of(PHB, "b");
        MechanicsDocumentValidator.validateDependencyGraph(java.util.Map.of(a, List.of(b), b, List.of()));
        assertThrows(IllegalArgumentException.class, () -> MechanicsDocumentValidator.validateDependencyGraph(
                java.util.Map.of(a, List.of(b), b, List.of(a))));
        assertThrows(IllegalArgumentException.class, () -> MechanicsDocumentValidator.validateDependencyGraph(
                java.util.Map.of(a, List.of(a))));
    }

    @Test
    void documentListsAndEvidenceHierarchyAreImmutableAndOrdered() throws Exception {
        var first = ac();
        var second = proposal(PHB, "duration", new EffectDurationMechanic(new FixedDuration(DurationUnit.ROUND, 1, null)),
                ac().context(), complete(), shieldBody());
        var records = new ArrayList<>(List.of(second, first));
        var doc = document(records);
        records.clear();
        assertEquals(List.of(second.id(), first.id()), doc.interpretations().stream().map(MechanicRecord::id).toList());
        assertThrows(UnsupportedOperationException.class, () -> doc.interpretations().clear());
        assertThrows(UnsupportedOperationException.class, () -> doc.evidence().clear());
        assertThrows(UnsupportedOperationException.class, () -> doc.sources().clear());
        assertThrows(UnsupportedOperationException.class, () -> first.evidenceAssociations().clear());
        assertThrows(UnsupportedOperationException.class, () -> shieldBody().syntax().location().hierarchy().clear());
        var mapper = MechanicsJson.mapper();
        assertEquals(mapper.writeValueAsString(doc), mapper.writeValueAsString(document(List.of(second, first))));
    }
}
