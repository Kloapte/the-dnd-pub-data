package com.thedndpub.data.mechanics.spell;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.mechanics.model.Ability;
import com.thedndpub.data.mechanics.model.AmountSymbol;
import com.thedndpub.data.mechanics.model.AppliesOnSaveOutcome;
import com.thedndpub.data.mechanics.model.CoverageStatus;
import com.thedndpub.data.mechanics.model.ConditionChangeMechanic;
import com.thedndpub.data.mechanics.model.ConditionChoiceMechanic;
import com.thedndpub.data.mechanics.model.ConditionName;
import com.thedndpub.data.mechanics.model.ConditionOperation;
import com.thedndpub.data.mechanics.model.DamageMechanic;
import com.thedndpub.data.mechanics.model.DamageOutcome;
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
import com.thedndpub.data.mechanics.model.ScalingEffect;
import com.thedndpub.data.mechanics.model.SavingThrowRequirementMechanic;
import com.thedndpub.data.mechanics.model.SpellSlotScalingMechanic;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    void fireballBindsFullFailureAndHalfSuccessToTheSameSaveAndDamageEffect() throws IOException {
        var document = extract(input("fireball"));
        assertEquals(3, document.interpretations().size());
        var record = claim(document, "fireball-damage-v1");
        var damage = assertInstanceOf(DamageMechanic.class, record.payload());
        assertEquals(new DiceAmount(8, 6, 0), damage.amount());
        assertEquals(new FixedDamageType("fire"), damage.damageType());
        var outcome = assertInstanceOf(AppliesOnSaveOutcome.class, record.context().bindings().getFirst());
        assertEquals(SaveOutcome.FAILURE, outcome.outcome());
        assertEquals(DamageOutcome.FULL_DAMAGE, outcome.damageOutcome());
        var successfulOutcome = assertInstanceOf(AppliesOnSaveOutcome.class, record.context().bindings().get(1));
        assertEquals(SaveOutcome.SUCCESS, successfulOutcome.outcome());
        assertEquals(DamageOutcome.HALF_DAMAGE, successfulOutcome.damageOutcome());
        var save = claim(document, "fireball-save-v1");
        assertEquals(save.revision().reference(save.id()), outcome.savingThrow());
        assertEquals(outcome.savingThrow(), successfulOutcome.savingThrow());
        assertTrue(record.evidenceAssociations().stream().anyMatch(value -> value.claimPath().equals("/context/bindings/1")
                && value.role() == com.thedndpub.data.mechanics.model.EvidenceRole.QUALIFIER));
        assertInstanceOf(UnknownSubject.class, record.context().subject());
        assertEquals(MechanicsCompleteness.PARTIAL, record.assessment().completeness());
    }

    @Test
    void blindnessDeafnessPreservesExclusiveChoiceAndFailedSaveQualifier() throws IOException {
        var document = extract(input("blindness-deafness"));
        var choice = document.interpretations().stream()
                .filter(value -> value.payload() instanceof ConditionChoiceMechanic)
                .findFirst().orElseThrow();
        var payload = (ConditionChoiceMechanic) choice.payload();
        assertEquals(ConditionOperation.APPLY, payload.operation());
        assertEquals(java.util.List.of(ConditionName.BLINDED, ConditionName.DEAFENED), payload.alternatives());
        assertEquals(1, payload.choicesToSelect());
        var individual = document.interpretations().stream()
                .filter(value -> value.payload() instanceof ConditionChangeMechanic)
                .toList();
        assertEquals(2, individual.size());
        assertTrue(individual.stream().allMatch(value ->
                ((ConditionChangeMechanic) value.payload()).choiceGroup().equals(choice.revision().reference(choice.id()))));
        var qualifiers = individual.stream().flatMap(value ->
                ((ConditionChangeMechanic) value.payload()).qualifiers().stream()).toList();
        assertTrue(qualifiers.stream().anyMatch(value -> value.contains("fails")));
        assertTrue(qualifiers.stream().anyMatch(value -> value.contains("for the duration")));
        assertEquals(MechanicsCompleteness.PARTIAL, choice.assessment().completeness());
        assertTrue(choice.evidenceAssociations().stream().anyMatch(value -> value.claimPath().equals("/payload/alternatives/0")));
        assertTrue(choice.evidenceAssociations().stream().anyMatch(value ->
                value.claimPath().equals("/payload/choicesToSelect")));
        assertTrue(individual.stream().allMatch(value -> value.evidenceAssociations().stream().anyMatch(association ->
                association.claimPath().equals("/payload/choiceGroup")
                        && association.role() == com.thedndpub.data.mechanics.model.EvidenceRole.REFERENCE)));
        assertEquals(document.interpretations().stream().map(value -> value.id().value()).toList(),
                extract(input("blindness-deafness")).interpretations().stream()
                        .map(value -> value.id().value()).toList());
    }

    @Test
    void simultaneousHypnoticPatternConditionsRemainIndependent() throws IOException {
        var document = extract(input("hypnotic-pattern"));
        var conditions = document.interpretations().stream()
                .filter(value -> value.payload() instanceof ConditionChangeMechanic)
                .map(value -> (ConditionChangeMechanic) value.payload()).toList();
        assertTrue(conditions.stream().anyMatch(value -> value.condition() == ConditionName.CHARMED));
        assertTrue(conditions.stream().anyMatch(value -> value.condition() == ConditionName.INCAPACITATED));
        assertTrue(conditions.stream().allMatch(value -> value.choiceGroup() == null));
        assertFalse(document.interpretations().stream().anyMatch(value -> value.payload() instanceof ConditionChoiceMechanic));
    }

    @Test
    void fireballEvidenceUsesGenericIdsAndRetainsValidLegacyAliases() throws IOException {
        var document = extract(input("fireball"));
        var damage = claim(document, "fireball-damage-v1");
        var saveOutcomeIds = damage.context().bindings().stream()
                .filter(AppliesOnSaveOutcome.class::isInstance).map(AppliesOnSaveOutcome.class::cast)
                .flatMap(value -> value.evidenceIds().stream()).toList();
        assertFalse(saveOutcomeIds.isEmpty());
        assertTrue(damage.evidenceAssociations().stream().allMatch(value -> value.evidenceId().startsWith("spell-")));
        assertTrue(saveOutcomeIds.stream().allMatch(id -> damage.evidenceAssociations().stream()
                .anyMatch(value -> value.evidenceId().equals(id))));
        var evidenceIds = document.evidence().stream().map(value -> value.id()).collect(Collectors.toSet());
        assertTrue(document.interpretations().stream().flatMap(value -> value.evidenceAssociations().stream())
                .allMatch(value -> value.evidenceId().startsWith("spell-") && evidenceIds.contains(value.evidenceId())));
        assertTrue(document.evidence().stream().filter(TextMechanicsEvidence.class::isInstance)
                .map(TextMechanicsEvidence.class::cast).allMatch(value ->
                        value.syntax().location().jsonPointer().startsWith("/")
                                && value.syntax().span().endExclusive() >= value.syntax().span().startInclusive()));
        assertTrue(document.evidence().stream().anyMatch(value -> value.id().startsWith("shield-text:")));
        assertTrue(document.evidence().stream().anyMatch(value -> value.id().startsWith("shield-field:")));
        assertTrue(document.evidence().stream().filter(value -> value.id().startsWith("spell-")).allMatch(value ->
                value instanceof TextMechanicsEvidence || value instanceof StructuredFieldMechanicsEvidence));
        assertEquals(document.evidence().stream().map(value -> value.id()).toList(),
                extract(input("fireball")).evidence().stream().map(value -> value.id()).toList());
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
            assertEquals(DamageOutcome.FULL_DAMAGE, outcome.damageOutcome(), slug);
            var successfulOutcome = assertInstanceOf(AppliesOnSaveOutcome.class,
                    document.interpretations().stream().filter(value -> value.payload() instanceof DamageMechanic)
                            .findFirst().orElseThrow().context().bindings().get(1));
            assertEquals(SaveOutcome.SUCCESS, successfulOutcome.outcome(), slug);
            assertEquals(DamageOutcome.HALF_DAMAGE, successfulOutcome.damageOutcome(), slug);
            assertEquals(save.id(), successfulOutcome.savingThrow().id(), slug);
            assertEquals(MechanicsCompleteness.PARTIAL, document.interpretations().stream()
                    .filter(value -> value.payload() instanceof DamageMechanic)
                    .findFirst().orElseThrow().assessment().completeness(), slug);
            assertFalse(document.unsupported().stream().anyMatch(value -> value.id().contains("successful-save-half")), slug);
        });
    }

    @Test
    void explicitNoDamageOnSuccessfulSaveIsTypedAndBoundToTheSameSave() throws IOException {
        var input = input("fireball").changed(mapper, root -> root.withArray("entries").set(0,
                mapper.getNodeFactory().textNode(root.at("/entries/0").textValue()
                        .replace("half as much damage on a successful one", "no damage on a successful one"))));
        var document = extract(input);
        var damage = document.interpretations().stream().filter(value -> value.payload() instanceof DamageMechanic)
                .findFirst().orElseThrow();
        assertEquals(2, damage.context().bindings().size());
        var success = (AppliesOnSaveOutcome) damage.context().bindings().get(1);
        assertEquals(SaveOutcome.SUCCESS, success.outcome());
        assertEquals(DamageOutcome.NO_DAMAGE, success.damageOutcome());
        assertEquals(claim(document, "fireball-save-v1").revision().reference(
                claim(document, "fireball-save-v1").id()), success.savingThrow());
    }

    @Test
    void ambiguousOrUnsupportedSuccessWordingDoesNotCreateATypedDamageOutcome() throws IOException {
        var input = input("fireball").changed(mapper, root -> root.withArray("entries").set(0,
                mapper.getNodeFactory().textNode(root.at("/entries/0").textValue()
                        .replace("half as much damage on a successful one", "the target might avoid the damage"))));
        var document = extract(input);
        assertFalse(document.interpretations().stream().anyMatch(value -> value.payload() instanceof DamageMechanic));
        assertTrue(document.unsupported().stream().anyMatch(value -> value.reason()
                == UnsupportedReason.NO_SUPPORTED_PATTERN));
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
    void fireballKeepsAreaUnsupportedWhileTypingItsHalfDamageOutcomeAndSlotScaling() throws IOException {
        var document = extract(input("fireball"));
        assertEquals(java.util.List.of("area-targets"),
                document.unsupported().stream().map(value -> value.id()).toList());
        assertTrue(document.unsupported().stream().allMatch(value -> value.reason() == UnsupportedReason.FAMILY_NOT_IMPLEMENTED));
        assertEquals(CoverageStatus.PARTIAL, document.coverage().status());
        assertEquals(1, document.coverage().unsupportedCount());
        assertFalse(document.coverage().unassessedSourcePaths().isEmpty());
        assertEquals(1, document.interpretations().stream().filter(value -> value.payload() instanceof DamageMechanic).count());
        var scaling = document.interpretations().stream().map(MechanicRecord::payload)
                .filter(SpellSlotScalingMechanic.class::isInstance).map(SpellSlotScalingMechanic.class::cast)
                .findFirst().orElseThrow();
        assertEquals(ScalingEffect.DAMAGE, scaling.effect());
        assertEquals(new DiceAmount(1, 6, 0), scaling.increment());
        assertEquals(3, scaling.baseSlotLevel());
        assertEquals(4, scaling.minimumCastingSlotLevel());
        assertEquals(1, scaling.slotLevelInterval());
    }

    @Test
    void allSeventeenScalingClaimsPreserveSourceIncrementApplicabilityIntervalAndEvidence() throws IOException {
        Map<String, String> increments = Map.ofEntries(
                Map.entry("fireball", "1d6"),
                Map.entry("healing-word", "1d4"),
                Map.entry("burning-hands", "1d6"),
                Map.entry("cone-of-cold", "1d8"),
                Map.entry("shatter", "1d8"),
                Map.entry("lightning-bolt", "1d6"),
                Map.entry("circle-of-death", "2d6"),
                Map.entry("cure-wounds", "1d8"),
                Map.entry("mass-cure-wounds", "1d8"),
                Map.entry("mass-healing-word", "1d4"),
                Map.entry("prayer-of-healing", "1d8"),
                Map.entry("blight", "1d8"),
                Map.entry("cloudkill", "1d8"),
                Map.entry("hellish-rebuke", "1d10"),
                Map.entry("insect-plague", "1d10"),
                Map.entry("moonbeam", "1d10"),
                Map.entry("flaming-sphere", "1d6"));
        var healingOwners = java.util.Set.of("healing-word", "cure-wounds", "mass-cure-wounds",
                "mass-healing-word", "prayer-of-healing");
        assertEquals(17, increments.size());
        for (var entry : increments.entrySet()) {
            var input = input(entry.getKey());
            var document = extract(input);
            var claim = document.interpretations().stream()
                    .filter(value -> value.payload() instanceof SpellSlotScalingMechanic).findFirst().orElseThrow();
            var scaling = (SpellSlotScalingMechanic) claim.payload();
            assertEquals(healingOwners.contains(entry.getKey()) ? ScalingEffect.HEALING : ScalingEffect.DAMAGE,
                    scaling.effect(), entry.getKey());
            Matcher formula = Pattern.compile("([1-9][0-9]*)d([1-9][0-9]*)").matcher(entry.getValue());
            assertTrue(formula.matches(), entry.getKey());
            assertEquals(new DiceAmount(Integer.parseInt(formula.group(1)), Integer.parseInt(formula.group(2)), 0),
                    scaling.increment(), entry.getKey());
            assertEquals(input.spell().level(), scaling.baseSlotLevel(), entry.getKey());
            assertEquals(input.spell().level() + 1, scaling.minimumCastingSlotLevel(), entry.getKey());
            assertEquals(1, scaling.slotLevelInterval(), entry.getKey());
            var association = claim.evidenceAssociations().getFirst();
            var evidence = document.evidence().stream().filter(value -> value.id().equals(association.evidenceId()))
                    .map(TextMechanicsEvidence.class::cast).findFirst().orElseThrow();
            String sourceText = mapper.readTree(input.bytes())
                    .at(evidence.syntax().location().jsonPointer()).textValue();
            String excerpt = evidence.syntax().span().extract(sourceText);
            assertTrue(excerpt.contains("cast this spell using a spell slot"), entry.getKey());
            assertTrue(excerpt.contains("for each slot level above"), entry.getKey());
            assertTrue(excerpt.contains(entry.getValue()), entry.getKey());
            assertEquals(MechanicsCompleteness.PARTIAL, claim.assessment().completeness());
        }
    }

    @Test
    void healingWordPreservesTheOrderedDiceAndSymbolicSum() throws IOException {
        var document = extract(input("healing-word"));
        assertEquals(2, document.interpretations().size());
        var healing = assertInstanceOf(HealingMechanic.class, document.interpretations().getFirst().payload());
        var sum = assertInstanceOf(SumAmount.class, healing.amount());
        assertEquals(new DiceAmount(1, 4, 0), sum.terms().get(0));
        assertEquals(new SymbolicAmount(AmountSymbol.CASTER_SPELLCASTING_ABILITY_MODIFIER), sum.terms().get(1));
        assertEquals(HealingMode.RESTORE_HIT_POINTS, healing.mode());
        assertEquals(1, document.unsupported().size());
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
                "mass-healing-word", "prayer-of-healing", "blight", "chain-lightning", "cloudkill",
                "fire-storm", "hellish-rebuke", "incendiary-cloud", "insect-plague", "moonbeam",
                "flaming-sphere", "wind-wall")) {
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
        assertFalse(document.interpretations().stream().anyMatch(value -> value.payload() instanceof DamageMechanic));
        assertEquals(CoverageStatus.PARTIAL, document.coverage().status());
        assertTrue(document.unsupported().stream().anyMatch(value -> value.reason()
                == UnsupportedReason.NO_SUPPORTED_PATTERN));
    }

    @Test
    void missingSymbolicModifierNeverBecomesDiceOnlyHealing() throws IOException {
        var input = input("healing-word").changed(mapper, root -> root.withArray("entries").set(0,
                mapper.getNodeFactory().textNode(root.at("/entries/0").textValue()
                        .replace(" + your spellcasting ability modifier", ""))));
        var document = extract(input);
        assertFalse(document.interpretations().stream().anyMatch(value -> value.payload() instanceof HealingMechanic));
        assertEquals(CoverageStatus.PARTIAL, document.coverage().status());
        assertEquals(2, document.unsupported().size());
    }

    @Test
    void changedSaveOutcomeCannotBecomeUnconditionalDamage() throws IOException {
        var input = input("fireball").changed(mapper, root -> root.withArray("entries").set(0,
                mapper.getNodeFactory().textNode(root.at("/entries/0").textValue().replace("on a failed save", "on any save"))));
        var document = extract(input);
        assertFalse(document.interpretations().stream()
                .anyMatch(value -> value.payload() instanceof DamageMechanic));
        assertEquals(3, document.unsupported().size());
    }

    @Test
    void unsupportedFormulaArgumentsAndMalformedTagsAbstain() throws IOException {
        for (String replacement : java.util.List.of("{@damage 8d6|extra}", "{@damage 8d6", "{@dice 8d6}", "{@damage PBd6}")) {
            var input = input("fireball").changed(mapper, root -> root.withArray("entries").set(0,
                    mapper.getNodeFactory().textNode(root.at("/entries/0").textValue().replace("{@damage 8d6}", replacement))));
            assertFalse(extract(input).interpretations().stream()
                    .anyMatch(value -> value.payload() instanceof DamageMechanic), replacement);
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
        assertFalse(document.interpretations().stream().anyMatch(value -> value.payload() instanceof DamageMechanic));
        assertFalse(document.evidence().isEmpty());
        assertEquals(document.evidence().size(), document.evidence().stream().map(value -> value.id()).distinct().count());
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
    void registeredPatternsProcessConditionOwnersAndPreserveAllFormulaObservations() {
        assertEquals(969, inspection.size());
        assertEquals(87, inspection.documents().stream().filter(value -> value.processing().state() == ProcessingState.PROCESSED).count());
        assertEquals(882, inspection.documents().stream().filter(value -> value.processing().state() == ProcessingState.NOT_ATTEMPTED).count());
        assertEquals(156, inspection.documents().stream().mapToInt(value -> value.interpretations().size()).sum());
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
            assertEquals(3, document.interpretations().size(), slug);
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
            assertEquals(2, document.interpretations().size(), slug);
            var claim = document.interpretations().stream()
                    .filter(value -> value.payload() instanceof HealingMechanic).findFirst().orElseThrow();
            assertEquals("PENDING_REVIEW", claim.review().recordedStatus().name(), slug);
            assertEquals("NEEDS_MECHANICS_REVIEW", claim.review().effectiveStatus().name(), slug);
            assertEquals("NEEDS_REVIEW", claim.licensing().status().name(), slug);
            assertInstanceOf(com.thedndpub.data.mechanics.model.HealingMechanic.class, claim.payload());
            assertTrue(claim.evidenceAssociations().stream()
                    .anyMatch(value -> value.role()
                            == com.thedndpub.data.mechanics.model.EvidenceRole.EFFECT_ROLE), slug);
            assertTrue(document.evidence().stream().allMatch(value -> evidenceId(value).startsWith("spell-")), slug);
        }
    }

    @Test
    void expandedDamageOwnersExposeTypedSaveDamageAndEvidenceBoundOutcomes() {
        var newOwners = java.util.List.of("blight", "chain-lightning", "cloudkill", "fire-storm",
                "hellish-rebuke", "incendiary-cloud", "insect-plague", "moonbeam",
                "flaming-sphere", "wind-wall");
        for (String slug : newOwners) {
            var document = inspection.findById(spells.findById("spell:phb:" + slug).id());
            assertEquals(ProcessingState.PROCESSED, document.processing().state(), slug);
            var save = document.interpretations().stream()
                    .filter(value -> value.payload() instanceof SavingThrowRequirementMechanic)
                    .findFirst().orElseThrow(() -> new AssertionError(slug + " missing save"));
            var damage = document.interpretations().stream()
                    .filter(value -> value.payload() instanceof DamageMechanic)
                    .findFirst().orElseThrow(() -> new AssertionError(slug + " missing damage: "
                            + document.unsupported().stream().map(value -> value.id() + "=" + value.reason()).toList()));
            var outcomes = damage.context().bindings().stream().filter(AppliesOnSaveOutcome.class::isInstance)
                    .map(AppliesOnSaveOutcome.class::cast).toList();
            assertTrue(outcomes.stream().anyMatch(value -> value.outcome() == SaveOutcome.FAILURE
                    && value.damageOutcome() == DamageOutcome.FULL_DAMAGE
                    && value.savingThrow().id().equals(save.id())), slug);
            assertTrue(outcomes.stream().anyMatch(value -> value.outcome() == SaveOutcome.SUCCESS
                    && value.damageOutcome() == DamageOutcome.HALF_DAMAGE
                    && value.savingThrow().id().equals(save.id())), slug);
            assertFalse(damage.evidenceAssociations().isEmpty(), slug);
            assertEquals("PENDING_REVIEW", damage.review().recordedStatus().name(), slug);
            assertEquals("NEEDS_REVIEW", damage.licensing().status().name(), slug);
        }
    }

    @Test
    void newAdapterRejectsNonPilotOwners() throws IOException {
        var input = ShieldExtractionTestInput.actual(spells.findById("spell:phb:magic-missile"),
                owners.get("spell:phb:magic-missile"));
        assertThrows(IllegalArgumentException.class, () -> extract(input));
    }
}
