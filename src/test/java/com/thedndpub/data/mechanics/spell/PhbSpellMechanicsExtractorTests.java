package com.thedndpub.data.mechanics.spell;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.mechanics.model.Ability;
import com.thedndpub.data.mechanics.model.AmountSymbol;
import com.thedndpub.data.mechanics.model.AppliesOnSaveOutcome;
import com.thedndpub.data.mechanics.model.CoverageStatus;
import com.thedndpub.data.mechanics.model.DamageMechanic;
import com.thedndpub.data.mechanics.model.DiceAmount;
import com.thedndpub.data.mechanics.model.FixedAbility;
import com.thedndpub.data.mechanics.model.FixedDamageType;
import com.thedndpub.data.mechanics.model.HealingMechanic;
import com.thedndpub.data.mechanics.model.HealingMode;
import com.thedndpub.data.mechanics.model.MechanicRecord;
import com.thedndpub.data.mechanics.model.MechanicsCompleteness;
import com.thedndpub.data.mechanics.model.MechanicsDocument;
import com.thedndpub.data.mechanics.model.MechanicsHashes;
import com.thedndpub.data.mechanics.model.MechanicsLicensingStatus;
import com.thedndpub.data.mechanics.model.ProcessingState;
import com.thedndpub.data.mechanics.model.SaveOutcome;
import com.thedndpub.data.mechanics.model.SavingThrowRequirementMechanic;
import com.thedndpub.data.mechanics.model.StructuredFieldMechanicsEvidence;
import com.thedndpub.data.mechanics.model.SumAmount;
import com.thedndpub.data.mechanics.model.SymbolicAmount;
import com.thedndpub.data.mechanics.model.TextMechanicsEvidence;
import com.thedndpub.data.mechanics.model.UnknownSaveDifficulty;
import com.thedndpub.data.mechanics.model.UnknownSubject;
import com.thedndpub.data.mechanics.model.UnknownValueReason;
import com.thedndpub.data.mechanics.model.UnsupportedReason;
import com.thedndpub.data.mechanics.review.MechanicsReviewStatus;
import com.thedndpub.data.mechanics.inspection.EvidenceLocatorDto;
import com.thedndpub.data.mechanics.inspection.StructuredEvidenceLocatorDto;
import com.thedndpub.data.mechanics.inspection.TextEvidenceLocatorDto;
import com.thedndpub.data.services.SpellCatalog;
import com.thedndpub.data.services.SpellFormulaAnnotationCatalog;
import com.thedndpub.data.services.SpellMechanicsInspectionCatalog;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PhbSpellMechanicsExtractorTests {
    @Autowired
    private SpellCatalog spells;
    @Autowired
    private SpellMechanicsInspectionCatalog inspection;
    @Autowired
    private SpellFormulaAnnotationCatalog formulas;
    @Autowired
    private ObjectMapper mapper;
    private Map<String, SpellMechanicsSourceOwner> owners;

    @BeforeAll
    void loadSourceOwners() {
        owners = new SpellMechanicsDocumentFactory(mapper).loadOwners().stream()
                .collect(Collectors.toMap(owner -> owner.id().value(), Function.identity()));
    }

    private ShieldExtractionTestInput input(String slug) throws IOException {
        String id = "spell:phb:" + slug;
        return ShieldExtractionTestInput.actual(spells.findById(id), owners.get(id));
    }

    private MechanicsDocument extract(ShieldExtractionTestInput input) {
        return new PhbSpellMechanicsExtractor().extract(input.spell(),
                new SpellMechanicsEvidenceSource(input.spell(), input.owner(), input.bytes(), mapper), input.seed());
    }

    private MechanicRecord claim(MechanicsDocument document, String pattern) {
        return document.interpretations().stream()
                .filter(value -> value.derivation().patternId().equals(pattern)).findFirst().orElseThrow();
    }

    private String evidenceId(EvidenceLocatorDto evidence) {
        if (evidence instanceof TextEvidenceLocatorDto text) {
            return text.id();
        }
        if (evidence instanceof StructuredEvidenceLocatorDto structured) {
            return structured.id();
        }
        throw new AssertionError("Unexpected evidence locator type: " + evidence.getClass().getName());
    }

    @Test
    void fireballPreservesEightD6FireDamageOnFailureOnly() throws IOException {
        var document = extract(input("fireball"));
        assertEquals(2, document.interpretations().size());
        var record = claim(document, "fireball-damage-v1");
        var damage = assertInstanceOf(DamageMechanic.class, record.payload());
        assertEquals(new DiceAmount(8, 6, 0), damage.amount());
        assertEquals(new FixedDamageType("fire"), damage.damageType());
        var outcome = assertInstanceOf(AppliesOnSaveOutcome.class, record.context().bindings().getFirst());
        assertEquals(SaveOutcome.FAILURE, outcome.outcome());
        var save = claim(document, "fireball-save-v1");
        assertEquals(save.revision().reference(save.id()), outcome.savingThrow());
        assertInstanceOf(UnknownSubject.class, record.context().subject());
        assertEquals(MechanicsCompleteness.PARTIAL, record.assessment().completeness());
    }

    @Test
    void generalizedDamagePatternPreservesEachSelectedFormulaTypeAndSaveAbility() {
        Map<String, String> expected = Map.of(
                "burning-hands", "fire",
                "cone-of-cold", "cold",
                "shatter", "thunder",
                "lightning-bolt", "lightning",
                "circle-of-death", "necrotic");
        expected.forEach((slug, damageType) -> {
            var document = inspection.findById(spells.findById("spell:phb:" + slug).id());
            var damage = document.interpretations().stream()
                    .filter(value -> value.payload() instanceof DamageMechanic)
                    .map(value -> (DamageMechanic) value.payload())
                    .findFirst().orElseThrow();
            var save = document.interpretations().stream()
                    .filter(value -> value.payload() instanceof SavingThrowRequirementMechanic)
                    .findFirst().orElseThrow();
            assertEquals(damageType, ((FixedDamageType) damage.damageType()).authoredCode(), slug);
            var outcome = assertInstanceOf(AppliesOnSaveOutcome.class,
                    document.interpretations().stream()
                            .filter(value -> value.payload() instanceof DamageMechanic)
                            .findFirst().orElseThrow().context().bindings().getFirst());
            assertEquals(save.id(), outcome.savingThrow().id(), slug);
            assertEquals(SaveOutcome.FAILURE, outcome.outcome(), slug);
            assertEquals(MechanicsCompleteness.PARTIAL, document.interpretations().stream()
                    .filter(value -> value.payload() instanceof DamageMechanic)
                    .findFirst().orElseThrow().assessment().completeness(), slug);
            assertTrue(document.unsupported().stream().anyMatch(value -> value.id().contains("successful-save-half")), slug);
        });
    }

    @Test
    void fireballDexteritySaveHasNoInventedNumericDifficulty() throws IOException {
        var record = claim(extract(input("fireball")), "fireball-save-v1");
        var save = assertInstanceOf(SavingThrowRequirementMechanic.class, record.payload());
        assertEquals(new FixedAbility(Ability.DEXTERITY), save.ability());
        assertEquals(new UnknownSaveDifficulty(UnknownValueReason.NOT_AUTHORED), save.difficulty());
        assertEquals(MechanicsCompleteness.PARTIAL, record.assessment().completeness());
    }

    @Test
    void fireballReportsHalfDamageAreaAndScalingAsUnsupported() throws IOException {
        var document = extract(input("fireball"));
        assertEquals(3, document.unsupported().size());
        assertEquals(java.util.List.of("successful-save-half", "area-targets", "slot-scaling"),
                document.unsupported().stream().map(value -> value.id()).toList());
        assertTrue(document.unsupported().stream().allMatch(value -> value.reason() == UnsupportedReason.FAMILY_NOT_IMPLEMENTED));
        assertEquals(CoverageStatus.PARTIAL, document.coverage().status());
        assertEquals(3, document.coverage().unsupportedCount());
        assertFalse(document.coverage().unassessedSourcePaths().isEmpty());
        assertEquals(1, document.interpretations().stream().filter(value -> value.payload() instanceof DamageMechanic).count());
    }

    @Test
    void healingWordPreservesTheOrderedDiceAndSymbolicSum() throws IOException {
        var document = extract(input("healing-word"));
        assertEquals(1, document.interpretations().size());
        var healing = assertInstanceOf(HealingMechanic.class, document.interpretations().getFirst().payload());
        var sum = assertInstanceOf(SumAmount.class, healing.amount());
        assertEquals(new DiceAmount(1, 4, 0), sum.terms().get(0));
        assertEquals(new SymbolicAmount(AmountSymbol.CASTER_SPELLCASTING_ABILITY_MODIFIER), sum.terms().get(1));
        assertEquals(HealingMode.RESTORE_HIT_POINTS, healing.mode());
        assertEquals(2, document.unsupported().size());
        assertEquals(MechanicsCompleteness.PARTIAL, document.interpretations().getFirst().assessment().completeness());
    }

    @Test
    void healingEvidenceIncludesBothTermsAndTheirHealingRole() throws IOException {
        var input = input("healing-word");
        var document = extract(input);
        var claim = document.interpretations().getFirst();
        var association = claim.evidenceAssociations().getFirst();
        var evidence = (TextMechanicsEvidence) document.evidence().stream()
                .filter(value -> value.id().equals(association.evidenceId())).findFirst().orElseThrow();
        String original = mapper.readTree(input.bytes()).at(evidence.syntax().location().jsonPointer()).textValue();
        assertEquals("regains hit points equal to {@dice 1d4} + your spellcasting ability modifier",
                evidence.syntax().span().extract(original));
    }

    @Test
    void everyEvidenceLocationFingerprintAndUtf16SpanReconstructsItsOwnOwner() throws IOException {
        for (String slug : java.util.List.of("fireball", "healing-word", "burning-hands", "cone-of-cold",
                "shatter", "lightning-bolt", "circle-of-death", "cure-wounds", "mass-cure-wounds",
                "mass-healing-word", "prayer-of-healing")) {
            var input = input(slug);
            var document = extract(input);
            var original = mapper.readTree(input.bytes());
            assertEquals(document.evidence().size(), document.evidence().stream().map(value -> value.id()).distinct().count());
            for (var evidence : document.evidence()) {
                assertEquals(input.spell().source(), evidence.source());
                if (evidence instanceof TextMechanicsEvidence text) {
                    var location = text.syntax().location();
                    assertEquals(input.owner().revision().resourceSha256(), location.owner().resourceSha256());
                    assertEquals(input.owner().jsonPointer(), location.owner().jsonPointer());
                    assertEquals(input.spell().id().value(), text.authoredOwner().id());
                    String string = original.at(location.jsonPointer()).textValue();
                    assertEquals(MechanicsHashes.sha256(string), text.textSha256());
                    assertFalse(text.syntax().span().extract(string).isEmpty());
                } else {
                    var field = assertInstanceOf(StructuredFieldMechanicsEvidence.class, evidence);
                    assertEquals(MechanicsHashes.sha256(original.at(field.jsonPointer()).toString()), field.valueSha256());
                }
            }
        }
    }

    @Test
    void changedGeneralizedSourceWordingAbstainsInsteadOfKeepingAStaleClaim() throws IOException {
        var input = input("cure-wounds").changed(mapper, root -> root.withArray("entries").set(0,
                mapper.getNodeFactory().textNode(root.at("/entries/0").textValue()
                        .replace("spellcasting ability modifier", "spellcasting modifier"))));
        var document = extract(input);
        assertTrue(document.interpretations().isEmpty());
        assertEquals(CoverageStatus.NONE_SUPPORTED, document.coverage().status());
        assertTrue(document.unsupported().stream().anyMatch(value -> value.reason()
                == UnsupportedReason.NO_SUPPORTED_PATTERN));
    }

    @Test
    void missingSymbolicModifierNeverBecomesDiceOnlyHealing() throws IOException {
        var input = input("healing-word").changed(mapper, root -> root.withArray("entries").set(0,
                mapper.getNodeFactory().textNode(root.at("/entries/0").textValue()
                        .replace(" + your spellcasting ability modifier", ""))));
        var document = extract(input);
        assertTrue(document.interpretations().isEmpty());
        assertEquals(CoverageStatus.NONE_SUPPORTED, document.coverage().status());
        assertEquals(3, document.unsupported().size());
    }

    @Test
    void changedSaveOutcomeCannotBecomeUnconditionalDamage() throws IOException {
        var input = input("fireball").changed(mapper, root -> root.withArray("entries").set(0,
                mapper.getNodeFactory().textNode(root.at("/entries/0").textValue().replace("on a failed save", "on any save"))));
        var document = extract(input);
        assertTrue(document.interpretations().isEmpty());
        assertEquals(5, document.unsupported().size());
    }

    @Test
    void unsupportedFormulaArgumentsAndMalformedTagsAbstain() throws IOException {
        for (String replacement : java.util.List.of("{@damage 8d6|extra}", "{@damage 8d6", "{@dice 8d6}", "{@damage PBd6}")) {
            var input = input("fireball").changed(mapper, root -> root.withArray("entries").set(0,
                    mapper.getNodeFactory().textNode(root.at("/entries/0").textValue().replace("{@damage 8d6}", replacement))));
            assertTrue(extract(input).interpretations().isEmpty(), replacement);
        }
    }

    @Test
    void sourceBasedParsingReadsChangedDiceInsteadOfFixtureConstants() throws IOException {
        var input = input("healing-word").changed(mapper, root -> root.withArray("entries").set(0,
                mapper.getNodeFactory().textNode(root.at("/entries/0").textValue().replace("1d4", "2d8"))));
        var healing = (HealingMechanic) extract(input).interpretations().getFirst().payload();
        assertEquals(new DiceAmount(2, 8, 0), ((SumAmount) healing.amount()).terms().getFirst());
        assertThrows(IllegalArgumentException.class, () -> ShieldMechanicsEdition.PHB.validateSourceRevision(input.owner()));
    }

    @Test
    void missingSourceAndNormalizedDisagreementCannotCreateFabricatedEvidence() throws IOException {
        var input = input("fireball");
        var missing = input.changed(mapper, root -> root.remove("entries"));
        var document = extract(missing);
        assertTrue(document.interpretations().isEmpty());
        assertTrue(document.evidence().stream().allMatch(value -> value instanceof StructuredFieldMechanicsEvidence
                || ((TextMechanicsEvidence) value).syntax().location().jsonPointer().contains("entriesHigherLevel")));
        assertThrows(IllegalArgumentException.class, () -> new PhbSpellMechanicsExtractor().extract(input.spell(),
                new SpellMechanicsEvidenceSource(input.spell(), missing.owner(), missing.bytes(), mapper), missing.seed()));
    }

    @Test
    void wrongFingerprintAndCrossOwnerDocumentsFailIntegrity() throws IOException {
        var fireball = input("fireball");
        var healing = input("healing-word");
        assertThrows(IllegalArgumentException.class, () -> new SpellMechanicsEvidenceSource(
                fireball.spell(), fireball.owner(), new byte[0], mapper));
        assertThrows(IllegalArgumentException.class, () -> new PhbSpellMechanicsExtractor().extract(fireball.spell(),
                new SpellMechanicsEvidenceSource(fireball.spell(), fireball.owner(), fireball.bytes(), mapper), healing.seed()));
    }

    @Test
    void deterministicImmutableClaimsHavePendingReviewAndUnreviewedRights() throws IOException {
        for (String slug : java.util.List.of("fireball", "healing-word")) {
            var input = input(slug);
            var document = extract(input);
            assertEquals(document, extract(input));
            assertThrows(UnsupportedOperationException.class, () -> document.evidence().clear());
            assertThrows(UnsupportedOperationException.class, () -> document.interpretations().clear());
            for (var claim : document.interpretations()) {
                assertEquals(MechanicsReviewStatus.PENDING_REVIEW, claim.review().recordedStatus());
                assertEquals(MechanicsReviewStatus.NEEDS_MECHANICS_REVIEW, claim.review().effectiveStatus());
                assertEquals(MechanicsLicensingStatus.NEEDS_REVIEW, claim.licensing().status());
                assertNull(claim.review().humanDecision());
                assertTrue(claim.assessment().benchmarks().isEmpty());
            }
        }
    }

    @Test
    void registeredPatternsProcessThirteenOwnersAndPreserveAllFormulaObservations() {
        assertEquals(969, inspection.size());
        assertEquals(13, inspection.documents().stream().filter(value -> value.processing().state() == ProcessingState.PROCESSED).count());
        assertEquals(956, inspection.documents().stream().filter(value -> value.processing().state() == ProcessingState.NOT_ATTEMPTED).count());
        assertEquals(27, inspection.documents().stream().mapToInt(value -> value.interpretations().size()).sum());
        assertEquals(930, inspection.documents().stream().mapToInt(value -> value.observations().size()).sum());
        for (String slug : java.util.List.of("fireball", "healing-word")) {
            var id = spells.findById("spell:phb:" + slug).id();
            assertEquals(2, inspection.findById(id).observations().size());
            assertEquals(2, formulas.index().bySpellId().get(id).size());
        }
    }

    @Test
    void sharedDamageAndHealingPatternsProduceSourceBackedClaimsForNineAdditionalOwners() {
        for (String slug : java.util.List.of("burning-hands", "cone-of-cold", "shatter", "lightning-bolt",
                "circle-of-death")) {
            var document = inspection.findById(spells.findById("spell:phb:" + slug).id());
            assertEquals(ProcessingState.PROCESSED, document.processing().state(), slug);
            assertEquals(2, document.interpretations().size(), slug);
            assertTrue(document.interpretations().stream().allMatch(value ->
                    value.review().recordedStatus().name().equals("PENDING_REVIEW")
                            && value.review().effectiveStatus().name().equals("NEEDS_MECHANICS_REVIEW")
                            && value.licensing().status().name().equals("NEEDS_REVIEW")), slug);
            assertTrue(document.interpretations().stream().anyMatch(value -> value.payload()
                    instanceof com.thedndpub.data.mechanics.model.SavingThrowRequirementMechanic), slug);
            assertTrue(document.interpretations().stream().anyMatch(value -> value.payload()
                    instanceof com.thedndpub.data.mechanics.model.DamageMechanic), slug);
            assertTrue(document.interpretations().stream()
                    .allMatch(value -> !value.evidenceAssociations().isEmpty()), slug);
            assertTrue(document.evidence().stream().allMatch(value -> evidenceId(value).startsWith("spell-")), slug);
        }
        for (String slug : java.util.List.of("cure-wounds", "mass-cure-wounds", "mass-healing-word", "prayer-of-healing")) {
            var document = inspection.findById(spells.findById("spell:phb:" + slug).id());
            assertEquals(ProcessingState.PROCESSED, document.processing().state(), slug);
            assertEquals(1, document.interpretations().size(), slug);
            var claim = document.interpretations().getFirst();
            assertEquals("PENDING_REVIEW", claim.review().recordedStatus().name(), slug);
            assertEquals("NEEDS_MECHANICS_REVIEW", claim.review().effectiveStatus().name(), slug);
            assertEquals("NEEDS_REVIEW", claim.licensing().status().name(), slug);
            assertInstanceOf(com.thedndpub.data.mechanics.model.HealingMechanic.class,
                    document.interpretations().getFirst().payload());
            assertTrue(document.interpretations().getFirst().evidenceAssociations().stream()
                    .anyMatch(value -> value.role()
                            == com.thedndpub.data.mechanics.model.EvidenceRole.EFFECT_ROLE), slug);
            assertTrue(document.evidence().stream().allMatch(value -> evidenceId(value).startsWith("spell-")), slug);
        }
    }

    @Test
    void newAdapterRejectsNonPilotOwners() throws IOException {
        var input = ShieldExtractionTestInput.actual(spells.findById("spell:phb:magic-missile"),
                owners.get("spell:phb:magic-missile"));
        assertThrows(IllegalArgumentException.class, () -> extract(input));
    }
}
