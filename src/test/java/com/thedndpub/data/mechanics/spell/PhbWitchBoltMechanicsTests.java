package com.thedndpub.data.mechanics.spell;

import com.thedndpub.data.mechanics.model.ActionActivatedDamage;
import com.thedndpub.data.mechanics.model.AppliesOnAttackRollOutcome;
import com.thedndpub.data.mechanics.model.AttackActivationTiming;
import com.thedndpub.data.mechanics.model.AttackActorRole;
import com.thedndpub.data.mechanics.model.AttackKind;
import com.thedndpub.data.mechanics.model.AttackRollMechanic;
import com.thedndpub.data.mechanics.model.AttackRollOutcome;
import com.thedndpub.data.mechanics.model.DamageMechanic;
import com.thedndpub.data.mechanics.model.DeclaredSpellDurationReference;
import com.thedndpub.data.mechanics.model.DiceAmount;
import com.thedndpub.data.mechanics.model.FixedDamageType;
import com.thedndpub.data.mechanics.model.HitGatedEffectEstablishment;
import com.thedndpub.data.mechanics.model.MechanicsCompleteness;
import com.thedndpub.data.mechanics.model.MechanicsDocument;
import com.thedndpub.data.mechanics.model.MechanicsJson;
import com.thedndpub.data.mechanics.model.PersistentEffectTerminationCondition;
import com.thedndpub.data.mechanics.model.PersistentSpellEffectMechanic;
import com.thedndpub.data.mechanics.model.SpellSlotDamageScaling;
import com.thedndpub.data.mechanics.model.StructuredFieldMechanicsEvidence;
import com.thedndpub.data.mechanics.model.TextMechanicsEvidence;
import com.thedndpub.data.mechanics.model.SpellTargetSubject;
import com.thedndpub.data.mechanics.model.TargetScope;
import com.thedndpub.data.services.SpellMechanicsInspectionCatalog;
import com.thedndpub.data.services.SpellCatalog;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class PhbWitchBoltMechanicsTests {
    @Autowired
    private SpellMechanicsInspectionCatalog inspection;
    @Autowired
    private SpellCatalog spells;

    @Test
    void modelsHitGatedLinkInitialDamageAndActionActivatedSubsequentDamage() throws Exception {
        MechanicsDocument document = document();
        assertEquals("c9e54cce55e7ce0fc988feb1b604bf505faa5c042635542fa6b6ed36724e297a",
                document.sources().getFirst().resourceSha256());
        assertEquals(List.of("mechanic:spell:phb:witch-bolt:attack-roll",
                        "mechanic:spell:phb:witch-bolt:persistent-link",
                        "mechanic:spell:phb:witch-bolt:initial-damage",
                        "mechanic:spell:phb:witch-bolt:subsequent-damage"),
                document.interpretations().stream().map(record -> record.id().value()).toList());
        assertEquals(1, document.unsupported().size());
        assertEquals("witch-bolt-unsupported:initial-attack-range", document.unsupported().getFirst().id());
        assertEquals("PARTIAL", document.coverage().status().name());
        assertEquals(MechanicsCompleteness.PARTIAL,
                document.interpretations().getFirst().assessment().completeness());
        assertTrue(document.interpretations().stream().allMatch(claim -> claim.revision().number() == 1
                && claim.review().recordedStatus().name().equals("PENDING_REVIEW")
                && claim.licensing().status().name().equals("NEEDS_REVIEW")));
        assertEquals("PENDING_REVIEW", document.interpretations().get(0).review().effectiveStatus().name());
        assertEquals("PENDING_REVIEW", document.interpretations().get(1).review().effectiveStatus().name());
        assertEquals("PENDING_REVIEW", document.interpretations().get(2).review().effectiveStatus().name());
        assertEquals("PENDING_REVIEW", document.interpretations().get(3).review().effectiveStatus().name());

        var attackRecord = document.interpretations().getFirst();
        AttackRollMechanic attack = assertInstanceOf(AttackRollMechanic.class, attackRecord.payload());
        assertEquals(AttackKind.RANGED_SPELL, attack.attackKind());
        assertEquals(AttackActorRole.CASTER, attack.actorRole());
        assertEquals(1, attack.attackCount());
        assertEquals(AttackActivationTiming.CAST_TIME, attack.activationTiming());
        assertNull(attack.countProgression());
        assertNull(attack.activationPattern());
        assertEquals(TargetScope.SINGLE_TARGET_CREATURE,
                assertInstanceOf(SpellTargetSubject.class, attackRecord.context().subject()).scope());

        var effectRecord = document.interpretations().get(1);
        PersistentSpellEffectMechanic effect = assertInstanceOf(
                PersistentSpellEffectMechanic.class, effectRecord.payload());
        assertInstanceOf(DeclaredSpellDurationReference.class, effect.duration());
        assertTrue(effect.concentrationRequired());
        assertFalse(effect.independentlyActingCreature());
        assertTrue(effect.requiresActivation());
        assertNull(effect.endsWhenSpellIsCastAgain());
        assertEquals(attackRecord.revision().reference(attackRecord.id()), effect.establishment().attackRoll());
        assertEquals(AttackRollOutcome.HIT, effect.establishment().outcome());
        assertEquals(List.of(PersistentEffectTerminationCondition.CONCENTRATION_ENDS,
                        PersistentEffectTerminationCondition.DURATION_EXPIRES,
                        PersistentEffectTerminationCondition.TARGET_OUTSIDE_SPELL_RANGE_FROM_CASTER,
                        PersistentEffectTerminationCondition.TARGET_HAS_TOTAL_COVER_FROM_CASTER,
                        PersistentEffectTerminationCondition.CASTER_USES_SUBSEQUENT_ACTION_FOR_ANOTHER_PURPOSE),
                effect.terminationConditions());
        assertEquals(1, spells.findById("spell:phb:witch-bolt").castingTimes().getFirst().number());
        assertEquals("action", spells.findById("spell:phb:witch-bolt").castingTimes().getFirst().unit());
        assertEquals(30, spells.findById("spell:phb:witch-bolt").range().distance().amount());
        assertEquals(1, spells.findById("spell:phb:witch-bolt").durations().getFirst().duration().amount());
        assertEquals("minute", spells.findById("spell:phb:witch-bolt").durations().getFirst().duration().type());
        assertEquals(Boolean.TRUE, spells.findById("spell:phb:witch-bolt").durations().getFirst().concentration());

        var initialRecord = document.interpretations().get(2);
        DamageMechanic initial = assertInstanceOf(DamageMechanic.class, initialRecord.payload());
        assertEquals(new DiceAmount(1, 12, 0), initial.amount());
        assertEquals(new FixedDamageType("lightning"), initial.damageType());
        assertNull(initial.actionActivation());
        AppliesOnAttackRollOutcome hit = assertInstanceOf(AppliesOnAttackRollOutcome.class,
                initialRecord.context().bindings().getFirst());
        assertEquals(AttackRollOutcome.HIT, hit.outcome());
        assertEquals(attackRecord.revision().reference(attackRecord.id()), hit.attackRoll());
        SpellSlotDamageScaling scaling = initial.spellSlotScaling();
        assertEquals(new SpellSlotDamageScaling(new DiceAmount(1, 12, 0), 1, 2, 1), scaling);
        for (int slot = 1; slot <= 9; slot++) {
            assertEquals(Math.max(slot - 1, 0), scaling.incrementsAtSlotLevel(Math.max(slot, 1)));
            assertEquals(new DiceAmount(slot, 12, 0),
                    new DiceAmount(1 + scaling.incrementsAtSlotLevel(slot), 12, 0));
        }

        DamageMechanic later = assertInstanceOf(DamageMechanic.class,
                document.interpretations().get(3).payload());
        assertEquals(new DiceAmount(1, 12, 0), later.amount());
        assertEquals(new FixedDamageType("lightning"), later.damageType());
        assertNull(later.spellSlotScaling());
        assertNull(later.timing());
        assertTrue(document.interpretations().get(3).context().bindings().isEmpty(),
                "Subsequent automatic resolution is not tied to another attack or save");
        ActionActivatedDamage activation = later.actionActivation();
        assertEquals(effectRecord.revision().reference(effectRecord.id()), activation.persistentEffect());
        assertTrue(activation.optional());
        assertEquals("SPELL_CASTER", activation.actor().name());
        assertEquals("ACTION_ON_SUBSEQUENT_TURN", activation.timing().name());
        assertEquals("ESTABLISHED_LINK_TARGET", activation.target().name());
        assertEquals("AUTOMATIC_WITHOUT_ATTACK_OR_SAVE", activation.resolution().name());
        assertEquals(document.interpretations().get(2).context().subject(),
                document.interpretations().get(3).context().subject());
        assertNotEquals(attackRecord.id(), effectRecord.id());

        assertTrue(document.evidence().stream().filter(StructuredFieldMechanicsEvidence.class::isInstance)
                .map(StructuredFieldMechanicsEvidence.class::cast)
                .map(StructuredFieldMechanicsEvidence::jsonPointer)
                .collect(java.util.stream.Collectors.toSet()).containsAll(List.of(
                        "/spell/357/time/0", "/spell/357/range", "/spell/357/duration/0",
                        "/spell/357/spellAttack/0", "/spell/357/damageInflict/0")));
        assertTrue(document.evidence().stream().filter(TextMechanicsEvidence.class::isInstance)
                .map(TextMechanicsEvidence.class::cast)
                .allMatch(text -> text.syntax().location().jsonPointer().startsWith("/spell/357/")));
        assertTextEvidence(document, "/spell/357/entries/0", 0, 140);
        assertTextEvidence(document, "/spell/357/entries/0", 141, 190);
        assertTextEvidence(document, "/spell/357/entries/0", 191, 199);
        assertTextEvidence(document, "/spell/357/entries/0", 201, 249);
        assertTextEvidence(document, "/spell/357/entries/0", 255, 387);
        assertTextEvidence(document, "/spell/357/entries/0", 388, 446);
        assertTextEvidence(document, "/spell/357/entries/0", 447, 514);
        assertTextEvidence(document, "/spell/357/entries/0", 518, 571);
        assertTextEvidence(document, "/spell/357/entriesHigherLevel/0/entries/0", 0, 159);
        assertEquals(document, MechanicsJson.mapper().readValue(
                MechanicsJson.mapper().writeValueAsBytes(document), MechanicsDocument.class));
        assertEquals(document.revision().contentSha256(), document().revision().contentSha256());
    }

    @Test
    void rejectsInvalidHitGatingAndCopiesTerminationLists() {
        var reference = document().interpretations().getFirst().revision()
                .reference(document().interpretations().getFirst().id());
        assertThrows(IllegalArgumentException.class,
                () -> new HitGatedEffectEstablishment(reference, AttackRollOutcome.MISS));
        assertThrows(NullPointerException.class, () -> new ActionActivatedDamage(
                null, null, null, null, null, true));
        var validActivation = ActionActivatedDamage.witchBolt(reference);
        assertThrows(IllegalArgumentException.class, () -> new ActionActivatedDamage(
                validActivation.persistentEffect(), validActivation.actor(), validActivation.timing(),
                validActivation.target(), validActivation.resolution(), false));
        var conditions = new ArrayList<>(List.of(PersistentEffectTerminationCondition.CONCENTRATION_ENDS));
        var effect = new PersistentSpellEffectMechanic(
                new DeclaredSpellDurationReference(spells.findById("spell:phb:witch-bolt").id(), 0,
                        document().sources().getFirst()), true, false, true, false,
                new HitGatedEffectEstablishment(reference), conditions);
        conditions.clear();
        assertEquals(List.of(PersistentEffectTerminationCondition.CONCENTRATION_ENDS),
                effect.terminationConditions());
        assertThrows(IllegalArgumentException.class, () -> new PersistentSpellEffectMechanic(
                effect.duration(), true, false, true, false, effect.establishment(),
                List.of(PersistentEffectTerminationCondition.CONCENTRATION_ENDS,
                        PersistentEffectTerminationCondition.CONCENTRATION_ENDS)));
    }

    @Test
    @EnabledIfSystemProperty(named = "mechanics.compatibility.capture", matches = "true")
    void captureCanonicalWitchBoltDocument() throws Exception {
        Path directory = Path.of(System.getProperty("mechanics.compatibility.directory"));
        Files.createDirectories(directory);
        MechanicsJson.mapper().writeValue(directory.resolve("witch-bolt.json").toFile(), document());
    }

    private MechanicsDocument document() {
        return inspection.documents().stream()
                .filter(candidate -> candidate.owner().id().equals("spell:phb:witch-bolt"))
                .findFirst().orElseThrow();
    }

    private void assertTextEvidence(MechanicsDocument document, String pointer, int start, int end) {
        assertTrue(document.evidence().stream().filter(TextMechanicsEvidence.class::isInstance)
                .map(TextMechanicsEvidence.class::cast).anyMatch(text ->
                        text.syntax().location().jsonPointer().equals(pointer)
                                && text.syntax().span().startInclusive() == start
                                && text.syntax().span().endExclusive() == end));
    }
}
