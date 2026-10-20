package com.thedndpub.data.mechanics.spell;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thedndpub.data.dto.spell.SpellId;
import com.thedndpub.data.mechanics.model.ActivatedBy;
import com.thedndpub.data.mechanics.model.AnyTrigger;
import com.thedndpub.data.mechanics.model.AppliesForDuration;
import com.thedndpub.data.mechanics.model.ArmorClassModifierMechanic;
import com.thedndpub.data.mechanics.model.AttackEventKind;
import com.thedndpub.data.mechanics.model.CasterSubject;
import com.thedndpub.data.mechanics.model.CoverageStatus;
import com.thedndpub.data.mechanics.model.DeclaredSpellDurationReference;
import com.thedndpub.data.mechanics.model.EffectDurationMechanic;
import com.thedndpub.data.mechanics.model.HitByAttackTrigger;
import com.thedndpub.data.mechanics.model.IncludesTriggeringAttack;
import com.thedndpub.data.mechanics.model.IntegerModifier;
import com.thedndpub.data.mechanics.model.MechanicRecord;
import com.thedndpub.data.mechanics.model.MechanicsCompleteness;
import com.thedndpub.data.mechanics.model.MechanicsDocument;
import com.thedndpub.data.mechanics.model.MechanicsEvidence;
import com.thedndpub.data.mechanics.model.MechanicsHashes;
import com.thedndpub.data.mechanics.model.MechanicsLicensingStatus;
import com.thedndpub.data.mechanics.model.MechanicsReferenceDisposition;
import com.thedndpub.data.mechanics.model.ProcessingState;
import com.thedndpub.data.mechanics.model.ReactionActivationMechanic;
import com.thedndpub.data.mechanics.model.SourceArtifactRevision;
import com.thedndpub.data.mechanics.model.SpellDamagePreventionMechanic;
import com.thedndpub.data.mechanics.model.SpellMentionForm;
import com.thedndpub.data.mechanics.model.StructuredFieldMechanicsEvidence;
import com.thedndpub.data.mechanics.model.TargetedBySpellTrigger;
import com.thedndpub.data.mechanics.model.TextMechanicsEvidence;
import com.thedndpub.data.mechanics.model.TurnActor;
import com.thedndpub.data.mechanics.model.TurnBoundary;
import com.thedndpub.data.mechanics.model.UntilTurnBoundary;
import com.thedndpub.data.mechanics.review.MechanicsReviewStatus;
import com.thedndpub.data.services.SpellCatalog;
import com.thedndpub.data.services.SpellFormulaAnnotationCatalog;
import com.thedndpub.data.services.SpellMechanicsInspectionCatalog;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ShieldMechanicsExtractorTests {
    @Autowired
    private SpellCatalog spells;
    @Autowired
    private SpellFormulaAnnotationCatalog formulas;
    @Autowired
    private SpellMechanicsInspectionCatalog inspection;
    @Autowired
    private ObjectMapper mapper;

    private Map<String, SpellMechanicsSourceOwner> owners;

    @BeforeAll
    void loadOwners() {
        owners = new SpellMechanicsDocumentFactory(mapper).loadOwners().stream()
                .collect(Collectors.toMap(value -> value.id().value(), Function.identity()));
    }

    private ShieldExtractionTestInput input(String edition) throws IOException {
        String id = "spell:" + edition + ":shield";
        return ShieldExtractionTestInput.actual(spells.findById(id), owners.get(id));
    }

    @Test
    void bothRealSourcesProduceFiveCompleteBoundedClaimsButPartialDocuments() throws IOException {
        for (String edition : List.of("phb", "xphb")) {
            var document = input(edition).extract(mapper);
            assertEquals(5, document.interpretations().size());
            assertEquals(ProcessingState.PROCESSED, document.processing().state());
            assertEquals(CoverageStatus.PARTIAL, document.coverage().status());
            assertFalse(document.coverage().unassessedSourcePaths().isEmpty());
            assertTrue(document.unsupported().isEmpty());
            assertTrue(document.interpretations().stream().allMatch(value ->
                    value.assessment().completeness() == MechanicsCompleteness.COMPLETE_WITHIN_CLAIM));
            assertEquals(Set.of("shield-reaction-v1", "shield-ac-v1", "spell-declared-duration-v1",
                    "shield-turn-boundary-v1", "shield-special-interaction-v1"), document.interpretations().stream()
                    .map(value -> value.derivation().patternId()).collect(Collectors.toSet()));
        }
    }

    @Test
    void armorClassValueSubjectAndAllThreeContextBindingsAreSourceBacked() throws IOException {
        for (String edition : List.of("phb", "xphb")) {
            var document = input(edition).extract(mapper);
            var claim = claim(document, "ac-bonus");
            assertEquals(new ArmorClassModifierMechanic(new IntegerModifier(5, "+5")), claim.payload());
            assertEquals(new CasterSubject(), claim.context().subject());
            var activation = claim(document, "activation");
            assertEquals(activation.revision().reference(activation.id()),
                    ((ActivatedBy) claim.context().bindings().getFirst()).activation());
            assertEquals(new UntilTurnBoundary(TurnActor.CASTER, TurnBoundary.START_OF_NEXT_TURN),
                    ((AppliesForDuration) claim.context().bindings().get(1)).duration());
            assertInstanceOf(IncludesTriggeringAttack.class, claim.context().bindings().get(2));
            assertEquals(5, claim.evidenceAssociations().size());
        }
    }

    @Test
    void reactionAlternativesPreserveEditionWordingAndMentionForms() throws IOException {
        for (String edition : List.of("phb", "xphb")) {
            var payload = (ReactionActivationMechanic) claim(input(edition).extract(mapper), "activation").payload();
            assertEquals(1, payload.cost().amount());
            var triggers = ((AnyTrigger) payload.trigger()).alternatives();
            assertEquals(2, triggers.size());
            assertEquals(edition.equals("phb") ? AttackEventKind.ATTACK_HIT : AttackEventKind.ATTACK_ROLL_HIT,
                    ((HitByAttackTrigger) triggers.getFirst()).event());
            var spell = ((TargetedBySpellTrigger) triggers.get(1)).spell();
            assertEquals(edition.equals("phb") ? SpellMentionForm.TAG_REFERENCE : SpellMentionForm.PROSE_MENTION, spell.form());
            assertNull(spell.authoredSource());
            assertNull(spell.targetId());
            assertEquals(MechanicsReferenceDisposition.NOT_ATTEMPTED, spell.resolution());
        }
    }

    @Test
    void protectionPreservesAuthoredQualificationWithoutGeneralImmunityOrResolution() throws IOException {
        var phb = (SpellDamagePreventionMechanic) claim(input("phb").extract(mapper), "magic-missile-protection").payload();
        var xphb = (SpellDamagePreventionMechanic) claim(input("xphb").extract(mapper), "magic-missile-protection").payload();
        assertEquals("magic missile", phb.spell().authoredName());
        assertNull(phb.spell().authoredSource());
        assertEquals("Magic Missile", xphb.spell().authoredName());
        assertEquals("XPHB", xphb.spell().authoredSource().code());
        assertNull(phb.spell().targetId());
        assertNull(xphb.spell().targetId());
    }

    @Test
    void structuredRoundAndProseTurnBoundaryRemainSeparateClaims() throws IOException {
        for (String edition : List.of("phb", "xphb")) {
            var source = input(edition);
            var document = source.extract(mapper);
            var declared = (EffectDurationMechanic) claim(document, "structured-duration").payload();
            var reference = (DeclaredSpellDurationReference) declared.specification();
            assertEquals(source.spell().id(), reference.spellId());
            assertEquals(0, reference.durationIndex());
            assertEquals("round", source.spell().durations().getFirst().duration().type());
            assertEquals(1, source.spell().durations().getFirst().duration().amount());
            assertEquals(new EffectDurationMechanic(new UntilTurnBoundary(TurnActor.CASTER, TurnBoundary.START_OF_NEXT_TURN)),
                    claim(document, "turn-boundary").payload());
            assertNotEquals(declared, claim(document, "turn-boundary").payload());
        }
    }

    @Test
    void everyEvidenceHashLocatorAndUtf16SpanReconstructsItsOwnSource() throws IOException {
        for (String edition : List.of("phb", "xphb")) {
            var input = input(edition);
            var document = input.extract(mapper);
            JsonNode original = mapper.readTree(input.bytes());
            assertEquals(document.evidence().size(), document.evidence().stream().map(MechanicsEvidence::id).distinct().count());
            for (var evidence : document.evidence()) {
                assertEquals(input.spell().source(), evidence.source());
                if (evidence instanceof TextMechanicsEvidence text) {
                    var location = text.syntax().location();
                    assertEquals(input.owner().revision().resourceSha256(), location.owner().resourceSha256());
                    assertEquals(input.owner().jsonPointer(), location.owner().jsonPointer());
                    String string = original.at(location.jsonPointer()).textValue();
                    assertEquals(MechanicsHashes.sha256(string), text.textSha256());
                    assertFalse(text.syntax().span().extract(string).isEmpty());
                    assertEquals(input.spell().id().value(), text.authoredOwner().id());
                } else if (evidence instanceof StructuredFieldMechanicsEvidence field) {
                    assertEquals(MechanicsHashes.sha256(original.at(field.jsonPointer()).toString()), field.valueSha256());
                    assertEquals(input.owner().revision().resourceSha256(), field.owner().resourceSha256());
                } else {
                    fail("Unexpected non-authored evidence");
                }
            }
        }
    }

    @Test
    void reviewAndLicensingRemainIndependentUnapprovedDefaults() throws IOException {
        var document = input("phb").extract(mapper);
        assertEquals(2, document.reviewSummary().pendingReview());
        assertEquals(3, document.reviewSummary().needsMechanicsReview());
        assertEquals(0, document.reviewSummary().verified());
        assertEquals(0, document.reviewSummary().rejected());
        for (var claim : document.interpretations()) {
            assertEquals(MechanicsReviewStatus.PENDING_REVIEW, claim.review().recordedStatus());
            assertNull(claim.review().humanDecision());
            assertEquals(MechanicsLicensingStatus.NEEDS_REVIEW, claim.licensing().status());
            assertTrue(claim.assessment().benchmarks().isEmpty());
        }
    }

    @Test
    void outputAndRevisionIdentitiesAreDeterministicImmutableAndSourceAware() throws IOException {
        var phb = input("phb");
        var document = phb.extract(mapper);
        assertEquals(document, phb.extract(mapper));
        assertEquals(document, inspection.documents().stream().filter(value -> value.owner().id().equals("spell:phb:shield"))
                .findFirst().orElseThrow());
        assertTrue(document.interpretations().stream().allMatch(value -> value.revision().number() == 1));
        assertTrue(document.interpretations().stream().allMatch(value -> value.id().value().startsWith("mechanic:spell:phb:shield:")));
        var xphb = input("xphb").extract(mapper);
        assertNotEquals(document.revision().contentSha256(), xphb.revision().contentSha256());
        assertThrows(UnsupportedOperationException.class, () -> document.interpretations().clear());
        assertThrows(UnsupportedOperationException.class, () -> document.evidence().clear());
    }

    @Test
    void missingReactionEvidenceAbstainsAndNeverCreatesDanglingActivationBindings() throws IOException {
        var changed = input("phb").changed(mapper, root -> ((ObjectNode) root.at("/time/0")).remove("condition"));
        var document = changed.extract(mapper);
        assertEquals(2, document.interpretations().size());
        assertEquals(3, document.unsupported().size());
        assertTrue(document.interpretations().stream().allMatch(value -> value.payload() instanceof EffectDurationMechanic));
        assertEquals(ProcessingState.PROCESSED, document.processing().state());
        assertEquals(CoverageStatus.PARTIAL, document.coverage().status());
        assertFalse(document.evidence().stream().anyMatch(value -> value instanceof TextMechanicsEvidence text
                && text.syntax().location().jsonPointer().endsWith("/condition")));
    }

    @Test
    void missingDurationPreservesOtherFourPatternsWithoutInventingAReference() throws IOException {
        var document = input("phb").changed(mapper, root -> root.remove("duration")).extract(mapper);
        assertEquals(4, document.interpretations().size());
        assertEquals(1, document.unsupported().size());
        assertTrue(document.interpretations().stream().noneMatch(value -> value.payload() instanceof EffectDurationMechanic duration
                && duration.specification() instanceof DeclaredSpellDurationReference));
    }

    @Test
    void assessedSourceWithNoSupportedPatternsIsNotMistakenForUnattemptedOrFailedProcessing() throws IOException {
        var changed = input("phb").changed(mapper, root -> {
            root.remove("time");
            root.remove("duration");
            root.remove("range");
            root.remove("entries");
        });
        var document = changed.extract(mapper);
        assertEquals(ProcessingState.PROCESSED, document.processing().state());
        assertEquals(CoverageStatus.NONE_SUPPORTED, document.coverage().status());
        assertEquals(5, document.unsupported().size());
        assertTrue(document.interpretations().isEmpty());
        assertTrue(document.evidence().stream().allMatch(StructuredFieldMechanicsEvidence.class::isInstance));
    }

    @Test
    void changedOrAmbiguousBodyTextDoesNotYieldCompleteGuessedClaims() throws IOException {
        var input = input("phb");
        for (String replacement : List.of("including only against some attacks", "including against the triggering attack if armed")) {
            var document = input.changed(mapper, root -> root.withArray("entries").set(0,
                    mapper.getNodeFactory().textNode(root.at("/entries/0").textValue()
                            .replace("including against the triggering attack", replacement)))).extract(mapper);
            assertEquals(2, document.interpretations().size());
            assertEquals(3, document.unsupported().size());
        }
        var repeated = input.changed(mapper, root -> root.withArray("entries").add(root.at("/entries/0"))).extract(mapper);
        assertEquals(2, repeated.interpretations().size());
        assertEquals(3, repeated.unsupported().size());
    }

    @Test
    void malformedAndUnsupportedTagsRemainUnsupportedRatherThanEffects() throws IOException {
        for (String replacement : List.of("{@spell magic missile", "{@damage 1d4}", "{@spell magic missile|PHB|extra}")) {
            var document = input("phb").changed(mapper, root -> root.withArray("entries").set(0,
                    mapper.getNodeFactory().textNode(root.at("/entries/0").textValue()
                            .replace("{@spell magic missile}", replacement)))).extract(mapper);
            assertEquals(2, document.interpretations().size());
            assertEquals(3, document.unsupported().size());
        }
    }

    @Test
    void attackWordingFromTheOtherEditionIsNotSilentlyNormalized() throws IOException {
        var document = input("xphb").changed(mapper, root -> ((ObjectNode) root.at("/time/0")).put("condition",
                root.at("/time/0/condition").textValue().replace("attack roll", "attack"))).extract(mapper);
        assertEquals(2, document.interpretations().size());
        assertEquals(3, document.unsupported().size());
    }

    @Test
    void valuesAreReadFromSourceRatherThanCopiedFromBenchmarks() throws IOException {
        var changed = input("phb").changed(mapper, root -> root.withArray("entries").set(0,
                mapper.getNodeFactory().textNode(root.at("/entries/0").textValue().replace("+5 bonus", "+6 bonus"))));
        var payload = (ArmorClassModifierMechanic) claim(changed.extract(mapper), "ac-bonus").payload();
        assertEquals(new IntegerModifier(6, "+6"), payload.amount());
        // Such revised input is never accepted by the pinned production entry point.
        assertThrows(IllegalArgumentException.class, () -> ShieldMechanicsEdition.PHB.validatePinned(changed.owner()));
    }

    @Test
    void incorrectFingerprintsAndCrossEditionOwnershipFailBeforeExtraction() throws IOException {
        var phb = input("phb");
        byte[] corrupted = phb.bytes();
        corrupted[0] = ' ';
        assertThrows(IllegalArgumentException.class, () ->
                new SpellMechanicsEvidenceSource(phb.spell(), phb.owner(), corrupted, mapper));
        var xphb = input("xphb");
        assertThrows(IllegalArgumentException.class, () -> new SpellMechanicsEvidenceSource(phb.spell(), xphb.owner(), xphb.bytes(), mapper));
        assertThrows(IllegalArgumentException.class, () -> new ShieldMechanicsExtractor().extract(phb.spell(),
                new SpellMechanicsEvidenceSource(phb.spell(), phb.owner(), phb.bytes(), mapper), xphb.seed()));
    }

    @Test
    void normalizedAndAuthoredTextMismatchFailsIntegrityInsteadOfReturningAnEmptyResult() throws IOException {
        var original = input("phb");
        var changed = original.changed(mapper, root -> root.withArray("entries").set(0,
                mapper.getNodeFactory().textNode("Unrecognized replacement")));
        assertThrows(IllegalArgumentException.class, () -> new ShieldMechanicsExtractor().extract(original.spell(),
                new SpellMechanicsEvidenceSource(original.spell(), changed.owner(), changed.bytes(), mapper), changed.seed()));
    }

    @Test
    void startupServiceLeavesOther848DocumentsAndAll930ObservationsUntouched() {
        var manifest = List.copyOf(owners.values());
        var initial = new SpellMechanicsDocumentFactory(mapper).unattempted(spells.findAll(), formulas.index(), manifest);
        var extracted = new SpellMechanicsExtractionService(mapper).extract(initial, spells.findAll(), manifest);
        assertEquals(969, extracted.size());
        assertEquals(124, extracted.stream().filter(value -> value.processing().state() == ProcessingState.PROCESSED).count());
        assertEquals(845, extracted.stream().filter(value -> value.processing().state() == ProcessingState.NOT_ATTEMPTED).count());
        Map<String, MechanicsDocument> before = initial.stream()
                .collect(Collectors.toMap(value -> value.owner().id(), Function.identity()));
        for (var document : extracted) {
            assertEquals(before.get(document.owner().id()).observations(), document.observations());
            if (document.processing().state() == ProcessingState.NOT_ATTEMPTED) {
                assertSame(before.get(document.owner().id()), document);
            }
        }
        assertEquals(930, extracted.stream().mapToInt(value -> value.observations().size()).sum());
        assertThrows(UnsupportedOperationException.class, () -> extracted.clear());
    }

    @Test
    void missingResourcesAndFingerprintChangesFailStartup() {
        var manifest = List.copyOf(owners.values());
        var initial = new SpellMechanicsDocumentFactory(mapper).unattempted(spells.findAll(), formulas.index(), manifest);
        var brokenResources = new DefaultResourceLoader() {
            @Override
            public Resource getResource(String location) {
                return new ByteArrayResource(new byte[0]);
            }
        };
        assertThrows(IllegalArgumentException.class, () -> new SpellMechanicsExtractionService(mapper, brokenResources)
                .extract(initial, spells.findAll(), manifest));
        var missingResources = new DefaultResourceLoader() {
            @Override
            public Resource getResource(String location) {
                return new ClassPathResource("data/spells/nonexistent-source.json");
            }
        };
        assertThrows(IllegalStateException.class, () -> new SpellMechanicsExtractionService(mapper, missingResources)
                .extract(initial, spells.findAll(), manifest));
        var wrong = manifest.stream().map(owner -> owner.id().equals(new SpellId("spell:phb:shield"))
                ? new SpellMechanicsSourceOwner(owner.id(), owner.source(), new SourceArtifactRevision(
                owner.revision().datasetVersion(), owner.revision().resourcePath(), MechanicsHashes.sha256("changed")),
                owner.jsonPointer()) : owner).toList();
        assertThrows(IllegalArgumentException.class, () -> new SpellMechanicsExtractionService(mapper)
                .extract(initial, spells.findAll(), wrong));
    }

    @Test
    void directExtractorRejectsOtherSpellsRatherThanClaimingTheyHaveNoMechanics() throws IOException {
        var definition = spells.findById("spell:phb:fireball");
        var input = ShieldExtractionTestInput.actual(definition, owners.get(definition.id().value()));
        assertThrows(IllegalArgumentException.class, () -> input.extract(mapper));
    }

    private MechanicRecord claim(MechanicsDocument document, String slot) {
        return document.interpretations().stream().filter(value -> value.id().value().endsWith(":" + slot)).findFirst().orElseThrow();
    }
}
