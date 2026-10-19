package com.thedndpub.data.mechanics.spell;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.mechanics.model.Ability;
import com.thedndpub.data.mechanics.model.AmountSymbol;
import com.thedndpub.data.mechanics.model.AppliesOnSaveOutcome;
import com.thedndpub.data.mechanics.model.AppliesWhen;
import com.thedndpub.data.mechanics.model.ApplicabilityCondition;
import com.thedndpub.data.mechanics.model.ArmorClassModifierMechanic;
import com.thedndpub.data.mechanics.model.CoverageStatus;
import com.thedndpub.data.mechanics.model.ConditionChangeMechanic;
import com.thedndpub.data.mechanics.model.ConditionChoiceMechanic;
import com.thedndpub.data.mechanics.model.ConditionEndsOnSaveSuccess;
import com.thedndpub.data.mechanics.model.ConditionEndsWithSpell;
import com.thedndpub.data.mechanics.model.ConditionName;
import com.thedndpub.data.mechanics.model.ConditionOnSaveOutcome;
import com.thedndpub.data.mechanics.model.ConditionOperation;
import com.thedndpub.data.mechanics.model.CreatureTypeRestriction;
import com.thedndpub.data.mechanics.model.DeclaredSpellDurationReference;
import com.thedndpub.data.mechanics.model.DamageMechanic;
import com.thedndpub.data.mechanics.model.DamageOutcome;
import com.thedndpub.data.mechanics.model.DamageApplicability;
import com.thedndpub.data.mechanics.model.DamageProtectionOperation;
import com.thedndpub.data.mechanics.model.DamageResistanceMechanic;
import com.thedndpub.data.mechanics.model.DamageTypeChoice;
import com.thedndpub.data.mechanics.model.DiceAmount;
import com.thedndpub.data.mechanics.model.FixedAbility;
import com.thedndpub.data.mechanics.model.FixedDamageType;
import com.thedndpub.data.mechanics.model.IntegerAmount;
import com.thedndpub.data.mechanics.model.IntegerModifier;
import com.thedndpub.data.mechanics.model.HealingMechanic;
import com.thedndpub.data.mechanics.model.HealingMode;
import com.thedndpub.data.mechanics.model.MechanicRecord;
import com.thedndpub.data.mechanics.model.MechanicsCompleteness;
import com.thedndpub.data.mechanics.model.MechanicsDocument;
import com.thedndpub.data.mechanics.model.MechanicsHashes;
import com.thedndpub.data.mechanics.model.MechanicContextBinding;
import com.thedndpub.data.mechanics.model.ModifierOnSaveOutcome;
import com.thedndpub.data.mechanics.model.ModifierOperation;
import com.thedndpub.data.mechanics.model.ModifiedStatistic;
import com.thedndpub.data.mechanics.model.MechanicsLicensingStatus;
import com.thedndpub.data.mechanics.model.AppliesForDuration;
import com.thedndpub.data.mechanics.model.ProcessingState;
import com.thedndpub.data.mechanics.model.SaveOutcome;
import com.thedndpub.data.mechanics.model.RepeatSaveTiming;
import com.thedndpub.data.mechanics.model.RequiresConcentration;
import com.thedndpub.data.mechanics.model.ScalingEffect;
import com.thedndpub.data.mechanics.model.SavingThrowRequirementMechanic;
import com.thedndpub.data.mechanics.model.SpellSlotScalingMechanic;
import com.thedndpub.data.mechanics.model.StatisticModifierMechanic;
import com.thedndpub.data.mechanics.model.StructuredFieldMechanicsEvidence;
import com.thedndpub.data.mechanics.model.SumAmount;
import com.thedndpub.data.mechanics.model.SymbolicAmount;
import com.thedndpub.data.mechanics.model.TextMechanicsEvidence;
import com.thedndpub.data.mechanics.model.SpellTargetSubject;
import com.thedndpub.data.mechanics.model.TargetScope;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
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
                .filter(value -> value.derivation().patternId().equals(pattern)).findFirst()
                .orElseThrow(() -> new AssertionError("Missing pattern " + pattern + ": "
                        + document.unsupported().stream().map(value -> value.id() + "=" + value.reason()).toList()));
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
    void generalizedConditionRemovalExtractsExplicitTargetsAndDoesNotApplyMentionedConditions()
            throws IOException {
        var lesser = extract(input("lesser-restoration"));
        var lesserRemovals = lesser.interpretations().stream()
                .filter(record -> record.payload() instanceof ConditionChangeMechanic).toList();
        assertEquals(4, lesserRemovals.size());
        assertTrue(lesserRemovals.stream().allMatch(record ->
                ((ConditionChangeMechanic) record.payload()).operation() == ConditionOperation.REMOVE));
        assertTrue(lesserRemovals.stream().allMatch(record ->
                ((ConditionChangeMechanic) record.payload()).qualifiers().stream()
                        .anyMatch(value -> value.contains("end either one disease"))));
        assertTrue(lesserRemovals.stream().allMatch(record ->
                record.context().subject() instanceof SpellTargetSubject subject
                        && subject.scope() == TargetScope.SINGLE_TARGET_CREATURE));
        assertTrue(lesserRemovals.stream().allMatch(record -> record.evidenceAssociations().stream()
                .anyMatch(value -> value.claimPath().equals("/payload/qualifiers"))));
        assertTrue(lesserRemovals.stream().allMatch(record -> record.derivation().sources()
                .equals(lesser.sources())));
        assertEquals(4, lesserRemovals.stream().map(record -> record.id().value()).distinct().count());
        var lesserChoiceRecord = lesser.interpretations().stream()
                .filter(record -> record.payload() instanceof ConditionChoiceMechanic).findFirst().orElseThrow();
        var lesserChoice = (ConditionChoiceMechanic) lesserChoiceRecord.payload();
        assertEquals(ConditionOperation.REMOVE, lesserChoice.operation());
        assertEquals(Set.of(ConditionName.BLINDED, ConditionName.DEAFENED,
                        ConditionName.PARALYZED, ConditionName.POISONED), Set.copyOf(lesserChoice.alternatives()));
        assertEquals(1, lesserChoice.choicesToSelect());
        assertTrue(lesserRemovals.stream().allMatch(record -> {
            ConditionChangeMechanic payload = (ConditionChangeMechanic) record.payload();
            return lesserChoiceRecord.revision().reference(lesserChoiceRecord.id()).equals(payload.choiceGroup());
        }), "The four removal claims must share an exclusive selection group");
        assertTrue(lesser.unsupported().stream().anyMatch(item ->
                item.id().equals("condition-removal-unmodeled-disease-option")),
                "The disease alternative must remain visible as unmodeled");

        var greater = extract(input("greater-restoration"));
        assertEquals(Set.of(ConditionName.CHARMED, ConditionName.PETRIFIED), greater.interpretations().stream()
                .filter(record -> record.payload() instanceof ConditionChangeMechanic)
                .map(record -> (ConditionChangeMechanic) record.payload())
                .filter(payload -> payload.operation() == ConditionOperation.REMOVE)
                .map(ConditionChangeMechanic::condition).collect(Collectors.toSet()));
        var greaterChoiceRecord = greater.interpretations().stream()
                .filter(record -> record.payload() instanceof ConditionChoiceMechanic).findFirst().orElseThrow();
        var greaterChoice = (ConditionChoiceMechanic) greaterChoiceRecord.payload();
        assertEquals(ConditionOperation.REMOVE, greaterChoice.operation());
        assertEquals(Set.of(ConditionName.CHARMED, ConditionName.PETRIFIED),
                Set.copyOf(greaterChoice.alternatives()));
        assertEquals(1, greaterChoice.choicesToSelect());
        assertTrue(greater.interpretations().stream()
                .filter(record -> record.payload() instanceof ConditionChangeMechanic)
                .map(record -> (ConditionChangeMechanic) record.payload())
                .filter(payload -> payload.operation() == ConditionOperation.REMOVE)
                .allMatch(payload -> greaterChoiceRecord.revision().reference(greaterChoiceRecord.id())
                        .equals(payload.choiceGroup())));
        assertTrue(greater.unsupported().stream().anyMatch(item ->
                item.id().equals("condition-removal-unmodeled-alternatives")));
        assertTrue(greater.unsupported().stream().anyMatch(item -> item.id().contains("exhaustion")));

        var powerWord = extract(input("power-word-heal"));
        assertEquals(Set.of(ConditionName.CHARMED, ConditionName.FRIGHTENED,
                        ConditionName.PARALYZED, ConditionName.STUNNED),
                powerWord.interpretations().stream()
                        .filter(record -> record.payload() instanceof ConditionChangeMechanic)
                        .map(record -> (ConditionChangeMechanic) record.payload())
                        .filter(payload -> payload.operation() == ConditionOperation.REMOVE)
                        .map(ConditionChangeMechanic::condition).collect(Collectors.toSet()));
        assertTrue(powerWord.interpretations().stream()
                .noneMatch(record -> record.payload() instanceof ConditionChoiceMechanic),
                "Power Word Heal ends its listed conditions independently");
        assertTrue(powerWord.interpretations().stream()
                .filter(record -> record.payload() instanceof ConditionChangeMechanic)
                .map(record -> (ConditionChangeMechanic) record.payload())
                .filter(payload -> payload.operation() == ConditionOperation.REMOVE)
                .allMatch(payload -> payload.choiceGroup() == null));
        assertTrue(powerWord.unsupported().stream().anyMatch(item -> item.id().contains("prone")),
                "Standing up is not generalized into a condition removal without an explicit ending clause");
    }

    @Test
    void generalizedConditionSaveDependencyReusesOneTypedSaveAndAbstainsForMultipleSaveAbilities()
            throws IOException {
        var web = extract(input("web"));
        var webSaves = web.interpretations().stream()
                .filter(record -> record.payload() instanceof SavingThrowRequirementMechanic).toList();
        assertEquals(1, webSaves.size());
        var restrained = condition(web, ConditionName.RESTRAINED);
        assertConditionSave(restrained, webSaves.getFirst(), SaveOutcome.FAILURE);
        assertTrue(restrained.context().subject() instanceof SpellTargetSubject subject
                && subject.scope() == TargetScope.AREA_AFFECTED_CREATURES);
        assertEquals(1, web.interpretations().stream()
                .filter(record -> record.payload() instanceof SavingThrowRequirementMechanic).count(),
                "The same save clause must not create duplicate typed saves");

        var earthquake = extract(input("earthquake"));
        assertTrue(earthquake.interpretations().stream()
                .noneMatch(record -> record.payload() instanceof SavingThrowRequirementMechanic));
        assertTrue(earthquake.unsupported().stream().anyMatch(record ->
                record.id().equals("condition-save-dependency")
                        && record.reason() == UnsupportedReason.AMBIGUOUS_INTERPRETATION));

        var blindingSmite = extract(input("blinding-smite"));
        var smiteSave = blindingSmite.interpretations().stream()
                .filter(record -> record.payload() instanceof SavingThrowRequirementMechanic).toList();
        assertEquals(1, smiteSave.size());
        assertConditionSave(condition(blindingSmite, ConditionName.BLINDED), smiteSave.getFirst(),
                SaveOutcome.FAILURE);
        assertTrue(blindingSmite.interpretations().stream()
                .noneMatch(record -> record.payload() instanceof DamageMechanic),
                "The condition save must not be reused as a damage dependency for this on-hit damage effect");
    }

    @Test
    void generalizedSaveAndDamagePatternsCoverRepresentativeStructures() throws IOException {
        var acidSplash = extract(input("acid-splash"));
        assertEquals(2, acidSplash.interpretations().size());
        var acidSave = acidSplash.interpretations().stream()
                .filter(record -> record.payload() instanceof SavingThrowRequirementMechanic).findFirst().orElseThrow();
        assertEquals(new FixedAbility(Ability.DEXTERITY),
                ((SavingThrowRequirementMechanic) acidSave.payload()).ability());
        var acidDamage = acidSplash.interpretations().stream()
                .filter(record -> record.payload() instanceof DamageMechanic).findFirst().orElseThrow();
        assertEquals(new DiceAmount(1, 6, 0), ((DamageMechanic) acidDamage.payload()).amount());
        var acidBinding = assertInstanceOf(AppliesOnSaveOutcome.class, acidDamage.context().bindings().getFirst());
        assertEquals(SaveOutcome.FAILURE, acidBinding.outcome());
        assertEquals(DamageOutcome.FULL_DAMAGE, acidBinding.damageOutcome());
        assertEquals(acidSave.revision().reference(acidSave.id()), acidBinding.savingThrow());
        assertTrue(acidDamage.evidenceAssociations().stream().allMatch(association ->
                acidSplash.evidence().stream().anyMatch(evidence -> evidence.id().equals(association.evidenceId()))));
        assertTrue(acidSplash.interpretations().stream().allMatch(record ->
                record.derivation().sources().equals(acidSplash.sources())));
        assertTrue(acidSplash.interpretations().stream().allMatch(record ->
                record.review().recordedStatus().name().equals("PENDING_REVIEW")
                        && record.licensing().status().name().equals("NEEDS_REVIEW")));
        assertEquals(acidSplash.interpretations().stream().map(MechanicRecord::id).toList(),
                extract(input("acid-splash")).interpretations().stream().map(MechanicRecord::id).toList());

        var armsOfHadar = extract(input("arms-of-hadar"));
        var armsDamage = armsOfHadar.interpretations().stream()
                .filter(record -> record.payload() instanceof DamageMechanic).findFirst().orElseThrow();
        assertEquals(2, armsDamage.context().bindings().size());
        assertEquals(DamageOutcome.HALF_DAMAGE,
                assertInstanceOf(AppliesOnSaveOutcome.class, armsDamage.context().bindings().get(1)).damageOutcome());
        assertEquals(1, armsOfHadar.interpretations().stream()
                .filter(record -> record.payload() instanceof SpellSlotScalingMechanic).count());

        var iceStorm = extract(input("ice-storm"));
        var iceDamage = iceStorm.interpretations().stream()
                .filter(record -> record.payload() instanceof DamageMechanic).toList();
        assertEquals(2, iceDamage.size());
        assertEquals(List.of("bludgeoning", "cold"), iceDamage.stream()
                .map(record -> ((FixedDamageType) ((DamageMechanic) record.payload()).damageType()).authoredCode())
                .sorted().toList());
        assertTrue(iceDamage.stream().allMatch(record -> record.context().bindings().stream()
                .anyMatch(binding -> binding instanceof AppliesOnSaveOutcome outcome
                        && outcome.damageOutcome() == DamageOutcome.HALF_DAMAGE)));

        var calmEmotions = extract(input("calm-emotions"));
        assertEquals(1, calmEmotions.interpretations().size());
        assertInstanceOf(SavingThrowRequirementMechanic.class, calmEmotions.interpretations().getFirst().payload());

        var disintegrate = extract(input("disintegrate"));
        var disintegrateDamage = disintegrate.interpretations().stream()
                .filter(record -> record.payload() instanceof DamageMechanic).findFirst().orElseThrow();
        assertEquals(new SumAmount(List.of(new DiceAmount(10, 6, 0), new IntegerAmount(40))),
                ((DamageMechanic) disintegrateDamage.payload()).amount());
    }

    @Test
    void generalizedSavePatternAbstainsWhenSourceWordingDoesNotMatch() throws IOException {
        var changed = input("acid-splash").changed(mapper, root -> root.withArray("entries").set(0,
                mapper.getNodeFactory().textNode("A target faces a Dexterity saving throw when affected.")));
        var document = extract(changed);
        assertTrue(document.interpretations().isEmpty());
        assertTrue(document.unsupported().stream().anyMatch(value ->
                value.candidateFamily() == com.thedndpub.data.mechanics.model.MechanicFamily.SAVING_THROW_REQUIREMENT));
    }

    @Test
    void generalizedDamagePatternAbstainsOnUnsupportedAmountAndOfflineMatchingStaysBounded() throws IOException {
        var changed = input("acid-splash").changed(mapper, root -> root.withArray("entries").set(0,
                mapper.getNodeFactory().textNode("The target must succeed on a Dexterity saving throw or take "
                        + "{@damage 1d6+1d4} acid damage.")));
        var document = extract(changed);
        assertEquals(1, document.interpretations().size());
        assertInstanceOf(SavingThrowRequirementMechanic.class, document.interpretations().getFirst().payload());
        assertTrue(document.unsupported().stream().anyMatch(value ->
                value.candidateFamily() == com.thedndpub.data.mechanics.model.MechanicFamily.DAMAGE));

        var unrelated = spells.findById("spell:phb:poison-spray");
        var extractor = new PhbSpellMechanicsExtractor();
        assertFalse(extractor.supports(unrelated));
        assertTrue(extractor.supportsForInventory(unrelated));
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
    void conditionDependenciesTargetsDurationsAndEndingsAreSourceBacked() throws IOException {
        var hold = extract(input("hold-person"));
        var holdCondition = condition(hold, ConditionName.PARALYZED);
        var holdSave = claim(hold, "condition-saving-throw-v1");
        assertEquals(new FixedAbility(Ability.WISDOM),
                assertInstanceOf(SavingThrowRequirementMechanic.class, holdSave.payload()).ability());
        assertConditionSave(holdCondition, holdSave, SaveOutcome.FAILURE);
        assertTarget(holdCondition, TargetScope.MULTIPLE_TARGET_CREATURES, CreatureTypeRestriction.HUMANOID);
        assertDurationAndConcentration(holdCondition, true);
        assertEquals(RepeatSaveTiming.END_OF_EACH_TARGET_TURN,
                binding(holdCondition, ConditionEndsOnSaveSuccess.class).timing());

        var blindness = extract(input("blindness-deafness"));
        var choice = blindness.interpretations().stream()
                .filter(value -> value.payload() instanceof ConditionChoiceMechanic).findFirst().orElseThrow();
        var blindnessSave = claim(blindness, "condition-saving-throw-v1");
        assertEquals(new FixedAbility(Ability.CONSTITUTION),
                assertInstanceOf(SavingThrowRequirementMechanic.class, blindnessSave.payload()).ability());
        assertConditionSave(choice, blindnessSave, SaveOutcome.FAILURE);
        assertDurationAndConcentration(choice, false);
        assertEquals(RepeatSaveTiming.END_OF_EACH_TARGET_TURN,
                binding(choice, ConditionEndsOnSaveSuccess.class).timing());
        var blindClaims = blindness.interpretations().stream()
                .filter(value -> value.payload() instanceof ConditionChangeMechanic).toList();
        assertEquals(2, blindClaims.size());
        assertTrue(blindClaims.stream().allMatch(value -> value.context().bindings().stream()
                .noneMatch(ConditionOnSaveOutcome.class::isInstance)));
        assertTrue(blindClaims.stream().allMatch(value -> ((ConditionChangeMechanic) value.payload()).choiceGroup()
                .equals(choice.revision().reference(choice.id()))));

        var fear = extract(input("fear"));
        var fearCondition = condition(fear, ConditionName.FRIGHTENED);
        var fearSave = claim(fear, "condition-saving-throw-v1");
        assertConditionSave(fearCondition, fearSave, SaveOutcome.FAILURE);
        assertTarget(fearCondition, TargetScope.AREA_AFFECTED_CREATURES, null);
        assertDurationAndConcentration(fearCondition, true);
        assertFalse(fearCondition.context().bindings().stream().anyMatch(ConditionEndsOnSaveSuccess.class::isInstance));
        assertTrue(((ConditionChangeMechanic) fearCondition.payload()).qualifiers().stream()
                .anyMatch(value -> value.contains("line of sight") && value.contains("Dash")));
        assertTrue(fear.unsupported().stream().anyMatch(value -> value.id().equals("condition-ending-rules")));

        var invisibility = extract(input("greater-invisibility"));
        var invisibleCondition = condition(invisibility, ConditionName.INVISIBLE);
        assertTrue(invisibility.interpretations().stream()
                .noneMatch(value -> value.payload() instanceof SavingThrowRequirementMechanic));
        assertTarget(invisibleCondition, TargetScope.CASTER_OR_TOUCHED_CREATURE, null);
        assertDurationAndConcentration(invisibleCondition, true);
        assertNotNull(binding(invisibleCondition, ConditionEndsWithSpell.class));

        var hypnotic = extract(input("hypnotic-pattern"));
        var charmed = condition(hypnotic, ConditionName.CHARMED);
        var incapacitated = condition(hypnotic, ConditionName.INCAPACITATED);
        var hypnoticSave = claim(hypnotic, "condition-saving-throw-v1");
        assertConditionSave(charmed, hypnoticSave, SaveOutcome.FAILURE);
        assertTrue(incapacitated.context().bindings().stream().noneMatch(ConditionOnSaveOutcome.class::isInstance));
        assertTarget(charmed, TargetScope.AREA_AFFECTED_CREATURES, null);
        assertTarget(incapacitated, TargetScope.AREA_AFFECTED_CREATURES, null);
        assertDurationAndConcentration(charmed, true);
        assertDurationAndConcentration(incapacitated, true);
        assertFalse(hypnotic.interpretations().stream().anyMatch(value -> value.payload() instanceof ConditionChoiceMechanic));
        assertTrue(hypnotic.unsupported().stream().anyMatch(value -> value.id().equals("condition-ending-rules")));

        for (var pair : List.of(Map.entry("hold-person", hold), Map.entry("blindness-deafness", blindness),
                Map.entry("fear", fear), Map.entry("greater-invisibility", invisibility),
                Map.entry("hypnotic-pattern", hypnotic))) {
            var repeated = extract(input(pair.getKey()));
            assertEquals(pair.getValue().interpretations().stream().map(value -> value.revision().contentSha256()).toList(),
                    repeated.interpretations().stream().map(value -> value.revision().contentSha256()).toList());
        }
    }

    @Test
    void ambiguousConditionSaveLanguageDoesNotCreateAnOutcomeDependency() throws IOException {
        var input = input("hold-person").changed(mapper, root -> root.withArray("entries").set(0,
                mapper.getNodeFactory().textNode(root.at("/entries/0").textValue()
                        .replace("must succeed on a Wisdom saving throw or be {@condition paralyzed}",
                                "makes a Wisdom saving throw and is {@condition paralyzed}"))));
        var document = extract(input);
        assertTrue(document.interpretations().stream().noneMatch(value ->
                value.derivation().patternId().equals("condition-saving-throw-v1")));
        assertTrue(document.interpretations().stream()
                .filter(value -> value.payload() instanceof ConditionChangeMechanic)
                .noneMatch(value -> value.context().bindings().stream().anyMatch(ConditionOnSaveOutcome.class::isInstance)));
        assertTrue(document.unsupported().stream().anyMatch(value ->
                value.reason() == UnsupportedReason.AMBIGUOUS_INTERPRETATION));
    }

    private MechanicRecord condition(MechanicsDocument document, ConditionName condition) {
        return document.interpretations().stream().filter(value -> value.payload() instanceof ConditionChangeMechanic change
                        && change.condition() == condition)
                .findFirst().orElseThrow();
    }

    private <T extends com.thedndpub.data.mechanics.model.MechanicContextBinding> T binding(
            MechanicRecord record, Class<T> type) {
        return record.context().bindings().stream().filter(type::isInstance).map(type::cast).findFirst().orElseThrow();
    }

    private void assertConditionSave(MechanicRecord effect, MechanicRecord save, SaveOutcome outcome) {
        var dependency = binding(effect, ConditionOnSaveOutcome.class);
        assertEquals(save.revision().reference(save.id()), dependency.savingThrow());
        assertEquals(outcome, dependency.outcome());
        assertTrue(effect.evidenceAssociations().stream().anyMatch(value ->
                value.claimPath().startsWith("/context/bindings/")
                        && dependency.evidenceIds().contains(value.evidenceId())));
        assertEquals(com.thedndpub.data.mechanics.review.MechanicsReviewStatus.PENDING_REVIEW,
                effect.review().recordedStatus());
        assertEquals(MechanicsLicensingStatus.NEEDS_REVIEW, effect.licensing().status());
        assertEquals(com.thedndpub.data.mechanics.review.MechanicsReviewStatus.PENDING_REVIEW,
                save.review().recordedStatus());
        assertEquals(MechanicsLicensingStatus.NEEDS_REVIEW, save.licensing().status());
    }

    private void assertTarget(MechanicRecord effect, TargetScope scope, CreatureTypeRestriction restriction) {
        var target = assertInstanceOf(SpellTargetSubject.class, effect.context().subject());
        assertEquals(scope, target.scope());
        assertEquals(restriction, target.creatureTypeRestriction());
        assertTrue(effect.evidenceAssociations().stream().anyMatch(value ->
                value.claimPath().equals("/context/subject")
                        && value.role() == com.thedndpub.data.mechanics.model.EvidenceRole.QUALIFIER));
    }

    private void assertDurationAndConcentration(MechanicRecord effect, boolean concentration) {
        var duration = binding(effect, AppliesForDuration.class);
        assertInstanceOf(DeclaredSpellDurationReference.class, duration.duration());
        assertTrue(effect.evidenceAssociations().stream().anyMatch(value ->
                duration.evidenceIds().contains(value.evidenceId())
                        && value.claimPath().startsWith("/context/bindings/")));
        assertEquals(concentration, effect.context().bindings().stream().anyMatch(RequiresConcentration.class::isInstance));
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
    void modifierAndProtectionPatternsPreserveOperationsDependenciesAndSourceBackedContext() throws IOException {
        var shieldFaith = extract(input("shield-of-faith"));
        var shieldAc = claim(shieldFaith, "shield-of-faith-ac-bonus-v1");
        assertEquals(new IntegerModifier(2, "+2"),
                assertInstanceOf(ArmorClassModifierMechanic.class, shieldAc.payload()).amount());
        assertTrue(shieldAc.context().bindings().stream().anyMatch(AppliesForDuration.class::isInstance));
        assertTrue(shieldAc.context().bindings().stream().anyMatch(RequiresConcentration.class::isInstance));
        assertEquals(TargetScope.SINGLE_TARGET_CREATURE,
                assertInstanceOf(SpellTargetSubject.class, shieldAc.context().subject()).scope());

        var mageArmor = extract(input("mage-armor"));
        var mageAc = claim(mageArmor, "mage-armor-base-ac-replacement-v1");
        var magePayload = assertInstanceOf(StatisticModifierMechanic.class, mageAc.payload());
        assertEquals(ModifierOperation.SET_BASE, magePayload.operation());
        assertEquals(List.of(ModifiedStatistic.BASE_ARMOR_CLASS), magePayload.statistics());
        assertEquals(new SumAmount(List.of(new IntegerAmount(13),
                        new SymbolicAmount(AmountSymbol.DEXTERITY_MODIFIER))), magePayload.amount());
        assertTrue(mageAc.assessment().completeness() == MechanicsCompleteness.PARTIAL);
        assertEquals(1, mageArmor.unsupported().size());
        assertTrue(mageAc.context().bindings().stream().anyMatch(binding -> binding instanceof AppliesWhen applies
                && applies.condition() == ApplicabilityCondition.TARGET_NOT_WEARING_ARMOR));

        var barkskin = extract(input("barkskin"));
        var barkAc = assertInstanceOf(StatisticModifierMechanic.class,
                claim(barkskin, "barkskin-ac-floor-v1").payload());
        assertEquals(ModifierOperation.MINIMUM, barkAc.operation());
        assertEquals(new IntegerAmount(16), barkAc.amount());

        var longstrider = extract(input("longstrider"));
        var speed = assertInstanceOf(StatisticModifierMechanic.class,
                claim(longstrider, "longstrider-speed-increase-v1").payload());
        assertEquals(List.of(ModifiedStatistic.MOVEMENT_SPEED), speed.statistics());
        assertEquals(ModifierOperation.ADD, speed.operation());
        assertEquals(new IntegerAmount(10), speed.amount());

        var bless = extract(input("bless"));
        var blessBonus = assertInstanceOf(StatisticModifierMechanic.class,
                claim(bless, "bless-roll-d4-bonus-v1").payload());
        assertEquals(ModifierOperation.ADD, blessBonus.operation());
        assertEquals(new DiceAmount(1, 4, 0), blessBonus.amount());
        assertEquals(List.of(ModifiedStatistic.ATTACK_ROLL, ModifiedStatistic.SAVING_THROW), blessBonus.statistics());
        assertEquals(3, assertInstanceOf(SpellTargetSubject.class,
                claim(bless, "bless-roll-d4-bonus-v1").context().subject()).maximumTargets());

        var bane = extract(input("bane"));
        var baneSave = claim(bane, "bane-saving-throw-requirement-v1");
        var banePenaltyClaim = claim(bane, "bane-roll-d4-penalty-v1");
        var banePenalty = assertInstanceOf(StatisticModifierMechanic.class, banePenaltyClaim.payload());
        assertEquals(ModifierOperation.SUBTRACT, banePenalty.operation());
        assertEquals(new DiceAmount(1, 4, 0), banePenalty.amount());
        var saveDependency = banePenaltyClaim.context().bindings().stream()
                .filter(ModifierOnSaveOutcome.class::isInstance).map(ModifierOnSaveOutcome.class::cast)
                .findFirst().orElseThrow();
        assertEquals(SaveOutcome.FAILURE, saveDependency.outcome());
        assertEquals(baneSave.revision().reference(baneSave.id()), saveDependency.savingThrow());
        assertEquals(MechanicsCompleteness.PARTIAL, baneSave.assessment().completeness());
        assertTrue(banePenaltyClaim.context().bindings().stream().anyMatch(binding -> binding instanceof AppliesWhen applies
                && applies.condition() == ApplicabilityCondition.TARGET_MAKES_ROLL_BEFORE_SPELL_ENDS));

        var protection = extract(input("protection-from-energy"));
        var energyClaim = claim(protection, "protection-from-energy-selected-resistance-v1");
        var energy = assertInstanceOf(DamageResistanceMechanic.class, energyClaim.payload());
        assertEquals(DamageProtectionOperation.RESISTANCE, energy.operation());
        assertEquals(new DamageTypeChoice(List.of("acid", "cold", "fire", "lightning", "thunder"), 1),
                energy.damageTypes());
        assertTrue(energyClaim.context().bindings().stream().anyMatch(RequiresConcentration.class::isInstance));

        var stoneskin = extract(input("stoneskin"));
        var stoneResistances = stoneskin.interpretations().stream()
                .filter(record -> record.payload() instanceof DamageResistanceMechanic).toList();
        assertEquals(3, stoneResistances.size());
        assertEquals(List.of("bludgeoning", "piercing", "slashing"), stoneResistances.stream()
                .map(record -> ((DamageResistanceMechanic) record.payload()).damageTypes())
                .map(type -> ((FixedDamageType) type).authoredCode()).toList());
        assertTrue(stoneResistances.stream().allMatch(record ->
                ((DamageResistanceMechanic) record.payload()).operation() == DamageProtectionOperation.RESISTANCE
                        && ((DamageResistanceMechanic) record.payload()).applicability()
                        == DamageApplicability.NONMAGICAL_DAMAGE_ONLY
                        && record.context().bindings().stream().anyMatch(RequiresConcentration.class::isInstance)));

        for (var document : List.of(shieldFaith, mageArmor, barkskin, longstrider, bless, bane, protection, stoneskin)) {
            assertEquals(document, extract(input(document.owner().id().substring("spell:phb:".length()))));
            assertTrue(document.interpretations().stream().allMatch(record ->
                    record.review().recordedStatus() == MechanicsReviewStatus.PENDING_REVIEW
                            && record.licensing().status() == MechanicsLicensingStatus.NEEDS_REVIEW));
            for (var record : document.interpretations()) {
                assertTrue(document.evidence().stream().map(value -> value.id())
                        .collect(Collectors.toSet()).containsAll(record.evidenceAssociations().stream()
                                .map(value -> value.evidenceId()).toList()));
            }
        }
    }

    @Test
    void ambiguousModifierWordingAbstainsInsteadOfGuessingAnOperation() throws IOException {
        var changed = input("barkskin").changed(mapper, root -> root.withArray("entries").set(0,
                mapper.getNodeFactory().textNode("The creature's AC changes to 16 in some circumstances.")));
        var document = extract(changed);
        assertFalse(document.interpretations().stream()
                .anyMatch(record -> record.payload() instanceof StatisticModifierMechanic));
        assertTrue(document.unsupported().stream().anyMatch(value ->
                value.candidateFamily() == com.thedndpub.data.mechanics.model.MechanicFamily.MODIFIER
                        && (value.reason() == UnsupportedReason.NO_SUPPORTED_PATTERN
                        || value.reason() == UnsupportedReason.AMBIGUOUS_INTERPRETATION)));
    }

    @Test
    void registeredPatternsProcessConditionOwnersAndPreserveAllFormulaObservations() {
        assertEquals(969, inspection.size());
        assertEquals(106, inspection.documents().stream().filter(value -> value.processing().state() == ProcessingState.PROCESSED).count());
        assertEquals(863, inspection.documents().stream().filter(value -> value.processing().state() == ProcessingState.NOT_ATTEMPTED).count());
        assertEquals(228, inspection.documents().stream().mapToInt(value -> value.interpretations().size()).sum());
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
