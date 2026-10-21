package com.thedndpub.data.mechanics.spell;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.mechanics.model.AmountSymbol;
import com.thedndpub.data.mechanics.model.AppliesOnAttackRollOutcome;
import com.thedndpub.data.mechanics.model.AttackActivationTiming;
import com.thedndpub.data.mechanics.model.AttackActorRole;
import com.thedndpub.data.mechanics.model.AttackKind;
import com.thedndpub.data.mechanics.model.AttackModifierAttribution;
import com.thedndpub.data.mechanics.model.AttackRollMechanic;
import com.thedndpub.data.mechanics.model.AttackRollOutcome;
import com.thedndpub.data.mechanics.model.DamageMechanic;
import com.thedndpub.data.mechanics.model.DeclaredSpellDurationReference;
import com.thedndpub.data.mechanics.model.DiceAmount;
import com.thedndpub.data.mechanics.model.EnchantedObjectCollectionMechanic;
import com.thedndpub.data.mechanics.model.EnchantedObjectDeliveryMode;
import com.thedndpub.data.mechanics.model.EnchantedObjectUseConsumption;
import com.thedndpub.data.mechanics.model.EnchantedObjectUserRole;
import com.thedndpub.data.mechanics.model.FixedDamageType;
import com.thedndpub.data.mechanics.model.MechanicsCompleteness;
import com.thedndpub.data.mechanics.model.MechanicsDocument;
import com.thedndpub.data.mechanics.model.MechanicsHashes;
import com.thedndpub.data.mechanics.model.MechanicsJson;
import com.thedndpub.data.mechanics.model.SpellTargetSubject;
import com.thedndpub.data.mechanics.model.SumAmount;
import com.thedndpub.data.mechanics.model.StructuredFieldMechanicsEvidence;
import com.thedndpub.data.mechanics.model.SymbolicAmount;
import com.thedndpub.data.mechanics.model.TextMechanicsEvidence;
import com.thedndpub.data.services.SpellMechanicsInspectionCatalog;
import com.thedndpub.data.services.SpellCatalog;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class XgeMagicStoneMechanicsTests {
    @Autowired
    private SpellMechanicsInspectionCatalog inspection;
    @Autowired
    private SpellCatalog spells;
    @Autowired
    private ObjectMapper mapper;

    @Test
    void capturesFiniteTransferablePerUseEnchantmentAndHitDamage() throws Exception {
        MechanicsDocument document = document();
        assertEquals("17e7e9fe7ad20c7df094d2a4929558c5d2a331f7e1498c7575181864ec5dc1dc",
                document.sources().getFirst().resourceSha256());
        assertEquals(List.of("mechanic:spell:xge:magic-stone:enchanted-stones",
                        "mechanic:spell:xge:magic-stone:attack-roll",
                        "mechanic:spell:xge:magic-stone:damage"),
                document.interpretations().stream().map(claim -> claim.id().value()).toList());
        assertEquals(1, document.unsupported().size());
        assertTrue(document.unsupported().getFirst().id().endsWith("sling-range"));
        assertEquals(com.thedndpub.data.mechanics.model.UnsupportedReason.SOURCE_UNSPECIFIED,
                document.unsupported().getFirst().reason());
        assertEquals(MechanicsCompleteness.PARTIAL,
                document.interpretations().get(1).assessment().completeness());

        EnchantedObjectCollectionMechanic objects = assertInstanceOf(
                EnchantedObjectCollectionMechanic.class, document.interpretations().getFirst().payload());
        assertEquals(3, objects.maximumObjectCount());
        assertFalse(objects.objectsCreatedBySpell());
        assertEquals(AttackActivationTiming.BONUS_ACTION, objects.enchantmentActivationTiming());
        assertEquals(EnchantedObjectUseConsumption.ON_ATTACK_USE_REGARDLESS_OF_OUTCOME,
                objects.useConsumption());
        assertTrue(objects.consumptionAppliesOnlyToUsedObject());
        assertTrue(objects.recastingEndsUnusedEnchantments());
        assertInstanceOf(DeclaredSpellDurationReference.class, objects.enchantmentDuration());
        assertEquals(1, spells.findById("spell:xge:magic-stone").durations().getFirst().duration().amount());
        assertEquals("minute", spells.findById("spell:xge:magic-stone").durations().getFirst().duration().type());
        assertEquals(null, spells.findById("spell:xge:magic-stone").durations().getFirst().concentration());
        assertEquals("touch", spells.findById("spell:xge:magic-stone").range().distance().type());

        AttackRollMechanic attack = assertInstanceOf(AttackRollMechanic.class,
                document.interpretations().get(1).payload());
        assertEquals(AttackKind.RANGED_SPELL, attack.attackKind());
        assertEquals(AttackActorRole.ENCHANTED_OBJECT_USER, attack.actorRole());
        assertEquals(1, attack.attackCount());
        assertEquals(AttackActivationTiming.OBJECT_USE, attack.activationTiming());
        assertEquals(null, attack.countProgression());
        assertEquals(List.of(EnchantedObjectUserRole.SPELL_CASTER, EnchantedObjectUserRole.OTHER_CREATURE),
                attack.enchantedObjectUse().permittedAttackActors());
        assertEquals(1, attack.enchantedObjectUse().enchantedObjectCollection().number());
        assertEquals(AttackModifierAttribution.SPELL_CASTER_SPELLCASTING_ABILITY,
                attack.enchantedObjectUse().attackModifierAttribution());
        assertEquals(List.of(EnchantedObjectDeliveryMode.THROW, EnchantedObjectDeliveryMode.SLING),
                attack.enchantedObjectUse().delivery().modes());
        assertEquals(60, attack.enchantedObjectUse().delivery().thrownRangeFeet());
        assertThrows(UnsupportedOperationException.class,
                () -> attack.enchantedObjectUse().delivery().modes().clear());
        assertThrows(UnsupportedOperationException.class,
                () -> attack.enchantedObjectUse().permittedAttackActors().clear());

        DamageMechanic damage = assertInstanceOf(DamageMechanic.class, document.interpretations().get(2).payload());
        assertEquals(new SumAmount(List.of(new DiceAmount(1, 6, 0),
                new SymbolicAmount(AmountSymbol.CASTER_SPELLCASTING_ABILITY_MODIFIER))), damage.amount());
        assertEquals(new FixedDamageType("bludgeoning"), damage.damageType());
        assertEquals(1, document.interpretations().get(2).context().bindings().size());
        AppliesOnAttackRollOutcome hit = assertInstanceOf(AppliesOnAttackRollOutcome.class,
                document.interpretations().get(2).context().bindings().getFirst());
        assertEquals(AttackRollOutcome.HIT, hit.outcome());
        assertEquals(document.interpretations().get(1).revision().reference(document.interpretations().get(1).id()),
                hit.attackRoll());
        assertEquals(1, document.interpretations().get(2).revision().number());
        assertTrue(document.interpretations().stream().allMatch(claim -> claim.review().recordedStatus().name()
                .equals("PENDING_REVIEW") && claim.licensing().status().name().equals("NEEDS_REVIEW")));

        assertTextEvidence(document, "/spell/48/entries/0", 0, 57);
        assertTextEvidence(document, "/spell/48/entries/0", 58, 132);
        assertTextEvidence(document, "/spell/48/entries/0", 133, 187);
        assertTextEvidence(document, "/spell/48/entries/0", 188, 231);
        assertTextEvidence(document, "/spell/48/entries/0", 232, 365);
        assertTextEvidence(document, "/spell/48/entries/0", 366, 374);
        assertTextEvidence(document, "/spell/48/entries/0", 376, 472);
        assertTextEvidence(document, "/spell/48/entries/0", 473, 541);
        assertTextEvidence(document, "/spell/48/entries/1", 0, 100);
        assertTrue(document.evidence().stream().filter(StructuredFieldMechanicsEvidence.class::isInstance)
                .map(StructuredFieldMechanicsEvidence.class::cast)
                .map(StructuredFieldMechanicsEvidence::jsonPointer)
                .collect(java.util.stream.Collectors.toSet()).containsAll(List.of(
                        "/spell/48/time/0", "/spell/48/range", "/spell/48/duration/0",
                        "/spell/48/spellAttack/0", "/spell/48/damageInflict/0")));
        byte[] first = MechanicsJson.mapper().writeValueAsBytes(document);
        byte[] second = MechanicsJson.mapper().writeValueAsBytes(document());
        assertTrue(java.util.Arrays.equals(first, second));
        assertEquals(document, MechanicsJson.mapper().readValue(first, MechanicsDocument.class));
        assertNotEquals("0".repeat(64), document.interpretations().get(1).revision().contentSha256());
    }

    @Test
    void rejectsInvalidFiniteCollectionBoundsAndMisboundAttackUse() {
        var duration = new DeclaredSpellDurationReference(spells.findById("spell:xge:magic-stone").id(), 0,
                document().sources().getFirst());
        assertThrows(IllegalArgumentException.class, () -> new EnchantedObjectCollectionMechanic(
                com.thedndpub.data.mechanics.model.EnchantedObjectCategory.PEBBLE, 4, false,
                AttackActivationTiming.BONUS_ACTION, com.thedndpub.data.mechanics.model.SpellCastingRangeKind.TOUCH,
                duration, EnchantedObjectUseConsumption.ON_ATTACK_USE_REGARDLESS_OF_OUTCOME, true, true));
        var use = document().interpretations().get(1).payload();
        var validUse = assertInstanceOf(AttackRollMechanic.class, use).enchantedObjectUse();
        assertThrows(IllegalArgumentException.class, () -> new AttackRollMechanic(
                AttackKind.RANGED_SPELL, AttackActorRole.CASTER, 1, false,
                AttackActivationTiming.OBJECT_USE, validUse));
        assertThrows(IllegalArgumentException.class, () -> new EnchantedObjectCollectionMechanic(
                com.thedndpub.data.mechanics.model.EnchantedObjectCategory.PEBBLE, 0, false,
                AttackActivationTiming.BONUS_ACTION, com.thedndpub.data.mechanics.model.SpellCastingRangeKind.TOUCH,
                duration, EnchantedObjectUseConsumption.ON_ATTACK_USE_REGARDLESS_OF_OUTCOME, true, true));
    }

    @Test
    @EnabledIfSystemProperty(named = "mechanics.compatibility.capture", matches = "true")
    void captureCanonicalMagicStoneDocument() throws Exception {
        Path directory = Path.of(System.getProperty("mechanics.compatibility.directory"));
        Files.createDirectories(directory);
        MechanicsJson.mapper().writeValue(directory.resolve("magic-stone.json").toFile(), document());
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
                "spell:xge:magic-stone")).findFirst().orElseThrow();
    }
}
