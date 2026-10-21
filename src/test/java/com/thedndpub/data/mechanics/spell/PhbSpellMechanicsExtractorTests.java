package com.thedndpub.data.mechanics.spell;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dte.common.entry.TextEntryDte;
import com.thedndpub.data.mechanics.model.Ability;
import com.thedndpub.data.mechanics.model.AttackActivationTiming;
import com.thedndpub.data.mechanics.model.AttackActorRole;
import com.thedndpub.data.mechanics.model.AttackKind;
import com.thedndpub.data.mechanics.model.AttackRollMechanic;
import com.thedndpub.data.mechanics.model.AttackRollModifier;
import com.thedndpub.data.mechanics.model.AttackRollModifierMechanic;
import com.thedndpub.data.mechanics.model.AttackRollOutcome;
import com.thedndpub.data.mechanics.model.AttackRollParticipant;
import com.thedndpub.data.mechanics.model.AppliesOnAttackRollOutcome;
import com.thedndpub.data.mechanics.model.AmountSymbol;
import com.thedndpub.data.mechanics.model.AppliesOnSaveOutcome;
import com.thedndpub.data.mechanics.model.AppliesWhen;
import com.thedndpub.data.mechanics.model.ApplicabilityCondition;
import com.thedndpub.data.mechanics.model.ArmorClassModifierMechanic;
import com.thedndpub.data.mechanics.model.CoverageStatus;
import com.thedndpub.data.mechanics.model.CharacterLevelDamageScaling;
import com.thedndpub.data.mechanics.model.CharacterLevelDamageTier;
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
import com.thedndpub.data.mechanics.model.MechanicFamily;
import com.thedndpub.data.mechanics.model.MechanicEvidenceAssociation;
import com.thedndpub.data.mechanics.model.MechanicRevisionReference;
import com.thedndpub.data.mechanics.model.MechanicsCompleteness;
import com.thedndpub.data.mechanics.model.MechanicsDocument;
import com.thedndpub.data.mechanics.model.MechanicsHashes;
import com.thedndpub.data.mechanics.model.MechanicContextBinding;
import com.thedndpub.data.mechanics.model.ModifierOnSaveOutcome;
import com.thedndpub.data.mechanics.model.ModifierOperation;
import com.thedndpub.data.mechanics.model.ModifiedStatistic;
import com.thedndpub.data.mechanics.model.MechanicsLicensingStatus;
import com.thedndpub.data.mechanics.model.AppliesForDuration;
import com.thedndpub.data.mechanics.model.HitPointRecoveryPreventionMechanic;
import com.thedndpub.data.mechanics.model.FutureAttackRollSelector;
import com.thedndpub.data.mechanics.model.ReferencedAttackRollSelector;
import com.thedndpub.data.mechanics.model.ReactionPreventionMechanic;
import com.thedndpub.data.mechanics.model.MechanicContext;
import com.thedndpub.data.mechanics.model.MechanicRevisionReference;
import com.thedndpub.data.mechanics.model.EvidenceRole;
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
import com.thedndpub.data.mechanics.model.TurnActor;
import com.thedndpub.data.mechanics.model.TurnBoundary;
import com.thedndpub.data.mechanics.model.UntilTurnBoundary;
import com.thedndpub.data.mechanics.model.SpellTargetSubject;
import com.thedndpub.data.mechanics.model.TargetScope;
import com.thedndpub.data.mechanics.model.TargetCategory;
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
    void inflictWoundsProducesOneSourceBoundMeleeSpellAttackAndLinkedHitDamage() throws Exception {
        var extractionInput = input("inflict-wounds");
        var sourceText = ((TextEntryDte) extractionInput.spell().entries().getFirst()).text();
        var document = extract(extractionInput);

        assertEquals(2, document.interpretations().size());
        var attack = document.interpretations().stream()
                .filter(record -> record.id().value().endsWith(":attack-roll"))
                .findFirst().orElseThrow();
        var attackPayload = assertInstanceOf(AttackRollMechanic.class, attack.payload());
        assertEquals("mechanic:spell:phb:inflict-wounds:attack-roll", attack.id().value());
        assertEquals(1, attack.revision().number());
        assertEquals(AttackKind.MELEE_SPELL, attackPayload.attackKind());
        assertEquals(AttackActorRole.CASTER, attackPayload.actorRole());
        assertEquals(1, attackPayload.attackCount());
        assertTrue(attackPayload.attacksIndependent());
        assertEquals(AttackActivationTiming.CAST_TIME, attackPayload.activationTiming());
        var attackTarget = assertInstanceOf(SpellTargetSubject.class, attack.context().subject());
        assertEquals(TargetScope.SINGLE_TARGET_CREATURE, attackTarget.scope());
        assertEquals("c9e54cce55e7ce0fc988feb1b604bf505faa5c042635542fa6b6ed36724e297a",
                document.sources().getFirst().resourceSha256());

        var damage = document.interpretations().stream()
                .filter(record -> record.id().value().endsWith(":damage"))
                .findFirst().orElseThrow();
        assertEquals("mechanic:spell:phb:inflict-wounds:damage", damage.id().value());
        assertEquals(1, damage.revision().number());
        var damagePayload = assertInstanceOf(DamageMechanic.class, damage.payload());
        assertEquals(new DiceAmount(3, 10, 0), damagePayload.amount());
        assertEquals(new FixedDamageType("necrotic"), damagePayload.damageType());
        var outcome = assertInstanceOf(AppliesOnAttackRollOutcome.class,
                damage.context().bindings().getFirst());
        assertEquals(attack.revision().reference(attack.id()), outcome.attackRoll());
        assertEquals(AttackRollOutcome.HIT, outcome.outcome());
        assertEquals(TargetScope.SINGLE_TARGET_CREATURE,
                assertInstanceOf(SpellTargetSubject.class, damage.context().subject()).scope());

        assertTrue(attack.evidenceAssociations().stream().anyMatch(value ->
                value.claimPath().equals("/payload")
                        && value.role() == com.thedndpub.data.mechanics.model.EvidenceRole.EFFECT_ROLE));
        assertTrue(damage.evidenceAssociations().stream().anyMatch(value ->
                value.claimPath().equals("/context/bindings/0")
                        && value.role() == com.thedndpub.data.mechanics.model.EvidenceRole.TRIGGER));
        var attackText = assertInstanceOf(TextMechanicsEvidence.class, document.evidence().stream()
                .filter(value -> attack.evidenceAssociations().stream()
                        .anyMatch(association -> association.evidenceId().equals(value.id())))
                .filter(TextMechanicsEvidence.class::isInstance).findFirst().orElseThrow());
        assertEquals("/spell/192/entries/0", attackText.syntax().location().jsonPointer());
        String attackPhrase = "Make a melee spell attack against a creature you can reach";
        int attackStart = sourceText.indexOf(attackPhrase);
        assertTrue(attackStart >= 0);
        assertEquals(attackStart, attackText.syntax().span().startInclusive());
        assertEquals(attackStart + attackPhrase.length(), attackText.syntax().span().endExclusive());
        var outcomeEvidence = assertInstanceOf(TextMechanicsEvidence.class, document.evidence().stream()
                .filter(value -> value.id().equals(outcome.evidenceIds().getFirst())).findFirst().orElseThrow());
        int hitStart = sourceText.indexOf("On a hit");
        assertEquals("/spell/192/entries/0", outcomeEvidence.syntax().location().jsonPointer());
        assertEquals(hitStart, outcomeEvidence.syntax().span().startInclusive());
        assertEquals(hitStart + "On a hit".length(), outcomeEvidence.syntax().span().endExclusive());
        var attackKindEvidence = assertInstanceOf(StructuredFieldMechanicsEvidence.class, document.evidence().stream()
                .filter(value -> attack.evidenceAssociations().stream()
                        .anyMatch(association -> association.evidenceId().equals(value.id())))
                .filter(StructuredFieldMechanicsEvidence.class::isInstance).findFirst().orElseThrow());
        assertEquals("/spell/192/spellAttack/0", attackKindEvidence.jsonPointer());
        var damageFormulaEvidence = document.evidence().stream()
                .filter(value -> damage.evidenceAssociations().stream()
                        .anyMatch(association -> association.evidenceId().equals(value.id())))
                .filter(TextMechanicsEvidence.class::isInstance)
                .map(TextMechanicsEvidence.class::cast)
                .filter(value -> sourceText.startsWith("{@damage 3d10}", value.syntax().span().startInclusive()))
                .findFirst().orElseThrow();
        assertEquals("/spell/192/entries/0", damageFormulaEvidence.syntax().location().jsonPointer());
        int damageFormulaStart = sourceText.indexOf("{@damage 3d10}");
        assertEquals(damageFormulaStart, damageFormulaEvidence.syntax().span().startInclusive());
        assertEquals(damageFormulaStart + "{@damage 3d10}".length(),
                damageFormulaEvidence.syntax().span().endExclusive());
        assertTrue(document.interpretations().stream().allMatch(record ->
                record.review().recordedStatus() == com.thedndpub.data.mechanics.review.MechanicsReviewStatus.PENDING_REVIEW
                        && record.licensing().status() == MechanicsLicensingStatus.NEEDS_REVIEW));
        assertEquals(com.thedndpub.data.mechanics.review.MechanicsReviewStatus.PENDING_REVIEW,
                attack.review().recordedStatus());
        assertEquals(MechanicsLicensingStatus.NEEDS_REVIEW, attack.licensing().status());
        assertEquals(0, document.reviewSummary().verified());

        byte[] serialized = com.thedndpub.data.mechanics.model.MechanicsJson.mapper().writeValueAsBytes(document);
        assertArrayEquals(serialized,
                com.thedndpub.data.mechanics.model.MechanicsJson.mapper().writeValueAsBytes(document));
        assertEquals(document, com.thedndpub.data.mechanics.model.MechanicsJson.mapper()
                .readValue(serialized, MechanicsDocument.class));
    }

    @Test
    void inflictWoundsRejectsAStaleAttackRollRevisionReference() throws Exception {
        var document = extract(input("inflict-wounds"));
        var attack = document.interpretations().stream()
                .filter(record -> record.payload() instanceof AttackRollMechanic).findFirst().orElseThrow();
        var damage = document.interpretations().stream()
                .filter(record -> record.payload() instanceof DamageMechanic).findFirst().orElseThrow();
        var outcome = assertInstanceOf(AppliesOnAttackRollOutcome.class, damage.context().bindings().getFirst());
        var staleReference = new MechanicRevisionReference(attack.id(), 1, "0".repeat(64));
        var staleBinding = new AppliesOnAttackRollOutcome(staleReference, AttackRollOutcome.HIT,
                outcome.evidenceIds());
        var staleDamage = MechanicRecord.propose(damage.id(), damage.payload(),
                new com.thedndpub.data.mechanics.model.MechanicContext(
                        damage.context().subject(), List.of(staleBinding)),
                damage.evidenceAssociations(), damage.derivation(), damage.assessment());
        var records = List.of(attack, staleDamage);

        assertThrows(IllegalArgumentException.class, () -> MechanicsDocument.create(
                document.owner(), 1, document.sources(), document.processing(), records, document.observations(),
                document.unsupported(), document.evidence(), document.coverage()));
    }

    @Test
    void rayOfFrostProducesRangedAttackDamageAndHitBoundSpeedReduction() throws Exception {
        var extractionInput = input("ray-of-frost");
        var body = ((TextEntryDte) extractionInput.spell().entries().getFirst()).text();
        var scaling = ((TextEntryDte) extractionInput.spell().entries().get(1)).text();
        var document = extract(extractionInput);

        assertEquals(3, document.interpretations().size());
        var attack = document.interpretations().stream()
                .filter(record -> record.payload() instanceof AttackRollMechanic).findFirst().orElseThrow();
        assertEquals("mechanic:spell:phb:ray-of-frost:attack-roll", attack.id().value());
        assertEquals(1, attack.revision().number());
        var attackPayload = assertInstanceOf(AttackRollMechanic.class, attack.payload());
        assertEquals(AttackKind.RANGED_SPELL, attackPayload.attackKind());
        assertEquals(AttackActorRole.CASTER, attackPayload.actorRole());
        assertEquals(1, attackPayload.attackCount());
        assertTrue(attackPayload.attacksIndependent());
        assertEquals(AttackActivationTiming.CAST_TIME, attackPayload.activationTiming());
        assertEquals(TargetScope.SINGLE_TARGET_CREATURE,
                assertInstanceOf(SpellTargetSubject.class, attack.context().subject()).scope());
        assertTextEvidence(document, attack, "/context/subject", EvidenceRole.SUBJECT,
                "/spell/273/entries/0", "a creature within range", body);

        var damage = document.interpretations().stream()
                .filter(record -> record.payload() instanceof DamageMechanic).findFirst().orElseThrow();
        assertEquals("mechanic:spell:phb:ray-of-frost:damage", damage.id().value());
        assertEquals(2, damage.revision().number());
        assertEquals(new MechanicRevisionReference(damage.id(), 1,
                        "126f7880b1895357a0a51913971f334e071754f18931be4266f0c1427d952d2a"),
                damage.revision().supersedes());
        var damagePayload = assertInstanceOf(DamageMechanic.class, damage.payload());
        assertEquals(new DiceAmount(1, 8, 0), damagePayload.amount());
        assertEquals(new FixedDamageType("cold"), damagePayload.damageType());
        var progression = damagePayload.characterLevelScaling();
        assertNotNull(progression);
        assertEquals(List.of(
                        new CharacterLevelDamageTier(5, new DiceAmount(2, 8, 0)),
                        new CharacterLevelDamageTier(11, new DiceAmount(3, 8, 0)),
                        new CharacterLevelDamageTier(17, new DiceAmount(4, 8, 0))),
                progression.tiers());
        for (int level : List.of(1, 4, 5, 10, 11, 16, 17)) {
            assertEquals(rayOfFrostDamageAtLevel(progression, level),
                    new DiceAmount(level < 5 ? 1 : level < 11 ? 2 : level < 17 ? 3 : 4, 8, 0),
                    "character level " + level);
        }
        var damageOutcome = assertInstanceOf(AppliesOnAttackRollOutcome.class,
                damage.context().bindings().getFirst());
        assertEquals(attack.revision().reference(attack.id()), damageOutcome.attackRoll());
        assertEquals(AttackRollOutcome.HIT, damageOutcome.outcome());

        var speed = document.interpretations().stream()
                .filter(record -> record.id().value().endsWith(":movement-speed")).findFirst().orElseThrow();
        assertEquals(1, speed.revision().number());
        assertEquals("587d0e43440e033c93976fbd7d7a7e8b6547ddc0450acfff711957c130398bc2",
                speed.revision().contentSha256());
        var speedPayload = assertInstanceOf(StatisticModifierMechanic.class, speed.payload());
        assertEquals(List.of(ModifiedStatistic.MOVEMENT_SPEED), speedPayload.statistics());
        assertEquals(ModifierOperation.SUBTRACT, speedPayload.operation());
        assertEquals(new IntegerAmount(10), speedPayload.amount());
        assertEquals(TargetScope.SINGLE_TARGET_CREATURE,
                assertInstanceOf(SpellTargetSubject.class, speed.context().subject()).scope());
        var speedOutcome = assertInstanceOf(AppliesOnAttackRollOutcome.class,
                speed.context().bindings().getFirst());
        assertEquals(attack.revision().reference(attack.id()), speedOutcome.attackRoll());
        assertEquals(AttackRollOutcome.HIT, speedOutcome.outcome());
        var duration = assertInstanceOf(AppliesForDuration.class, speed.context().bindings().get(1));
        assertEquals(new UntilTurnBoundary(TurnActor.CASTER, TurnBoundary.START_OF_NEXT_TURN), duration.duration());

        assertTrue(document.unsupported().stream()
                .noneMatch(value -> value.id().equals("ray-of-frost-cantrip-scaling")));
        assertEquals(1, document.coverage().unsupportedCount());
        assertEquals("spell:phb:ray-of-frost:unsupported:attack-range", document.unsupported().getFirst().id());
        assertTrue(damage.evidenceAssociations().stream().anyMatch(association ->
                association.claimPath().equals("/payload/characterLevelScaling")
                        && association.role() == com.thedndpub.data.mechanics.model.EvidenceRole.SCALING));
        for (int level : List.of(5, 11, 17)) {
            var structured = assertInstanceOf(StructuredFieldMechanicsEvidence.class, document.evidence().stream()
                    .filter(value -> damage.evidenceAssociations().stream()
                            .anyMatch(association -> association.evidenceId().equals(value.id())))
                    .filter(StructuredFieldMechanicsEvidence.class::isInstance)
                    .map(StructuredFieldMechanicsEvidence.class::cast)
                    .filter(value -> value.jsonPointer().endsWith("/scaling/" + level)).findFirst().orElseThrow());
            assertEquals("/spell/273/scalingLevelDice/scaling/" + level, structured.jsonPointer());
            String tierPhrase = level + "th level ({@damage " + (level == 5 ? "2d8" : level == 11 ? "3d8" : "4d8") + "})";
            var tierEvidence = assertInstanceOf(TextMechanicsEvidence.class, document.evidence().stream()
                    .filter(value -> value instanceof TextMechanicsEvidence text
                            && text.syntax().location().jsonPointer().equals("/spell/273/entries/1")
                            && sourceContainsSpan(scaling, text, tierPhrase))
                    .findFirst().orElseThrow());
            int tierStart = scaling.indexOf(tierPhrase);
            assertEquals(tierStart, tierEvidence.syntax().span().startInclusive());
            assertEquals(tierStart + tierPhrase.length(), tierEvidence.syntax().span().endExclusive());
        }
        String scalingJson = com.thedndpub.data.mechanics.model.MechanicsJson.mapper()
                .writeValueAsString(damagePayload);
        assertTrue(scalingJson.contains("characterLevelScaling"));
        assertFalse(scalingJson.contains("spellSlotScaling"));

        assertEquals("c9e54cce55e7ce0fc988feb1b604bf505faa5c042635542fa6b6ed36724e297a",
                document.sources().getFirst().resourceSha256());
        assertTrue(document.interpretations().stream().allMatch(record ->
                record.review().recordedStatus() == com.thedndpub.data.mechanics.review.MechanicsReviewStatus.PENDING_REVIEW
                        && record.licensing().status() == MechanicsLicensingStatus.NEEDS_REVIEW));
        assertEquals(0, document.reviewSummary().verified());
        assertTrue(document.unsupported().stream().allMatch(value ->
                value.evidenceIds().stream().allMatch(id -> document.evidence().stream()
                        .anyMatch(evidence -> evidence.id().equals(id)))));
        assertTrue(body.contains("its speed is reduced by 10 feet until the start of your next turn"));

        byte[] first = com.thedndpub.data.mechanics.model.MechanicsJson.mapper().writeValueAsBytes(document);
        byte[] second = com.thedndpub.data.mechanics.model.MechanicsJson.mapper().writeValueAsBytes(document);
        assertArrayEquals(first, second);
        assertEquals(document, com.thedndpub.data.mechanics.model.MechanicsJson.mapper()
                .readValue(first, MechanicsDocument.class));
    }

    private DiceAmount rayOfFrostDamageAtLevel(CharacterLevelDamageScaling scaling, int characterLevel) {
        DiceAmount amount = new DiceAmount(1, 8, 0);
        for (CharacterLevelDamageTier tier : scaling.tiers()) {
            if (characterLevel >= tier.minimumCharacterLevel()) {
                amount = tier.amount();
            }
        }
        return amount;
    }

    private boolean sourceContainsSpan(String text, TextMechanicsEvidence evidence, String phrase) {
        int start = text.indexOf(phrase);
        return start >= 0 && evidence.syntax().span().startInclusive() == start
                && evidence.syntax().span().endExclusive() == start + phrase.length();
    }

    private void assertHitReference(MechanicRecord attack, MechanicRecord dependent) {
        var binding = dependent.context().bindings().stream()
                .filter(AppliesOnAttackRollOutcome.class::isInstance)
                .map(AppliesOnAttackRollOutcome.class::cast).findFirst().orElseThrow();
        assertEquals(attack.revision().reference(attack.id()), binding.attackRoll());
        assertEquals(AttackRollOutcome.HIT, binding.outcome());
    }

    private void assertTextEvidence(
            MechanicsDocument document,
            MechanicRecord claim,
            String path,
            EvidenceRole role,
            String pointer,
            String phrase,
            String sourceText) {
        var association = claim.evidenceAssociations().stream()
                .filter(value -> value.claimPath().equals(path) && value.role() == role)
                .findFirst().orElseThrow();
        var evidence = assertInstanceOf(TextMechanicsEvidence.class, document.evidence().stream()
                .filter(value -> value.id().equals(association.evidenceId())).findFirst().orElseThrow());
        assertEquals(pointer, evidence.syntax().location().jsonPointer());
        assertTrue(sourceContainsSpan(sourceText, evidence, phrase), phrase);
    }

    private TextMechanicsEvidence unsupportedText(
            MechanicsDocument document,
            com.thedndpub.data.mechanics.model.UnsupportedMechanicsConstruct unsupported,
            String pointer) {
        var evidence = assertInstanceOf(TextMechanicsEvidence.class, document.evidence().stream()
                .filter(value -> unsupported.evidenceIds().contains(value.id())).findFirst().orElseThrow());
        assertEquals(pointer, evidence.syntax().location().jsonPointer());
        return evidence;
    }

    private com.thedndpub.data.mechanics.model.UnsupportedMechanicsConstruct unsupported(
            MechanicsDocument document,
            String id) {
        return document.unsupported().stream().filter(value -> value.id().equals(id))
                .findFirst().orElseThrow();
    }

    private void assertReviewPending(MechanicsDocument document) {
        assertTrue(document.interpretations().stream().allMatch(record ->
                record.review().recordedStatus() == MechanicsReviewStatus.PENDING_REVIEW
                        && record.licensing().status() == MechanicsLicensingStatus.NEEDS_REVIEW));
        assertEquals(0, document.reviewSummary().verified());
        assertEquals(0, document.reviewSummary().rejected());
    }

    private void assertStaleAttackReferenceRejected(MechanicsDocument document) {
        var attack = document.interpretations().stream()
                .filter(record -> record.payload() instanceof AttackRollMechanic).findFirst().orElseThrow();
        var dependent = document.interpretations().stream().filter(record -> record != attack
                && record.context().bindings().stream().anyMatch(AppliesOnAttackRollOutcome.class::isInstance))
                .findFirst().orElseThrow();
        var bindings = new java.util.ArrayList<>(dependent.context().bindings());
        int index = java.util.stream.IntStream.range(0, bindings.size())
                .filter(value -> bindings.get(value) instanceof AppliesOnAttackRollOutcome).findFirst().orElseThrow();
        var existing = (AppliesOnAttackRollOutcome) bindings.get(index);
        bindings.set(index, new AppliesOnAttackRollOutcome(
                new MechanicRevisionReference(attack.id(), attack.revision().number(), "0".repeat(64)),
                existing.outcome(), existing.evidenceIds()));
        var stale = MechanicRecord.propose(dependent.id(), dependent.payload(),
                new MechanicContext(dependent.context().subject(), bindings), dependent.evidenceAssociations(),
                dependent.derivation(), dependent.assessment());
        var records = document.interpretations().stream()
                .map(record -> record == dependent ? stale : record).toList();
        assertThrows(IllegalArgumentException.class, () -> MechanicsDocument.create(
                document.owner(), document.revision().number(), document.sources(), document.processing(), records,
                document.observations(), document.unsupported(), document.evidence(), document.coverage()));
    }

    private void assertStaleModifierSelectorRejected(MechanicsDocument document) {
        var attack = document.interpretations().stream()
                .filter(record -> record.payload() instanceof AttackRollMechanic).findFirst().orElseThrow();
        var modifier = document.interpretations().stream()
                .filter(record -> record.payload() instanceof AttackRollModifierMechanic).findFirst().orElseThrow();
        var payload = assertInstanceOf(AttackRollModifierMechanic.class, modifier.payload());
        var staleReference = new MechanicRevisionReference(attack.id(), attack.revision().number(), "0".repeat(64));
        var stalePayload = new AttackRollModifierMechanic(payload.modifier(),
                new ReferencedAttackRollSelector(staleReference));
        var staleModifier = MechanicRecord.propose(modifier.id(), stalePayload, modifier.context(),
                modifier.evidenceAssociations(), modifier.derivation(), modifier.assessment());
        var records = document.interpretations().stream()
                .map(record -> record == modifier ? staleModifier : record).toList();
        assertThrows(IllegalArgumentException.class, () -> MechanicsDocument.create(
                document.owner(), document.revision().number(), document.sources(), document.processing(), records,
                document.observations(), document.unsupported(), document.evidence(), document.coverage()));
    }

    private void assertContradictoryArmorQualifierRejected(MechanicsDocument document) {
        var modifier = document.interpretations().stream()
                .filter(record -> record.payload() instanceof AttackRollModifierMechanic).findFirst().orElseThrow();
        var metalArmor = modifier.context().bindings().stream().filter(AppliesWhen.class::isInstance)
                .map(AppliesWhen.class::cast).findFirst().orElseThrow();
        var qualifierEvidence = document.evidence().stream()
                .filter(evidence -> metalArmor.evidenceIds().contains(evidence.id())).findFirst().orElseThrow();
        var context = new MechanicContext(modifier.context().subject(), List.of(metalArmor,
                new AppliesWhen(ApplicabilityCondition.TARGET_NOT_WEARING_ARMOR, metalArmor.evidenceIds())));
        var associations = new java.util.ArrayList<>(modifier.evidenceAssociations());
        associations.add(MechanicEvidenceAssociation.of(qualifierEvidence,
                "/context/bindings/1", EvidenceRole.QUALIFIER));
        var contradictory = MechanicRecord.propose(modifier.id(), modifier.payload(), context, associations,
                modifier.derivation(), modifier.assessment());
        var records = document.interpretations().stream()
                .map(record -> record == modifier ? contradictory : record).toList();
        assertThrows(IllegalArgumentException.class, () -> MechanicsDocument.create(
                document.owner(), document.revision().number(), document.sources(), document.processing(), records,
                document.observations(), document.unsupported(), document.evidence(), document.coverage()));
    }

    private void assertDeterministic(MechanicsDocument document) throws Exception {
        byte[] first = com.thedndpub.data.mechanics.model.MechanicsJson.mapper().writeValueAsBytes(document);
        byte[] second = com.thedndpub.data.mechanics.model.MechanicsJson.mapper().writeValueAsBytes(document);
        assertArrayEquals(first, second);
        assertEquals(document, com.thedndpub.data.mechanics.model.MechanicsJson.mapper()
                .readValue(first, MechanicsDocument.class));
    }

    @Test
    void xphbSpellAttackOwnersRemainUnactivated() {
        var xphbFireBolt = inspection.findById(spells.findById("spell:xphb:fire-bolt").id());
        assertEquals(ProcessingState.NOT_ATTEMPTED, xphbFireBolt.processing().state());
        assertTrue(xphbFireBolt.interpretations().isEmpty());
    }

    @Test
    void fireBoltTargetsExactlyOneCreatureOrObjectAndKeepsIgnitionUnsupported() throws Exception {
        var extractionInput = input("fire-bolt");
        var body = ((TextEntryDte) extractionInput.spell().entries().getFirst()).text();
        var scalingText = ((TextEntryDte) extractionInput.spell().entries().get(1)).text();
        var document = extract(extractionInput);

        assertEquals("spell:phb:fire-bolt", document.owner().id());
        assertEquals("c9e54cce55e7ce0fc988feb1b604bf505faa5c042635542fa6b6ed36724e297a",
                document.sources().getFirst().resourceSha256());
        assertEquals(ProcessingState.PROCESSED, document.processing().state());
        assertEquals(2, document.interpretations().size());

        var attack = document.interpretations().stream()
                .filter(record -> record.payload() instanceof AttackRollMechanic).findFirst().orElseThrow();
        assertEquals("mechanic:spell:phb:fire-bolt:attack-roll", attack.id().value());
        assertEquals(1, attack.revision().number());
        var attackPayload = assertInstanceOf(AttackRollMechanic.class, attack.payload());
        assertEquals(AttackKind.RANGED_SPELL, attackPayload.attackKind());
        assertEquals(AttackActorRole.CASTER, attackPayload.actorRole());
        assertEquals(1, attackPayload.attackCount());
        assertTrue(attackPayload.attacksIndependent());
        assertEquals(AttackActivationTiming.CAST_TIME, attackPayload.activationTiming());

        var attackSubject = assertInstanceOf(SpellTargetSubject.class, attack.context().subject());
        assertEquals(TargetScope.SINGLE_TARGET, attackSubject.scope(), "one target is selected for this attack");
        assertEquals(List.of(TargetCategory.CREATURE, TargetCategory.OBJECT),
                attackSubject.eligibleTargetCategories(), "categories are alternatives for the one target");
        assertNull(attackSubject.maximumTargets());
        assertNull(attackSubject.creatureTypeRestriction());
        for (int index = 0; index < attackSubject.eligibleTargetCategories().size(); index++) {
            int categoryIndex = index;
            var association = attack.evidenceAssociations().stream()
                    .filter(value -> value.claimPath().equals(
                            "/context/subject/eligibleTargetCategories/" + categoryIndex))
                    .findFirst().orElseThrow();
            var targetEvidence = assertInstanceOf(TextMechanicsEvidence.class, document.evidence().stream()
                    .filter(value -> value.id().equals(association.evidenceId())).findFirst().orElseThrow());
            assertEquals("/spell/136/entries/0", targetEvidence.syntax().location().jsonPointer());
            assertTrue(body.substring(targetEvidence.syntax().span().startInclusive(),
                    targetEvidence.syntax().span().endExclusive()).contains("creature or object within range"));
        }

        var damage = document.interpretations().stream()
                .filter(record -> record.payload() instanceof DamageMechanic).findFirst().orElseThrow();
        assertEquals("mechanic:spell:phb:fire-bolt:damage", damage.id().value());
        assertEquals(1, damage.revision().number());
        var damagePayload = assertInstanceOf(DamageMechanic.class, damage.payload());
        assertEquals(new DiceAmount(1, 10, 0), damagePayload.amount());
        assertEquals(new FixedDamageType("fire"), damagePayload.damageType());
        assertEquals(List.of(
                        new CharacterLevelDamageTier(5, new DiceAmount(2, 10, 0)),
                        new CharacterLevelDamageTier(11, new DiceAmount(3, 10, 0)),
                        new CharacterLevelDamageTier(17, new DiceAmount(4, 10, 0))),
                damagePayload.characterLevelScaling().tiers());
        assertEquals(attackSubject, damage.context().subject());
        var hitBinding = assertInstanceOf(AppliesOnAttackRollOutcome.class,
                damage.context().bindings().getFirst());
        assertEquals(attack.revision().reference(attack.id()), hitBinding.attackRoll());
        assertEquals(AttackRollOutcome.HIT, hitBinding.outcome());
        assertEquals("phb-fire-bolt-attack-roll-v1", attack.derivation().patternId());
        assertEquals("phb-fire-bolt-hit-damage-v1", damage.derivation().patternId());

        var ignition = document.unsupported().stream()
                .filter(value -> value.id().equals("fire-bolt-object-ignition")).findFirst().orElseThrow();
        assertEquals(1, document.unsupported().size());
        assertEquals(MechanicFamily.UNDETERMINED, ignition.candidateFamily());
        assertEquals(UnsupportedReason.FAMILY_NOT_IMPLEMENTED, ignition.reason());
        var ignitionEvidence = assertInstanceOf(TextMechanicsEvidence.class, document.evidence().stream()
                .filter(value -> ignition.evidenceIds().contains(value.id())).findFirst().orElseThrow());
        assertEquals("/spell/136/entries/0", ignitionEvidence.syntax().location().jsonPointer());
        String ignitionText = body.substring(ignitionEvidence.syntax().span().startInclusive(),
                ignitionEvidence.syntax().span().endExclusive());
        assertEquals("A flammable object hit by this spell ignites if it isn't being worn or carried.",
                ignitionText);
        assertFalse(document.interpretations().stream().anyMatch(record ->
                record.derivation().patternId().contains("ignition")));

        for (int level : List.of(5, 11, 17)) {
            var evidence = document.evidence().stream().filter(StructuredFieldMechanicsEvidence.class::isInstance)
                    .map(StructuredFieldMechanicsEvidence.class::cast)
                    .filter(value -> value.jsonPointer().endsWith("/scaling/" + level)).findFirst().orElseThrow();
            assertEquals("/spell/136/scalingLevelDice/scaling/" + level, evidence.jsonPointer());
            String phrase = level + "th level ({@damage " + (level == 5 ? "2d10" : level == 11 ? "3d10" : "4d10") + "})";
            assertTrue(scalingText.contains(phrase));
            assertTrue(damage.evidenceAssociations().stream().anyMatch(association ->
                    association.evidenceId().equals(evidence.id())
                            && association.claimPath().startsWith("/payload/characterLevelScaling/tiers/")));
        }

        assertTrue(document.interpretations().stream().allMatch(record ->
                record.review().recordedStatus() == MechanicsReviewStatus.PENDING_REVIEW
                        && record.licensing().status() == MechanicsLicensingStatus.NEEDS_REVIEW));
        assertEquals(0, document.reviewSummary().verified());
        byte[] first = com.thedndpub.data.mechanics.model.MechanicsJson.mapper().writeValueAsBytes(document);
        byte[] second = com.thedndpub.data.mechanics.model.MechanicsJson.mapper().writeValueAsBytes(document);
        assertArrayEquals(first, second);
        assertEquals(document, com.thedndpub.data.mechanics.model.MechanicsJson.mapper()
                .readValue(first, MechanicsDocument.class));
    }

    @Test
    void chillTouchBindsHitPointRecoveryAndUndeadAttackDisadvantage() throws Exception {
        var extractionInput = input("chill-touch");
        String body = ((TextEntryDte) extractionInput.spell().entries().getFirst()).text();
        String scalingText = ((TextEntryDte) extractionInput.spell().entries().get(2)).text();
        var document = extract(extractionInput);

        assertEquals("spell:phb:chill-touch", document.owner().id());
        assertEquals("PHB", document.owner().source().identity().code());
        assertEquals("c9e54cce55e7ce0fc988feb1b604bf505faa5c042635542fa6b6ed36724e297a",
                document.sources().getFirst().resourceSha256());
        assertEquals(ProcessingState.PROCESSED, document.processing().state());
        assertEquals(4, document.interpretations().size());

        var attack = document.interpretations().stream()
                .filter(record -> record.payload() instanceof AttackRollMechanic).findFirst().orElseThrow();
        assertEquals("mechanic:spell:phb:chill-touch:attack-roll", attack.id().value());
        assertEquals(1, attack.revision().number());
        var attackPayload = assertInstanceOf(AttackRollMechanic.class, attack.payload());
        assertEquals(AttackKind.RANGED_SPELL, attackPayload.attackKind());
        assertEquals(AttackActorRole.CASTER, attackPayload.actorRole());
        assertEquals(1, attackPayload.attackCount());
        assertTrue(attackPayload.attacksIndependent());
        assertEquals(AttackActivationTiming.CAST_TIME, attackPayload.activationTiming());
        assertTrue(attack.context().bindings().isEmpty(), "effect dependencies point to the attack, not vice versa");
        assertEquals(TargetScope.SINGLE_TARGET_CREATURE,
                assertInstanceOf(SpellTargetSubject.class, attack.context().subject()).scope());
        assertTextEvidence(document, attack, "/context/subject", EvidenceRole.SUBJECT,
                "/spell/45/entries/0", "a creature within range", body);

        var damage = document.interpretations().stream()
                .filter(record -> record.payload() instanceof DamageMechanic).findFirst().orElseThrow();
        assertEquals("mechanic:spell:phb:chill-touch:damage", damage.id().value());
        assertEquals(1, damage.revision().number());
        var damagePayload = assertInstanceOf(DamageMechanic.class, damage.payload());
        assertEquals(new DiceAmount(1, 8, 0), damagePayload.amount());
        assertEquals(new FixedDamageType("necrotic"), damagePayload.damageType());
        assertEquals(List.of(
                        new CharacterLevelDamageTier(5, new DiceAmount(2, 8, 0)),
                        new CharacterLevelDamageTier(11, new DiceAmount(3, 8, 0)),
                        new CharacterLevelDamageTier(17, new DiceAmount(4, 8, 0))),
                damagePayload.characterLevelScaling().tiers());
        assertHitReference(attack, damage);
        assertTextEvidence(document, damage, "/payload/amount", EvidenceRole.VALUE,
                "/spell/45/entries/0", "{@damage 1d8}", body);
        for (int level : List.of(5, 11, 17)) {
            var evidence = document.evidence().stream().filter(StructuredFieldMechanicsEvidence.class::isInstance)
                    .map(StructuredFieldMechanicsEvidence.class::cast)
                    .filter(value -> value.jsonPointer().endsWith("/scaling/" + level)).findFirst().orElseThrow();
            assertEquals("/spell/45/scalingLevelDice/scaling/" + level, evidence.jsonPointer());
            assertTrue(damage.evidenceAssociations().stream().anyMatch(association ->
                    association.evidenceId().equals(evidence.id())
                            && association.claimPath().startsWith("/payload/characterLevelScaling/tiers/")));
        }
        assertTrue(scalingText.contains("when you reach 5th level ({@damage 2d8})"));

        var prevention = document.interpretations().stream()
                .filter(record -> record.payload() instanceof HitPointRecoveryPreventionMechanic)
                .findFirst().orElseThrow();
        assertEquals("mechanic:spell:phb:chill-touch:hit-point-recovery-prevention", prevention.id().value());
        assertEquals(1, prevention.revision().number());
        assertHitReference(attack, prevention);
        var duration = prevention.context().bindings().stream().filter(AppliesForDuration.class::isInstance)
                .map(AppliesForDuration.class::cast).findFirst().orElseThrow();
        assertEquals(new UntilTurnBoundary(TurnActor.CASTER, TurnBoundary.START_OF_NEXT_TURN), duration.duration());
        assertTextEvidence(document, prevention, "/payload", EvidenceRole.EFFECT_ROLE,
                "/spell/45/entries/0", "it can't regain hit points", body);
        assertTextEvidence(document, prevention, "/context/bindings/0", EvidenceRole.TRIGGER,
                "/spell/45/entries/0", "On a hit", body);
        assertTextEvidence(document, prevention, "/context/bindings/1", EvidenceRole.QUALIFIER,
                "/spell/45/entries/0", "until the start of your next turn", body);

        var disadvantage = document.interpretations().stream()
                .filter(record -> record.payload() instanceof AttackRollModifierMechanic).findFirst().orElseThrow();
        assertEquals("mechanic:spell:phb:chill-touch:undead-attack-roll-disadvantage", disadvantage.id().value());
        assertEquals(1, disadvantage.revision().number());
        var modifier = assertInstanceOf(AttackRollModifierMechanic.class, disadvantage.payload());
        assertEquals(AttackRollModifier.DISADVANTAGE, modifier.modifier());
        assertEquals(new FutureAttackRollSelector(AttackRollParticipant.AFFECTED_SUBJECT,
                        AttackRollParticipant.CASTER), modifier.affectedRolls());
        assertEquals(3, disadvantage.context().bindings().size());
        assertHitReference(attack, disadvantage);
        assertEquals(ApplicabilityCondition.AFFECTED_SUBJECT_IS_UNDEAD,
                disadvantage.context().bindings().stream().filter(AppliesWhen.class::isInstance)
                        .map(AppliesWhen.class::cast).map(AppliesWhen::condition).findFirst().orElseThrow());
        var disadvantageDuration = disadvantage.context().bindings().stream()
                .filter(AppliesForDuration.class::isInstance).map(AppliesForDuration.class::cast)
                .findFirst().orElseThrow();
        assertEquals(new UntilTurnBoundary(TurnActor.CASTER, TurnBoundary.END_OF_NEXT_TURN),
                disadvantageDuration.duration());
        String undeadText = ((TextEntryDte) extractionInput.spell().entries().get(1)).text();
        assertTextEvidence(document, disadvantage, "/payload", EvidenceRole.EFFECT_ROLE,
                "/spell/45/entries/1", "has disadvantage on attack rolls against you", undeadText);
        assertTextEvidence(document, disadvantage, "/payload/affectedRolls/actor", EvidenceRole.SUBJECT,
                "/spell/45/entries/1", "attack rolls against you", undeadText);
        assertTextEvidence(document, disadvantage, "/payload/affectedRolls/target", EvidenceRole.REFERENCE,
                "/spell/45/entries/1", "attack rolls against you", undeadText);
        assertTextEvidence(document, disadvantage, "/context/subject", EvidenceRole.SUBJECT,
                "/spell/45/entries/1", "an undead target", undeadText);
        assertTextEvidence(document, disadvantage, "/context/bindings/0", EvidenceRole.TRIGGER,
                "/spell/45/entries/1", "If you hit", undeadText);
        assertTextEvidence(document, disadvantage, "/context/bindings/1", EvidenceRole.QUALIFIER,
                "/spell/45/entries/1", "an undead target", undeadText);
        assertTextEvidence(document, disadvantage, "/context/bindings/2", EvidenceRole.DURATION,
                "/spell/45/entries/1", "until the end of your next turn", undeadText);
        assertEquals("spell:phb:chill-touch:unsupported:attack-range", document.unsupported().getFirst().id());
        assertFalse(document.interpretations().stream()
                .anyMatch(record -> record.payload() instanceof StatisticModifierMechanic));
        assertReviewPending(document);
        assertStaleAttackReferenceRejected(document);
        assertDeterministic(document);
    }

    @Test
    void shockingGraspBindsReactionPreventionAndMetalArmorAdvantage() throws Exception {
        var extractionInput = input("shocking-grasp");
        String body = ((TextEntryDte) extractionInput.spell().entries().getFirst()).text();
        String scalingText = ((TextEntryDte) extractionInput.spell().entries().get(1)).text();
        var document = extract(extractionInput);

        assertEquals("spell:phb:shocking-grasp", document.owner().id());
        assertEquals("PHB", document.owner().source().identity().code());
        assertEquals(ProcessingState.PROCESSED, document.processing().state());
        assertEquals(4, document.interpretations().size());

        var attack = document.interpretations().stream()
                .filter(record -> record.payload() instanceof AttackRollMechanic).findFirst().orElseThrow();
        assertEquals("mechanic:spell:phb:shocking-grasp:attack-roll", attack.id().value());
        assertEquals(1, attack.revision().number());
        var attackPayload = assertInstanceOf(AttackRollMechanic.class, attack.payload());
        assertEquals(AttackKind.MELEE_SPELL, attackPayload.attackKind());
        assertEquals(AttackActorRole.CASTER, attackPayload.actorRole());
        assertEquals(1, attackPayload.attackCount());
        assertTrue(attackPayload.attacksIndependent());
        assertEquals(AttackActivationTiming.CAST_TIME, attackPayload.activationTiming());
        assertTrue(attack.context().bindings().isEmpty(), "effect dependencies point to the attack, not vice versa");
        assertEquals(TargetScope.SINGLE_TARGET_CREATURE,
                assertInstanceOf(SpellTargetSubject.class, attack.context().subject()).scope());
        assertTextEvidence(document, attack, "/context/subject", EvidenceRole.SUBJECT,
                "/spell/297/entries/0", "a creature you try to touch", body);

        var damage = document.interpretations().stream()
                .filter(record -> record.payload() instanceof DamageMechanic).findFirst().orElseThrow();
        assertEquals("mechanic:spell:phb:shocking-grasp:damage", damage.id().value());
        assertEquals(new DiceAmount(1, 8, 0), assertInstanceOf(DamageMechanic.class, damage.payload()).amount());
        var damagePayload = assertInstanceOf(DamageMechanic.class, damage.payload());
        assertEquals(new FixedDamageType("lightning"), damagePayload.damageType());
        assertEquals(List.of(
                        new CharacterLevelDamageTier(5, new DiceAmount(2, 8, 0)),
                        new CharacterLevelDamageTier(11, new DiceAmount(3, 8, 0)),
                        new CharacterLevelDamageTier(17, new DiceAmount(4, 8, 0))),
                damagePayload.characterLevelScaling().tiers());
        assertHitReference(attack, damage);
        assertTextEvidence(document, damage, "/payload/amount", EvidenceRole.VALUE,
                "/spell/297/entries/0", "{@damage 1d8}", body);
        assertTrue(scalingText.contains("and 17th level ({@damage 4d8})"));

        var prevention = document.interpretations().stream()
                .filter(record -> record.payload() instanceof ReactionPreventionMechanic).findFirst().orElseThrow();
        assertEquals("mechanic:spell:phb:shocking-grasp:reaction-prevention", prevention.id().value());
        assertEquals(1, prevention.revision().number());
        assertHitReference(attack, prevention);
        var duration = prevention.context().bindings().stream().filter(AppliesForDuration.class::isInstance)
                .map(AppliesForDuration.class::cast).findFirst().orElseThrow();
        assertEquals(new UntilTurnBoundary(TurnActor.AFFECTED_SUBJECT, TurnBoundary.START_OF_NEXT_TURN), duration.duration());
        assertTextEvidence(document, prevention, "/payload", EvidenceRole.EFFECT_ROLE,
                "/spell/297/entries/0", "it can't take reactions", body);
        assertTextEvidence(document, prevention, "/context/bindings/0", EvidenceRole.TRIGGER,
                "/spell/297/entries/0", "On a hit", body);
        assertTextEvidence(document, prevention, "/context/bindings/1", EvidenceRole.QUALIFIER,
                "/spell/297/entries/0", "until the start of its next turn", body);

        var advantage = document.interpretations().stream()
                .filter(record -> record.payload() instanceof AttackRollModifierMechanic).findFirst().orElseThrow();
        assertEquals("mechanic:spell:phb:shocking-grasp:metal-armor-attack-roll-advantage", advantage.id().value());
        assertEquals(1, advantage.revision().number());
        var modifier = assertInstanceOf(AttackRollModifierMechanic.class, advantage.payload());
        assertEquals(AttackRollModifier.ADVANTAGE, modifier.modifier());
        var referenced = assertInstanceOf(ReferencedAttackRollSelector.class, modifier.affectedRolls());
        assertEquals(attack.revision().reference(attack.id()), referenced.attackRoll());
        assertEquals(1, advantage.context().bindings().size());
        assertEquals(ApplicabilityCondition.ATTACK_ROLL_TARGET_WEARING_ARMOR_MADE_OF_METAL,
                advantage.context().bindings().stream().filter(AppliesWhen.class::isInstance)
                        .map(AppliesWhen.class::cast).map(AppliesWhen::condition).findFirst().orElseThrow());
        assertFalse(advantage.context().bindings().stream().anyMatch(AppliesOnAttackRollOutcome.class::isInstance),
                "This modifier applies before an attack outcome and does not depend on HIT");
        assertFalse(advantage.context().bindings().stream().anyMatch(AppliesForDuration.class::isInstance));
        assertTextEvidence(document, advantage, "/payload", EvidenceRole.EFFECT_ROLE,
                "/spell/297/entries/0", "You have advantage on the attack roll", body);
        assertTextEvidence(document, advantage, "/payload/affectedRolls/attackRoll", EvidenceRole.REFERENCE,
                "/spell/297/entries/0", "Make a melee spell attack against the target", body);
        assertTextEvidence(document, advantage, "/context/subject", EvidenceRole.SUBJECT,
                "/spell/297/entries/0", "a creature you try to touch", body);
        assertTextEvidence(document, advantage, "/context/bindings/0", EvidenceRole.QUALIFIER,
                "/spell/297/entries/0", "the target is wearing armor made of metal", body);
        assertEquals("spell:phb:shocking-grasp:unsupported:attack-range", document.unsupported().getFirst().id());
        assertFalse(document.interpretations().stream().anyMatch(record ->
                record.payload() instanceof StatisticModifierMechanic statisticModifier
                        && statisticModifier.statistics().contains(ModifiedStatistic.ATTACK_ROLL)));
        assertReviewPending(document);
        assertStaleAttackReferenceRejected(document);
        assertStaleModifierSelectorRejected(document);
        assertContradictoryArmorQualifierRejected(document);
        assertDeterministic(document);
    }

    @Test
    void cantripCoverageAccountsForEachWholeSpellWhilePreservingUnmodeledAttackRange() throws Exception {
        for (String id : List.of("chill-touch", "ray-of-frost", "shocking-grasp")) {
            MechanicsDocument document = extract(input(id));
            assertEquals(CoverageStatus.COMPLETE_WITHIN_SCOPE, document.coverage().status(), id);
            assertEquals(com.thedndpub.data.mechanics.model.CoverageScopeExtent.FULL_SPELL,
                    document.coverage().scopeExtent(), id);
            assertTrue(document.coverage().unassessedSourcePaths().isEmpty(), id);
            assertTrue(document.coverage().consideredSourcePaths().containsAll(List.of(
                    "/spell/" + spellIndex(id) + "/time", "/spell/" + spellIndex(id) + "/range",
                    "/spell/" + spellIndex(id) + "/level", "/spell/" + spellIndex(id) + "/school",
                    "/spell/" + spellIndex(id) + "/duration", "/spell/" + spellIndex(id) + "/components",
                    "/spell/" + spellIndex(id) + "/entries", "/spell/" + spellIndex(id) + "/spellAttack",
                    "/spell/" + spellIndex(id) + "/damageInflict",
                    "/spell/" + spellIndex(id) + "/scalingLevelDice")), id);
            assertEquals(1, document.unsupported().size(), id);
            assertTrue(document.unsupported().getFirst().id().endsWith(":unsupported:attack-range"), id);
            assertEquals(MechanicsCompleteness.PARTIAL, document.interpretations().getFirst()
                    .assessment().completeness(), id);
            assertEquals(0, document.reviewSummary().verified(), id);
        }
    }

    private static int spellIndex(String id) {
        return switch (id) {
            case "chill-touch" -> 45;
            case "ray-of-frost" -> 273;
            case "shocking-grasp" -> 297;
            default -> throw new IllegalArgumentException("Unknown test owner");
        };
    }

    @Test
    void conditionalAttackRulesAbstainOnMissingConflictingAndMalformedSourceEvidence() throws IOException {
        var missingAttackText = input("chill-touch").changed(mapper, root -> root.withArray("entries").set(0,
                mapper.getNodeFactory().textNode(root.at("/entries/0").textValue()
                        .replace("Make a ranged spell attack", "Make a ranged weapon attack"))));
        var missingAttackDocument = extract(missingAttackText);
        assertFalse(missingAttackDocument.interpretations().stream()
                .anyMatch(record -> record.payload() instanceof AttackRollMechanic));
        assertFalse(missingAttackDocument.interpretations().stream()
                .anyMatch(record -> record.payload() instanceof DamageMechanic));

        var conflictingScalingText = input("chill-touch").changed(mapper, root -> root.withArray("entries").set(2,
                mapper.getNodeFactory().textNode(root.at("/entries/2").textValue()
                        .replace("17th level", "16th level"))));
        var conflictingScalingDocument = extract(conflictingScalingText);
        var damage = conflictingScalingDocument.interpretations().stream()
                .filter(record -> record.payload() instanceof DamageMechanic).findFirst().orElseThrow();
        assertNull(((DamageMechanic) damage.payload()).characterLevelScaling());
        assertTrue(conflictingScalingDocument.unsupported().stream()
                .anyMatch(value -> value.id().equals("chill-touch-cantrip-scaling")));

        var missingScalingField = input("shocking-grasp").changed(mapper, root ->
                ((com.fasterxml.jackson.databind.node.ObjectNode) root.at("/scalingLevelDice/scaling")).remove("17"));
        var missingScalingDocument = extract(missingScalingField);
        var shockingDamage = missingScalingDocument.interpretations().stream()
                .filter(record -> record.payload() instanceof DamageMechanic).findFirst().orElseThrow();
        assertNull(((DamageMechanic) shockingDamage.payload()).characterLevelScaling());
        assertTrue(missingScalingDocument.unsupported().stream()
                .anyMatch(value -> value.id().equals("shocking-grasp-cantrip-scaling")));

        var carriedMetalText = input("shocking-grasp").changed(mapper, root -> root.withArray("entries").set(0,
                mapper.getNodeFactory().textNode(root.at("/entries/0").textValue().replace(
                        "if the target is wearing armor made of metal",
                        "if the target is carrying metal"))));
        var carriedMetalDocument = extract(carriedMetalText);
        assertFalse(carriedMetalDocument.interpretations().stream()
                .anyMatch(record -> record.payload() instanceof AttackRollModifierMechanic),
                "Carrying metal does not satisfy the source's worn-armor requirement");
        assertTrue(carriedMetalDocument.unsupported().stream().anyMatch(value ->
                value.candidateFamily() == MechanicFamily.ATTACK_ROLL_MODIFIER));

        var conflictingAttackField = input("shocking-grasp").changed(mapper, root ->
                root.withArray("spellAttack").set(0, mapper.getNodeFactory().textNode("R")));
        assertThrows(IllegalArgumentException.class, () -> extract(conflictingAttackField));
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
        assertEquals(129, inspection.documents().stream().filter(value -> value.processing().state() == ProcessingState.PROCESSED).count());
        assertEquals(840, inspection.documents().stream().filter(value -> value.processing().state() == ProcessingState.NOT_ATTEMPTED).count());
        assertEquals(279, inspection.documents().stream().mapToInt(value -> value.interpretations().size()).sum());
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
    void phbAdapterRejectsOtherEditionOwners() throws IOException {
        var input = ShieldExtractionTestInput.actual(spells.findById("spell:xphb:fire-bolt"),
                owners.get("spell:xphb:fire-bolt"));
        assertThrows(IllegalArgumentException.class, () -> extract(input));
    }
}
