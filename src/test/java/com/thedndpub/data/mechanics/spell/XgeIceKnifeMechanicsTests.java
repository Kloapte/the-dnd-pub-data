package com.thedndpub.data.mechanics.spell;

import com.thedndpub.data.mechanics.model.AppliesOnAttackRollOutcome;
import com.thedndpub.data.mechanics.model.AppliesOnSaveOutcome;
import com.thedndpub.data.mechanics.model.AppliesToAreaEffect;
import com.thedndpub.data.mechanics.model.AreaEffectCenter;
import com.thedndpub.data.mechanics.model.AreaEffectFrequency;
import com.thedndpub.data.mechanics.model.AttackActivationTiming;
import com.thedndpub.data.mechanics.model.AttackActorRole;
import com.thedndpub.data.mechanics.model.AttackEffectTrigger;
import com.thedndpub.data.mechanics.model.AttackKind;
import com.thedndpub.data.mechanics.model.AttackRollMechanic;
import com.thedndpub.data.mechanics.model.AttackRollOutcome;
import com.thedndpub.data.mechanics.model.AttackTriggeredAreaEffectMechanic;
import com.thedndpub.data.mechanics.model.Ability;
import com.thedndpub.data.mechanics.model.FixedAbility;
import com.thedndpub.data.mechanics.model.DamageMechanic;
import com.thedndpub.data.mechanics.model.DamageOutcome;
import com.thedndpub.data.mechanics.model.DiceAmount;
import com.thedndpub.data.mechanics.model.FixedDamageType;
import com.thedndpub.data.mechanics.model.MechanicsCompleteness;
import com.thedndpub.data.mechanics.model.MechanicsDocument;
import com.thedndpub.data.mechanics.model.MechanicsCoverage;
import com.thedndpub.data.mechanics.model.MechanicsProcessing;
import com.thedndpub.data.mechanics.model.MechanicsJson;
import com.thedndpub.data.mechanics.model.SaveOutcome;
import com.thedndpub.data.mechanics.model.SavingThrowRequirementMechanic;
import com.thedndpub.data.mechanics.model.StructuredFieldMechanicsEvidence;
import com.thedndpub.data.mechanics.model.SpellSlotDamageScaling;
import com.thedndpub.data.mechanics.model.SpellTargetSubject;
import com.thedndpub.data.mechanics.model.TargetCategory;
import com.thedndpub.data.mechanics.model.TargetScope;
import com.thedndpub.data.mechanics.model.TextMechanicsEvidence;
import com.thedndpub.data.mechanics.model.ProcessingState;
import com.thedndpub.data.mechanics.model.UnknownSaveDifficulty;
import com.thedndpub.data.services.SpellMechanicsInspectionCatalog;
import com.thedndpub.data.mechanics.review.MechanicsReviewStatus;
import com.thedndpub.data.mechanics.model.MechanicsLicensingStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class XgeIceKnifeMechanicsTests {
    private static final String RESOURCE_SHA = "17e7e9fe7ad20c7df094d2a4929558c5d2a331f7e1498c7575181864ec5dc1dc";

    @Autowired
    private SpellMechanicsInspectionCatalog inspection;

    @Test
    void separatesCastAttackPiercingExplosionSaveAndScaledColdDamage() throws Exception {
        MechanicsDocument document = document();
        assertEquals(RESOURCE_SHA, document.sources().getFirst().resourceSha256());
        assertEquals(List.of("mechanic:spell:xge:ice-knife:attack-roll",
                        "mechanic:spell:xge:ice-knife:piercing-damage",
                        "mechanic:spell:xge:ice-knife:explosion",
                        "mechanic:spell:xge:ice-knife:explosion-save",
                        "mechanic:spell:xge:ice-knife:explosion-damage"),
                document.interpretations().stream().map(claim -> claim.id().value()).toList());
        assertEquals(2, document.unsupported().size());
        assertTrue(document.coverage().consideredSourcePaths().contains("/spell/35/duration"));
        assertTrue(document.evidence().stream().filter(StructuredFieldMechanicsEvidence.class::isInstance)
                .map(StructuredFieldMechanicsEvidence.class::cast)
                .anyMatch(field -> field.jsonPointer().equals("/spell/35/duration/0")));
        assertTrue(document.unsupported().stream().anyMatch(item -> item.id().endsWith("attack-range")));
        assertTrue(document.unsupported().stream().anyMatch(item -> item.id().endsWith("save-dc-attribution")));

        var attackClaim = document.interpretations().get(0);
        AttackRollMechanic attack = assertInstanceOf(AttackRollMechanic.class, attackClaim.payload());
        assertEquals(AttackKind.RANGED_SPELL, attack.attackKind());
        assertEquals(AttackActorRole.CASTER, attack.actorRole());
        assertEquals(1, attack.attackCount());
        assertEquals(AttackActivationTiming.CAST_TIME, attack.activationTiming());
        assertFalse(attack.attacksIndependent());
        assertEquals(null, attack.countProgression());
        assertEquals(MechanicsCompleteness.PARTIAL, attackClaim.assessment().completeness());
        assertEquals(TargetScope.SINGLE_TARGET_CREATURE,
                assertInstanceOf(SpellTargetSubject.class, attackClaim.context().subject()).scope());

        var piercingClaim = document.interpretations().get(1);
        DamageMechanic piercing = assertInstanceOf(DamageMechanic.class, piercingClaim.payload());
        assertEquals(new DiceAmount(1, 10, 0), piercing.amount());
        assertEquals(new FixedDamageType("piercing"), piercing.damageType());
        assertEquals(null, piercing.spellSlotScaling());
        AppliesOnAttackRollOutcome hit = assertInstanceOf(AppliesOnAttackRollOutcome.class,
                piercingClaim.context().bindings().getFirst());
        assertEquals(AttackRollOutcome.HIT, hit.outcome());
        assertEquals(attackClaim.revision().reference(attackClaim.id()), hit.attackRoll());

        var explosionClaim = document.interpretations().get(2);
        AttackTriggeredAreaEffectMechanic explosion = assertInstanceOf(
                AttackTriggeredAreaEffectMechanic.class, explosionClaim.payload());
        assertEquals(attackClaim.revision().reference(attackClaim.id()), explosion.attackRoll());
        assertEquals(AttackEffectTrigger.AFTER_ATTACK_REGARDLESS_OF_OUTCOME, explosion.trigger());
        assertEquals(AreaEffectCenter.ATTACK_TARGET, explosion.center());
        assertEquals(5, explosion.radiusFeet());
        assertTrue(explosion.includesAttackTarget());
        assertEquals(List.of(TargetCategory.CREATURE), explosion.affectedTargetCategories());
        assertEquals(AreaEffectFrequency.ONCE_PER_ATTACK, explosion.frequency());
        assertEquals(TargetScope.AREA_AFFECTED_CREATURES,
                assertInstanceOf(SpellTargetSubject.class, explosionClaim.context().subject()).scope());
        assertTrue(explosionClaim.assessment().completeness() == MechanicsCompleteness.COMPLETE_WITHIN_CLAIM);
        assertThrows(UnsupportedOperationException.class, () -> explosion.affectedTargetCategories().clear());

        var saveClaim = document.interpretations().get(3);
        SavingThrowRequirementMechanic save = assertInstanceOf(SavingThrowRequirementMechanic.class,
                saveClaim.payload());
        assertEquals(Ability.DEXTERITY, assertInstanceOf(FixedAbility.class, save.ability()).ability());
        assertInstanceOf(UnknownSaveDifficulty.class, save.difficulty());
        AppliesToAreaEffect saveArea = assertInstanceOf(AppliesToAreaEffect.class,
                saveClaim.context().bindings().getFirst());
        assertEquals(explosionClaim.revision().reference(explosionClaim.id()), saveArea.areaEffect());
        assertEquals(MechanicsCompleteness.PARTIAL, saveClaim.assessment().completeness());

        var coldClaim = document.interpretations().get(4);
        DamageMechanic cold = assertInstanceOf(DamageMechanic.class, coldClaim.payload());
        assertEquals(new DiceAmount(2, 6, 0), cold.amount());
        assertEquals(new FixedDamageType("cold"), cold.damageType());
        assertEquals(new SpellSlotDamageScaling(new DiceAmount(1, 6, 0), 1, 2, 1), cold.spellSlotScaling());
        assertEquals(3, coldClaim.context().bindings().size());
        AppliesToAreaEffect damageArea = assertInstanceOf(AppliesToAreaEffect.class,
                coldClaim.context().bindings().getFirst());
        assertEquals(saveArea.areaEffect(), damageArea.areaEffect());
        List<AppliesOnSaveOutcome> outcomes = coldClaim.context().bindings().stream()
                .filter(AppliesOnSaveOutcome.class::isInstance)
                .map(AppliesOnSaveOutcome.class::cast).toList();
        assertEquals(2, outcomes.size());
        assertTrue(outcomes.stream().anyMatch(value -> value.outcome() == SaveOutcome.FAILURE
                && value.damageOutcome() == DamageOutcome.FULL_DAMAGE
                && value.savingThrow().equals(saveClaim.revision().reference(saveClaim.id()))));
        assertTrue(outcomes.stream().anyMatch(value -> value.outcome() == SaveOutcome.SUCCESS
                && value.damageOutcome() == DamageOutcome.NO_DAMAGE
                && value.savingThrow().equals(saveClaim.revision().reference(saveClaim.id()))));

        int[] expectedColdDice = {2, 3, 4, 5, 6, 7, 8, 9, 10};
        for (int slot = 1; slot <= 9; slot++) {
            assertEquals(expectedColdDice[slot - 1] - 2,
                    cold.spellSlotScaling().incrementsAtSlotLevel(slot));
            assertEquals(new DiceAmount(1, 10, 0), piercing.amount());
        }

        assertEquals(1, document.interpretations().stream().filter(record ->
                record.payload() instanceof AttackTriggeredAreaEffectMechanic).count());
        assertTrue(document.interpretations().stream().allMatch(claim ->
                claim.review().recordedStatus() == MechanicsReviewStatus.PENDING_REVIEW
                        && claim.licensing().status() == MechanicsLicensingStatus.NEEDS_REVIEW));
        assertEquals(0, document.reviewSummary().verified());

        assertTextEvidence(document, "/spell/35/entries/0", 39, 68);
        assertTextEvidence(document, "/spell/35/entries/0", 69, 115);
        assertTextEvidence(document, "/spell/35/entries/0", 116, 124);
        assertTextEvidence(document, "/spell/35/entries/0", 126, 174);
        assertTextEvidence(document, "/spell/35/entries/0", 175, 212);
        assertTextEvidence(document, "/spell/35/entries/0", 213, 261);
        assertTextEvidence(document, "/spell/35/entries/0", 262, 302);
        assertTextEvidence(document, "/spell/35/entries/0", 303, 337);
        assertTextEvidence(document, "/spell/35/entriesHigherLevel/0/entries/0", 0, 154);
        byte[] first = MechanicsJson.mapper().writeValueAsBytes(document);
        byte[] second = MechanicsJson.mapper().writeValueAsBytes(document());
        assertTrue(Arrays.equals(first, second));
        assertEquals(document, MechanicsJson.mapper().readValue(first, MechanicsDocument.class));
        assertEquals(64, coldClaim.revision().contentSha256().length());
        assertFalse(coldClaim.revision().contentSha256().isBlank());
    }

    @Test
    void rejectsMisrepresentedAreaTimingAndGeometry() {
        var attackClaim = document().interpretations().getFirst();
        var reference = attackClaim.revision().reference(attackClaim.id());
        assertThrows(IllegalArgumentException.class, () -> new AttackTriggeredAreaEffectMechanic(reference,
                AttackEffectTrigger.AFTER_ATTACK_REGARDLESS_OF_OUTCOME, AreaEffectCenter.ATTACK_TARGET,
                0, true, List.of(TargetCategory.CREATURE), AreaEffectFrequency.ONCE_PER_ATTACK));
        assertThrows(IllegalArgumentException.class, () -> new AttackTriggeredAreaEffectMechanic(reference,
                AttackEffectTrigger.AFTER_ATTACK_REGARDLESS_OF_OUTCOME, AreaEffectCenter.ATTACK_TARGET,
                5, false, List.of(TargetCategory.CREATURE), AreaEffectFrequency.ONCE_PER_ATTACK));
        var categories = new java.util.ArrayList<>(List.of(TargetCategory.CREATURE));
        var effect = new AttackTriggeredAreaEffectMechanic(reference,
                AttackEffectTrigger.AFTER_ATTACK_REGARDLESS_OF_OUTCOME, AreaEffectCenter.ATTACK_TARGET,
                5, true, categories, AreaEffectFrequency.ONCE_PER_ATTACK);
        categories.clear();
        assertEquals(List.of(TargetCategory.CREATURE), effect.affectedTargetCategories());

        MechanicsDocument valid = document();
        var areaClaim = valid.interpretations().get(2);
        var cyclicArea = new AttackTriggeredAreaEffectMechanic(
                areaClaim.revision().reference(areaClaim.id()),
                AttackEffectTrigger.AFTER_ATTACK_REGARDLESS_OF_OUTCOME,
                AreaEffectCenter.ATTACK_TARGET, 5, true, List.of(TargetCategory.CREATURE),
                AreaEffectFrequency.ONCE_PER_ATTACK);
        var invalidRecord = com.thedndpub.data.mechanics.model.MechanicRecord.propose(areaClaim.id(), cyclicArea,
                areaClaim.context(), areaClaim.evidenceAssociations(), areaClaim.derivation(), areaClaim.assessment());
        var records = new java.util.ArrayList<>(valid.interpretations());
        records.set(2, invalidRecord);
        assertThrows(IllegalArgumentException.class, () -> MechanicsDocument.create(valid.owner(), 1,
                valid.sources(), new MechanicsProcessing(ProcessingState.PROCESSED, "xge-ice-knife", "1"),
                records, valid.observations(), valid.unsupported(), valid.evidence(), valid.coverage()));
    }

    @Test
    @EnabledIfSystemProperty(named = "mechanics.compatibility.capture", matches = "true")
    void captureCanonicalIceKnifeDocument() throws Exception {
        Path directory = Path.of(System.getProperty("mechanics.compatibility.directory"));
        Files.createDirectories(directory);
        MechanicsJson.mapper().writeValue(directory.resolve("ice-knife.json").toFile(), document());
    }

    private void assertTextEvidence(MechanicsDocument document, String pointer, int start, int end) {
        assertTrue(document.evidence().stream().filter(TextMechanicsEvidence.class::isInstance)
                .map(TextMechanicsEvidence.class::cast).anyMatch(text ->
                        text.syntax().location().jsonPointer().equals(pointer)
                                && text.syntax().span().startInclusive() == start
                                && text.syntax().span().endExclusive() == end));
    }

    private MechanicsDocument document() {
        return inspection.documents().stream().filter(candidate -> candidate.owner().id().equals(
                "spell:xge:ice-knife")).findFirst().orElseThrow();
    }
}
