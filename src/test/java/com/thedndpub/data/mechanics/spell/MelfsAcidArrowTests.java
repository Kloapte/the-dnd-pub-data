package com.thedndpub.data.mechanics.spell;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dte.common.entry.TextEntryDte;
import com.thedndpub.data.dte.common.entry.NamedEntryDte;
import com.thedndpub.data.mechanics.model.AppliesOnAttackRollOutcome;
import com.thedndpub.data.mechanics.model.AttackActivationTiming;
import com.thedndpub.data.mechanics.model.AttackActorRole;
import com.thedndpub.data.mechanics.model.AttackKind;
import com.thedndpub.data.mechanics.model.AttackRollMechanic;
import com.thedndpub.data.mechanics.model.AttackRollOutcome;
import com.thedndpub.data.mechanics.model.DamageMechanic;
import com.thedndpub.data.mechanics.model.DamageTiming;
import com.thedndpub.data.mechanics.model.DiceAmount;
import com.thedndpub.data.mechanics.model.CoverageStatus;
import com.thedndpub.data.mechanics.model.FixedDamageType;
import com.thedndpub.data.mechanics.model.FractionOfAmount;
import com.thedndpub.data.mechanics.model.MechanicsCompleteness;
import com.thedndpub.data.mechanics.model.MechanicsDocument;
import com.thedndpub.data.mechanics.model.MechanicsJson;
import com.thedndpub.data.mechanics.model.ProcessingState;
import com.thedndpub.data.mechanics.model.SpellSlotDamageScaling;
import com.thedndpub.data.mechanics.model.SpellTargetSubject;
import com.thedndpub.data.mechanics.model.TargetScope;
import com.thedndpub.data.mechanics.model.TextMechanicsEvidence;
import com.thedndpub.data.services.SpellCatalog;
import com.thedndpub.data.services.SpellMechanicsInspectionCatalog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class MelfsAcidArrowTests {
    @Autowired
    private SpellCatalog spells;
    @Autowired
    private SpellMechanicsInspectionCatalog inspection;
    @Autowired
    private ObjectMapper mapper;

    @Test
    void extractsExclusiveHitAndMissBranchesWithDelayedHitDamage() throws Exception {
        MechanicsDocument document = extract();

        assertEquals("c9e54cce55e7ce0fc988feb1b604bf505faa5c042635542fa6b6ed36724e297a",
                document.sources().getFirst().resourceSha256());
        assertEquals(ProcessingState.PROCESSED, document.processing().state());
        assertEquals(CoverageStatus.PARTIAL, document.coverage().status());
        assertEquals(0, document.coverage().unsupportedCount());
        assertEquals(List.of("mechanic:spell:phb:melf-s-acid-arrow:attack-roll",
                        "mechanic:spell:phb:melf-s-acid-arrow:damage-on-hit",
                        "mechanic:spell:phb:melf-s-acid-arrow:delayed-damage-on-hit",
                        "mechanic:spell:phb:melf-s-acid-arrow:damage-on-miss"),
                document.interpretations().stream().map(record -> record.id().value()).toList());
        assertTrue(document.interpretations().stream().allMatch(record -> record.revision().number() == 1));
        assertTrue(document.interpretations().stream().allMatch(record ->
                record.assessment().completeness() == MechanicsCompleteness.COMPLETE_WITHIN_CLAIM));
        assertTrue(document.interpretations().stream().allMatch(record ->
                record.review().recordedStatus()
                        == com.thedndpub.data.mechanics.review.MechanicsReviewStatus.PENDING_REVIEW
                        && record.licensing().status()
                        == com.thedndpub.data.mechanics.model.MechanicsLicensingStatus.NEEDS_REVIEW));

        var attackRecord = document.interpretations().getFirst();
        AttackRollMechanic attack = assertInstanceOf(AttackRollMechanic.class, attackRecord.payload());
        assertEquals(AttackKind.RANGED_SPELL, attack.attackKind());
        assertEquals(AttackActorRole.CASTER, attack.actorRole());
        assertEquals(1, attack.attackCount());
        assertTrue(attack.attacksIndependent());
        assertEquals(AttackActivationTiming.CAST_TIME, attack.activationTiming());
        assertEquals(TargetScope.SINGLE_TARGET_UNSPECIFIED_CATEGORY,
                assertInstanceOf(SpellTargetSubject.class, attackRecord.context().subject()).scope());
        assertEquals(0, document.unsupported().size());

        var hitRecord = document.interpretations().get(1);
        DamageMechanic hit = assertInstanceOf(DamageMechanic.class, hitRecord.payload());
        assertEquals(new DiceAmount(4, 4, 0), hit.amount());
        assertEquals(new FixedDamageType("acid"), hit.damageType());
        assertEquals(DamageTiming.IMMEDIATE, hit.timing());
        assertEquals(new SpellSlotDamageScaling(new DiceAmount(1, 4, 0), 2, 3, 1), hit.spellSlotScaling());
        assertOutcome(hitRecord, attackRecord, AttackRollOutcome.HIT);

        var delayedRecord = document.interpretations().get(2);
        DamageMechanic delayed = assertInstanceOf(DamageMechanic.class, delayedRecord.payload());
        assertEquals(new DiceAmount(2, 4, 0), delayed.amount());
        assertEquals(new FixedDamageType("acid"), delayed.damageType());
        assertEquals(DamageTiming.END_OF_TARGET_NEXT_TURN, delayed.timing());
        assertEquals(hit.spellSlotScaling(), delayed.spellSlotScaling());
        assertOutcome(delayedRecord, attackRecord, AttackRollOutcome.HIT);
        assertFalse(delayedRecord.context().bindings().stream().anyMatch(binding ->
                binding instanceof AppliesOnAttackRollOutcome outcome
                        && outcome.outcome() == AttackRollOutcome.MISS));

        var missRecord = document.interpretations().get(3);
        DamageMechanic miss = assertInstanceOf(DamageMechanic.class, missRecord.payload());
        FractionOfAmount half = assertInstanceOf(FractionOfAmount.class, miss.amount());
        assertEquals(1, half.numerator());
        assertEquals(2, half.denominator());
        assertEquals(hitRecord.revision().reference(hitRecord.id()), half.baseAmount());
        assertEquals(DamageTiming.IMMEDIATE, miss.timing());
        assertEquals(hit.damageType(), miss.damageType());
        assertOutcome(missRecord, attackRecord, AttackRollOutcome.MISS);
        assertTrue(missRecord.evidenceAssociations().stream().anyMatch(association ->
                association.role() == com.thedndpub.data.mechanics.model.EvidenceRole.COUNTEREVIDENCE));

        assertEvidence(document, "Make a ranged spell attack against the target.", 93, 139);
        assertEvidence(document, "On a hit", 140, 148);
        assertEvidence(document, "{@damage 4d4}", 167, 180);
        assertEvidence(document, "{@damage 2d4}", 209, 222);
        assertEvidence(document, "and {@damage 2d4} acid damage at the end of its next turn", 205, 262);
        assertEvidence(document, "On a miss", 264, 273);
        assertEvidence(document, "no damage at the end of its next turn", 358, 395);
        assertEvidence(document, "the damage (both initial and later) increases by "
                        + "{@scaledamage 4d4;2d4|2-9|1d4} for each slot level above 2nd",
                "/spell/223/entriesHigherLevel/0/entries/0", 68, 177);
        byte[] first = MechanicsJson.mapper().writeValueAsBytes(document);
        byte[] second = MechanicsJson.mapper().writeValueAsBytes(document);
        assertTrue(java.util.Arrays.equals(first, second));
        assertEquals(document, MechanicsJson.mapper().readValue(first, MechanicsDocument.class));
        assertArrayEquals(first, MechanicsJson.mapper().writeValueAsBytes(extract()));
        assertNotNull(inspection.findById(spells.findById("spell:phb:melf-s-acid-arrow").id()));
    }

    @Test
    void validatesFractionAndSlotDamageScalingBounds() {
        var id = new com.thedndpub.data.mechanics.model.MechanicId(
                "mechanic:spell:phb:melf-s-acid-arrow:damage-on-hit");
        var reference = new com.thedndpub.data.mechanics.model.MechanicRevisionReference(
                id, 1, "0".repeat(64));
        assertThrows(IllegalArgumentException.class, () -> new FractionOfAmount(1, 0, reference));
        assertThrows(IllegalArgumentException.class, () -> new FractionOfAmount(0, 2, reference));
        assertThrows(IllegalArgumentException.class, () -> new FractionOfAmount(3, 2, reference));
        assertThrows(IllegalArgumentException.class,
                () -> new SpellSlotDamageScaling(new DiceAmount(1, 4, 0), 2, 2, 1));
        assertThrows(IllegalArgumentException.class,
                () -> new SpellSlotDamageScaling(new DiceAmount(1, 4, 0), 2, 10, 1));
        var scaling = new SpellSlotDamageScaling(new DiceAmount(1, 4, 0), 2, 3, 1);
        assertEquals(0, scaling.incrementsAtSlotLevel(2));
        for (int slotLevel = 2; slotLevel <= 9; slotLevel++) {
            assertEquals(slotLevel - 2, scaling.incrementsAtSlotLevel(slotLevel));
        }
        assertThrows(IllegalArgumentException.class, () -> scaling.incrementsAtSlotLevel(1));
        assertThrows(IllegalArgumentException.class, () -> scaling.incrementsAtSlotLevel(10));
        assertThrows(IllegalArgumentException.class, () -> new DamageMechanic(
                new DiceAmount(Integer.MAX_VALUE, 4, 0), new FixedDamageType("acid"), null,
                scaling, DamageTiming.IMMEDIATE));
    }

    private MechanicsDocument extract() throws IOException {
        var factory = new SpellMechanicsDocumentFactory(mapper);
        Map<String, SpellMechanicsSourceOwner> owners = factory.loadOwners().stream()
                .collect(Collectors.toMap(owner -> owner.id().value(), Function.identity()));
        var spell = spells.findById("spell:phb:melf-s-acid-arrow");
        var input = ShieldExtractionTestInput.actual(spell, owners.get(spell.id().value()));
        return new PhbSpellMechanicsExtractor().extract(spell,
                new SpellMechanicsEvidenceSource(spell, input.owner(), input.bytes(), mapper), input.seed());
    }

    private void assertOutcome(
            com.thedndpub.data.mechanics.model.MechanicRecord record,
            com.thedndpub.data.mechanics.model.MechanicRecord attack,
            AttackRollOutcome expected) {
        var outcome = assertInstanceOf(AppliesOnAttackRollOutcome.class, record.context().bindings().getFirst());
        assertEquals(attack.revision().reference(attack.id()), outcome.attackRoll());
        assertEquals(expected, outcome.outcome());
    }

    private void assertEvidence(MechanicsDocument document, String phrase, int start, int end) {
        assertEvidence(document, phrase, "/spell/223/entries/0", start, end);
    }

    private void assertEvidence(
            MechanicsDocument document, String phrase, String pointer, int start, int end) {
        TextMechanicsEvidence evidence = document.evidence().stream()
                .filter(TextMechanicsEvidence.class::isInstance)
                .map(TextMechanicsEvidence.class::cast)
                .filter(item -> item.syntax().span().startInclusive() == start
                        && item.syntax().span().endExclusive() == end)
                .findFirst().orElseThrow();
        assertEquals(pointer, evidence.syntax().location().jsonPointer());
        String authored = pointer.equals("/spell/223/entries/0")
                ? ((TextEntryDte) spells.findById("spell:phb:melf-s-acid-arrow").entries().getFirst()).text()
                : ((TextEntryDte) ((NamedEntryDte) spells.findById("spell:phb:melf-s-acid-arrow")
                        .entriesHigherLevel().getFirst()).entries().getFirst()).text();
        assertEquals(phrase, authored.substring(start, end));
    }
}
