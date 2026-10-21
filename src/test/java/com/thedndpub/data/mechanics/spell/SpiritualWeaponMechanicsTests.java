package com.thedndpub.data.mechanics.spell;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.thedndpub.data.mechanics.model.AppliesOnAttackRollOutcome;
import com.thedndpub.data.mechanics.model.AmountSymbol;
import com.thedndpub.data.mechanics.model.AttackActivationPattern;
import com.thedndpub.data.mechanics.model.AttackActivationTiming;
import com.thedndpub.data.mechanics.model.AttackActorRole;
import com.thedndpub.data.mechanics.model.AttackKind;
import com.thedndpub.data.mechanics.model.AttackRollMechanic;
import com.thedndpub.data.mechanics.model.AttackRollOutcome;
import com.thedndpub.data.mechanics.model.DamageMechanic;
import com.thedndpub.data.mechanics.model.DiceAmount;
import com.thedndpub.data.mechanics.model.DeclaredSpellDurationReference;
import com.thedndpub.data.mechanics.model.FixedDamageType;
import com.thedndpub.data.mechanics.model.MechanicsDocument;
import com.thedndpub.data.mechanics.model.MechanicsCompleteness;
import com.thedndpub.data.mechanics.model.MechanicsJson;
import com.thedndpub.data.mechanics.model.PersistentSpellEffectMechanic;
import com.thedndpub.data.mechanics.model.SpellSlotDamageScaling;
import com.thedndpub.data.mechanics.model.SumAmount;
import com.thedndpub.data.mechanics.model.SymbolicAmount;
import com.thedndpub.data.services.SpellMechanicsInspectionCatalog;
import com.thedndpub.data.services.SpellCatalog;
import com.thedndpub.data.services.SpellsService;
import com.thedndpub.data.mechanics.inspection.SpellMechanicsInspectionMapper;
import com.thedndpub.data.mechanics.model.MechanicsHashes;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class SpiritualWeaponMechanicsTests {
    @Autowired
    private SpellMechanicsInspectionCatalog inspection;
    @Autowired
    private SpellCatalog spells;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private SpellsService service;

    @Test
    void representsPersistentEffectAndExplicitRepeatedAttackOpportunities() throws Exception {
        MechanicsDocument document = document();
        assertEquals("c9e54cce55e7ce0fc988feb1b604bf505faa5c042635542fa6b6ed36724e297a",
                document.sources().getFirst().resourceSha256());
        assertEquals(List.of("mechanic:spell:phb:spiritual-weapon:persistent-effect",
                        "mechanic:spell:phb:spiritual-weapon:attack-roll",
                        "mechanic:spell:phb:spiritual-weapon:damage"),
                document.interpretations().stream().map(record -> record.id().value()).toList());
        assertEquals(2, document.unsupported().size());
        assertEquals("PARTIAL", document.coverage().status().name());
        assertTrue(document.interpretations().stream().allMatch(record -> record.revision().number() == 1
                && record.review().recordedStatus().name().equals("PENDING_REVIEW")
                && record.review().effectiveStatus().name().equals("PENDING_REVIEW")
                && record.licensing().status().name().equals("NEEDS_REVIEW")));

        var effectRecord = document.interpretations().getFirst();
        PersistentSpellEffectMechanic effect = assertInstanceOf(
                PersistentSpellEffectMechanic.class, effectRecord.payload());
        assertFalse(effect.concentrationRequired());
        assertFalse(effect.independentlyActingCreature());
        assertTrue(effect.requiresActivation());
        assertTrue(effect.endsWhenSpellIsCastAgain());
        assertEquals(0, assertInstanceOf(DeclaredSpellDurationReference.class, effect.duration()).durationIndex());
        var spell = spells.findById("spell:phb:spiritual-weapon");
        assertEquals(1, spell.durations().size());
        assertEquals("timed", spell.durations().getFirst().type());
        assertEquals("minute", spell.durations().getFirst().duration().type());
        assertEquals(1, spell.durations().getFirst().duration().amount());
        assertNull(spell.durations().getFirst().concentration());
        assertEquals(1, spell.castingTimes().getFirst().number());
        assertEquals("bonus", spell.castingTimes().getFirst().unit());

        var attackRecord = document.interpretations().get(1);
        AttackRollMechanic attack = assertInstanceOf(AttackRollMechanic.class, attackRecord.payload());
        assertEquals(AttackKind.MELEE_SPELL, attack.attackKind());
        assertEquals(AttackActorRole.SPELL_CREATED_EFFECT, attack.actorRole());
        assertEquals(MechanicsCompleteness.PARTIAL, attackRecord.assessment().completeness());
        assertEquals(1, attack.attackCount());
        assertEquals(null, attack.countProgression());
        assertEquals(null, attack.selectedTargetCount());
        AttackActivationPattern activations = attack.activationPattern();
        assertNotNull(activations);
        assertEquals(AttackActivationTiming.CAST_TIME, activations.initialActivation());
        assertEquals(AttackActivationTiming.BONUS_ACTION, activations.repeatedActivation());
        assertEquals(1, activations.attacksPerActivation());
        assertTrue(activations.repeatsOnlyWhileEffectPersists());
        assertEquals(effectRecord.revision().reference(effectRecord.id()), activations.persistentEffect());

        var damageRecord = document.interpretations().get(2);
        DamageMechanic damage = assertInstanceOf(DamageMechanic.class, damageRecord.payload());
        assertEquals(new SumAmount(List.of(new DiceAmount(1, 8, 0),
                new SymbolicAmount(AmountSymbol.CASTER_SPELLCASTING_ABILITY_MODIFIER))), damage.amount());
        assertEquals(new FixedDamageType("force"), damage.damageType());
        assertEquals(new SpellSlotDamageScaling(new DiceAmount(1, 8, 0), 2, 3, 2), damage.spellSlotScaling());
        for (int slot = 2; slot <= 9; slot++) {
            assertEquals(slot < 3 ? 0 : 1 + (slot - 3) / 2,
                    damage.spellSlotScaling().incrementsAtSlotLevel(slot));
        }
        var hit = assertInstanceOf(AppliesOnAttackRollOutcome.class,
                damageRecord.context().bindings().getFirst());
        assertEquals(AttackRollOutcome.HIT, hit.outcome());
        assertEquals(attackRecord.revision().reference(attackRecord.id()), hit.attackRoll());
        assertFalse(damageRecord.context().bindings().stream().anyMatch(binding ->
                binding instanceof AppliesOnAttackRollOutcome outcome
                        && outcome.outcome() == AttackRollOutcome.MISS));

        assertTextEvidence(document, "/spell/311/entries/0", 0, 115);
        assertTextEvidence(document, "/spell/311/entries/0", 116, 222);
        assertTextEvidence(document, "/spell/311/entries/0", 223, 231);
        assertTextEvidence(document, "/spell/311/entries/0", 233, 323);
        assertTextEvidence(document, "/spell/311/entries/1", 0, 131);
        assertTextEvidence(document, "/spell/311/entriesHigherLevel/0/entries/0", 0, 159);
        assertTrue(document.evidence().stream()
                .filter(com.thedndpub.data.mechanics.model.StructuredFieldMechanicsEvidence.class::isInstance)
                .map(com.thedndpub.data.mechanics.model.StructuredFieldMechanicsEvidence.class::cast)
                .map(com.thedndpub.data.mechanics.model.StructuredFieldMechanicsEvidence::jsonPointer)
                .collect(java.util.stream.Collectors.toSet()).containsAll(List.of(
                        "/spell/311/duration/0", "/spell/311/time/0", "/spell/311/spellAttack/0",
                        "/spell/311/damageInflict/0")));
        assertEquals(2, document.unsupported().size(), "Spatial restrictions must remain explicit diagnostics");

        byte[] first = MechanicsJson.mapper().writeValueAsBytes(document);
        byte[] second = MechanicsJson.mapper().writeValueAsBytes(document());
        assertTrue(java.util.Arrays.equals(first, second));
        assertEquals(document, MechanicsJson.mapper().readValue(first, MechanicsDocument.class));
    }

    @Test
    void rejectsInvalidRepeatedActivationPatternsAndPreservesExpressionInputs() {
        var effect = new com.thedndpub.data.mechanics.model.MechanicRevisionReference(
                new com.thedndpub.data.mechanics.model.MechanicId(
                        "mechanic:spell:phb:spiritual-weapon:persistent-effect"), 1, "0".repeat(64));
        assertThrows(IllegalArgumentException.class, () -> new AttackActivationPattern(
                AttackActivationTiming.CAST_TIME, AttackActivationTiming.CAST_TIME,
                true, 1, effect));
        assertThrows(IllegalArgumentException.class, () -> new AttackActivationPattern(
                AttackActivationTiming.CAST_TIME, AttackActivationTiming.BONUS_ACTION,
                true, 2, effect));
        assertThrows(IllegalArgumentException.class, () -> new AttackActivationPattern(
                AttackActivationTiming.CAST_TIME, AttackActivationTiming.BONUS_ACTION,
                false, 1, effect));
        var terms = new java.util.ArrayList<com.thedndpub.data.mechanics.model.AmountExpression>();
        terms.add(new DiceAmount(1, 8, 0));
        terms.add(new SymbolicAmount(AmountSymbol.CASTER_SPELLCASTING_ABILITY_MODIFIER));
        var amount = new SumAmount(terms);
        terms.clear();
        assertEquals(2, amount.terms().size());
    }

    @Test
    @EnabledIfSystemProperty(named = "mechanics.compatibility.capture", matches = "true")
    void captureCanonicalPrivateAndDefaultRestHashes() throws Exception {
        var canonical = new TreeMap<String, String>();
        var privateInspection = new TreeMap<String, String>();
        var defaultRest = new TreeMap<String, String>();
        var projection = new SpellMechanicsInspectionMapper();
        for (var document : inspection.documents()) {
            String id = document.owner().id();
            canonical.put(id, MechanicsHashes.sha256(MechanicsJson.mapper().writeValueAsBytes(document)));
            privateInspection.put(id, MechanicsHashes.sha256(mapper.writeValueAsBytes(projection.project(document))));
            defaultRest.put(id, MechanicsHashes.sha256(mapper.writeValueAsBytes(service.findById(id))));
        }
        Path directory = Path.of(System.getProperty("mechanics.compatibility.directory"));
        Files.createDirectories(directory);
        MechanicsJson.mapper().writeValue(directory.resolve("canonical.json").toFile(), canonical);
        MechanicsJson.mapper().writeValue(directory.resolve("private.json").toFile(), privateInspection);
        MechanicsJson.mapper().writeValue(directory.resolve("default-rest.json").toFile(), defaultRest);
        MechanicsJson.mapper().writeValue(directory.resolve("spiritual-weapon.json").toFile(), document());
    }

    private MechanicsDocument document() {
        return inspection.documents().stream()
                .filter(value -> value.owner().id().equals("spell:phb:spiritual-weapon"))
                .findFirst().orElseThrow();
    }

    private void assertTextEvidence(MechanicsDocument document, String pointer, int start, int end) {
        assertTrue(document.evidence().stream()
                .filter(com.thedndpub.data.mechanics.model.TextMechanicsEvidence.class::isInstance)
                .map(com.thedndpub.data.mechanics.model.TextMechanicsEvidence.class::cast)
                .anyMatch(evidence -> evidence.syntax().location().jsonPointer().equals(pointer)
                        && evidence.syntax().span().startInclusive() == start
                        && evidence.syntax().span().endExclusive() == end));
    }

}
