package com.thedndpub.data.mechanics.spell;

import com.thedndpub.data.mechanics.model.AttackActivationTiming;
import com.thedndpub.data.mechanics.model.AdditionalAttackMissBehavior;
import com.thedndpub.data.mechanics.model.AttackActorRole;
import com.thedndpub.data.mechanics.model.AttackKind;
import com.thedndpub.data.mechanics.model.AttackRollMechanic;
import com.thedndpub.data.mechanics.model.AttackRollOutcome;
import com.thedndpub.data.mechanics.model.AppliesOnAttackRollOutcome;
import com.thedndpub.data.mechanics.model.AppliesOnAttackRollOutcome;
import com.thedndpub.data.mechanics.model.ChainDistanceOrigin;
import com.thedndpub.data.mechanics.model.ChainTargetCategory;
import com.thedndpub.data.mechanics.model.ChainTargetReusePolicy;
import com.thedndpub.data.mechanics.model.ConditionalAttackChainMechanic;
import com.thedndpub.data.mechanics.model.DamageDiceMatchRule;
import com.thedndpub.data.mechanics.model.DamageDicePopulation;
import com.thedndpub.data.mechanics.model.DamageMechanic;
import com.thedndpub.data.mechanics.model.FixedDamageType;
import com.thedndpub.data.mechanics.model.DamageTypeChoice;
import com.thedndpub.data.mechanics.model.DamageTypeChoiceTiming;
import com.thedndpub.data.mechanics.model.DiceAmount;
import com.thedndpub.data.mechanics.model.ExternalDamageDiceTreatment;
import com.thedndpub.data.mechanics.model.MechanicsCompleteness;
import com.thedndpub.data.mechanics.model.MechanicsDocument;
import com.thedndpub.data.mechanics.model.MechanicContext;
import com.thedndpub.data.mechanics.model.MechanicRecord;
import com.thedndpub.data.mechanics.model.MechanicsJson;
import com.thedndpub.data.mechanics.model.MechanicsLicensingStatus;
import com.thedndpub.data.mechanics.model.ProcessingState;
import com.thedndpub.data.mechanics.review.MechanicsReviewStatus;
import com.thedndpub.data.mechanics.model.SpellSlotDamageScaling;
import com.thedndpub.data.mechanics.model.SpellSlotLeapLimit;
import com.thedndpub.data.mechanics.model.SpellTargetSubject;
import com.thedndpub.data.mechanics.model.SubjectReference;
import com.thedndpub.data.mechanics.model.TargetScope;
import com.thedndpub.data.mechanics.model.TextMechanicsEvidence;
import com.thedndpub.data.services.SpellMechanicsInspectionCatalog;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class XphbChromaticOrbMechanicsTests {
    private static final String RESOURCE_SHA = "442544a91aad1181cbad78d49cfa48f9ced7d8b048dbc781e32fa6fefc6278b9";
    private static final List<String> DAMAGE_TYPES = List.of("acid", "cold", "fire", "lightning", "poison", "thunder");

    @Autowired
    private SpellMechanicsInspectionCatalog inspection;

    @Test
    void interpretsDamageDiceTriggeredConditionalChainFromXphbOnly() throws Exception {
        MechanicsDocument document = document();
        assertEquals("spell:xphb:chromatic-orb", document.owner().id());
        assertEquals("XPHB", document.owner().source().identity().code());
        assertEquals("data/spells/spells-xphb.json", document.sources().getFirst().resourcePath());
        assertEquals(RESOURCE_SHA, document.sources().getFirst().resourceSha256());
        MechanicsDocument olderPhb = inspection.documents().stream().filter(candidate ->
                candidate.owner().id().equals("spell:phb:chromatic-orb")).findFirst().orElseThrow();
        assertEquals(ProcessingState.NOT_ATTEMPTED, olderPhb.processing().state());
        assertTrue(olderPhb.interpretations().isEmpty());
        assertEquals("/spell/48", document.evidence().getFirst() instanceof TextMechanicsEvidence text
                ? text.syntax().location().owner().jsonPointer() : "/spell/48");
        assertEquals(3, document.interpretations().size());
        assertEquals(List.of("mechanic:spell:xphb:chromatic-orb:attack-roll",
                        "mechanic:spell:xphb:chromatic-orb:damage",
                        "mechanic:spell:xphb:chromatic-orb:conditional-chain"),
                document.interpretations().stream().map(record -> record.id().value()).toList());
        assertEquals(4, document.unsupported().size());
        assertTrue(document.unsupported().stream().anyMatch(item -> item.id().endsWith("attack-range")));
        assertTrue(document.unsupported().stream().anyMatch(item -> item.id().endsWith("external-damage-dice")));
        assertTrue(document.unsupported().stream().anyMatch(item -> item.id().endsWith("additional-attack-miss")));
        assertTrue(document.unsupported().stream().anyMatch(item -> item.id().endsWith("target-category")));

        var attackClaim = document.interpretations().get(0);
        AttackRollMechanic attack = assertInstanceOf(AttackRollMechanic.class, attackClaim.payload());
        assertEquals(AttackKind.RANGED_SPELL, attack.attackKind());
        assertEquals(AttackActorRole.CASTER, attack.actorRole());
        assertEquals(1, attack.attackCount());
        assertEquals(AttackActivationTiming.CAST_TIME, attack.activationTiming());
        assertFalse(attack.attacksIndependent());
        assertEquals(TargetScope.SINGLE_TARGET_UNSPECIFIED_CATEGORY,
                assertInstanceOf(SpellTargetSubject.class, attackClaim.context().subject()).scope());

        var damageClaim = document.interpretations().get(1);
        DamageMechanic damage = assertInstanceOf(DamageMechanic.class, damageClaim.payload());
        assertEquals(new DiceAmount(3, 8, 0), damage.amount());
        DamageTypeChoice choice = assertInstanceOf(DamageTypeChoice.class, damage.damageType());
        assertEquals(DAMAGE_TYPES, choice.alternatives());
        assertEquals(1, choice.count());
        assertEquals(new SpellSlotDamageScaling(new DiceAmount(1, 8, 0), 1, 2, 1), damage.spellSlotScaling());
        AppliesOnAttackRollOutcome hit = assertInstanceOf(AppliesOnAttackRollOutcome.class,
                damageClaim.context().bindings().getFirst());
        assertEquals(attackClaim.revision().reference(attackClaim.id()), hit.attackRoll());
        for (int slot = 1; slot <= 9; slot++) {
            assertEquals(slot - 1, damage.spellSlotScaling().incrementsAtSlotLevel(slot));
        }

        var chainClaim = document.interpretations().get(2);
        ConditionalAttackChainMechanic chain = assertInstanceOf(ConditionalAttackChainMechanic.class,
                chainClaim.payload());
        assertEquals(attackClaim.revision().reference(attackClaim.id()), chain.initialAttack());
        assertEquals(damageClaim.revision().reference(damageClaim.id()), chain.reusableDamage());
        DamageDiceMatchRule trigger = chain.continuationTrigger();
        assertEquals(8, trigger.dieSides());
        assertEquals(2, trigger.minimumMatchingDice());
        assertEquals(DamageDicePopulation.SPELL_DAMAGE_DICE_IN_ONE_ROLL, trigger.population());
        assertEquals(ExternalDamageDiceTreatment.UNSPECIFIED_BY_SOURCE, trigger.externalDiceTreatment());
        assertEquals(ChainTargetCategory.UNSPECIFIED_IN_SOURCE, chain.subsequentTargetCategory());
        assertEquals(TargetScope.SINGLE_TARGET_UNSPECIFIED_CATEGORY,
                assertInstanceOf(SpellTargetSubject.class, chainClaim.context().subject()).scope());
        assertEquals(ChainTargetReusePolicy.DIFFERENT_FROM_PREVIOUS_AND_CREATURES_ONCE_PER_CASTING,
                chain.targetReusePolicy());
        assertEquals(ChainDistanceOrigin.PREVIOUS_TARGET, chain.targetDistance().origin());
        assertEquals(30, chain.targetDistance().maximumFeet());
        assertEquals(DamageTypeChoiceTiming.WHEN_CASTING, chain.damageTypeChoiceTiming());
        assertTrue(chain.damageTypePersistsAcrossLeaps());
        assertTrue(chain.atMostOneLeapPerQualifyingRoll());
        assertTrue(chain.eachNewTargetRequiresAttackRoll());
        assertEquals(AdditionalAttackMissBehavior.UNSPECIFIED_IN_SOURCE, chain.additionalAttackMissBehavior());
        assertFalse(chain.maximumLeapsCountsInitialAttack());
        assertEquals(1, chain.leapLimit().maximumLeapsAtSlotLevel(1));
        for (int slot = 2; slot <= 9; slot++) {
            assertEquals(slot, chain.leapLimit().maximumLeapsAtSlotLevel(slot));
        }
        assertEquals(MechanicsCompleteness.PARTIAL, chainClaim.assessment().completeness());

        assertTrue(document.interpretations().stream().allMatch(claim ->
                claim.review().recordedStatus() == MechanicsReviewStatus.PENDING_REVIEW
                        && claim.licensing().status() == MechanicsLicensingStatus.NEEDS_REVIEW));
        assertEquals(0, document.reviewSummary().verified());
        assertTextEvidence(document, "/spell/48/entries/0",
                "Choose Acid, Cold, Fire, Lightning, Poison, or Thunder for the type of orb you create, and then make a ranged spell attack against the target.");
        assertTextEvidence(document, "/spell/48/entries/1", "If you roll the same number on two or more of the d8s,");
        assertTextEvidence(document, "/spell/48/entries/1",
                "the orb leaps to a different target of your choice within 30 feet of the target.");
        assertTextEvidence(document, "/spell/48/entriesHigherLevel/0/entries/0",
                "The orb can leap a maximum number of times equal to the level of the slot expended,");

        byte[] first = MechanicsJson.mapper().writeValueAsBytes(document);
        byte[] second = MechanicsJson.mapper().writeValueAsBytes(document());
        assertTrue(Arrays.equals(first, second));
        assertEquals(document, MechanicsJson.mapper().readValue(first, MechanicsDocument.class));
        assertEquals(64, chainClaim.revision().contentSha256().length());
    }

    @Test
    void rejectsInvalidMatchingDiceAndLeapLimits() {
        var claim = document().interpretations().get(2);
        ConditionalAttackChainMechanic chain = assertInstanceOf(ConditionalAttackChainMechanic.class, claim.payload());
        assertThrows(IllegalArgumentException.class, () -> new DamageDiceMatchRule(
                chain.reusableDamage(), 1, 2, DamageDicePopulation.SPELL_DAMAGE_DICE_IN_ONE_ROLL,
                ExternalDamageDiceTreatment.UNSPECIFIED_BY_SOURCE));
        assertThrows(IllegalArgumentException.class, () -> new DamageDiceMatchRule(
                chain.reusableDamage(), 8, 1, DamageDicePopulation.SPELL_DAMAGE_DICE_IN_ONE_ROLL,
                ExternalDamageDiceTreatment.UNSPECIFIED_BY_SOURCE));
        assertThrows(IllegalArgumentException.class, () -> new SpellSlotLeapLimit(2, 2, -1, 1));
        assertThrows(UnsupportedOperationException.class, () ->
                assertInstanceOf(DamageTypeChoice.class, damage().damageType()).alternatives().clear());
        assertThrows(IllegalArgumentException.class, () -> new ConditionalAttackChainMechanic(
                chain.initialAttack(), chain.reusableDamage(),
                new DamageDiceMatchRule(chain.initialAttack(), 8, 2, DamageDicePopulation.SPELL_DAMAGE_DICE_IN_ONE_ROLL,
                        ExternalDamageDiceTreatment.UNSPECIFIED_BY_SOURCE),
                true, chain.subsequentTargetCategory(), chain.targetReusePolicy(), chain.targetDistance(),
                chain.leapLimit(), chain.damageTypeChoiceTiming(), true, true,
                chain.additionalAttackMissBehavior(), false));
    }

    @Test
    void genericChainValidationAcceptsAValidNonChromaticConfiguration() {
        MechanicsDocument original = document();
        var sourceAttack = original.interpretations().get(0);
        var sourceDamage = original.interpretations().get(1);
        var sourceChain = original.interpretations().get(2);
        SubjectReference target = new SpellTargetSubject(TargetScope.SINGLE_TARGET_CREATURE);
        DamageMechanic originalDamage = (DamageMechanic) sourceDamage.payload();

        MechanicRecord attack = MechanicRecord.propose(sourceAttack.id(),
                new AttackRollMechanic(AttackKind.MELEE_SPELL, AttackActorRole.SOURCE_SELECTED_CREATURE,
                        1, true, AttackActivationTiming.BONUS_ACTION),
                new MechanicContext(target, List.of()), sourceAttack.evidenceAssociations(),
                sourceAttack.derivation(), sourceAttack.assessment());
        DamageMechanic alternateDamage = new DamageMechanic(new DiceAmount(2, 6, 0),
                new FixedDamageType("fire"));
        List<com.thedndpub.data.mechanics.model.MechanicEvidenceAssociation> damageEvidence =
                sourceDamage.evidenceAssociations().stream().filter(association -> List.of("/payload",
                        "/payload/amount", "/payload/damageType", "/context/bindings/0")
                        .contains(association.claimPath())).toList();
        MechanicRecord damage = MechanicRecord.propose(sourceDamage.id(), alternateDamage,
                new MechanicContext(target, List.of(new AppliesOnAttackRollOutcome(
                        attack.revision().reference(attack.id()), AttackRollOutcome.HIT,
                        sourceDamage.context().bindings().stream()
                                .filter(AppliesOnAttackRollOutcome.class::isInstance)
                                .map(AppliesOnAttackRollOutcome.class::cast).findFirst().orElseThrow().evidenceIds()))),
                damageEvidence, sourceDamage.derivation(), sourceDamage.assessment());
        ConditionalAttackChainMechanic source = (ConditionalAttackChainMechanic) sourceChain.payload();
        ConditionalAttackChainMechanic alternate = new ConditionalAttackChainMechanic(
                attack.revision().reference(attack.id()), damage.revision().reference(damage.id()),
                new DamageDiceMatchRule(damage.revision().reference(damage.id()), 6, 2,
                        DamageDicePopulation.SPELL_DAMAGE_DICE_IN_ONE_ROLL,
                        ExternalDamageDiceTreatment.UNSPECIFIED_BY_SOURCE),
                true, source.subsequentTargetCategory(), source.targetReusePolicy(), source.targetDistance(),
                new SpellSlotLeapLimit(2, 2, 2, 2), source.damageTypeChoiceTiming(), false, true,
                AdditionalAttackMissBehavior.UNSPECIFIED_IN_SOURCE, true);
        MechanicRecord chain = MechanicRecord.propose(sourceChain.id(), alternate,
                new MechanicContext(target, List.of()), sourceChain.evidenceAssociations(),
                sourceChain.derivation(), sourceChain.assessment());

        MechanicsDocument accepted = MechanicsDocument.create(original.owner(), 1, original.sources(),
                original.processing(), List.of(attack, damage, chain), original.observations(), original.unsupported(),
                original.evidence(), original.coverage());
        assertEquals(AttackKind.MELEE_SPELL,
                ((AttackRollMechanic) accepted.interpretations().get(0).payload()).attackKind());
        assertEquals(new DiceAmount(2, 6, 0), ((DamageMechanic) accepted.interpretations().get(1).payload()).amount());
        assertEquals(2, ((ConditionalAttackChainMechanic) accepted.interpretations().get(2).payload())
                .leapLimit().maximumLeapsAtSlotLevel(2));
        assertTrue(((ConditionalAttackChainMechanic) accepted.interpretations().get(2).payload())
                .maximumLeapsCountsInitialAttack());
    }

    @Test
    @EnabledIfSystemProperty(named = "mechanics.compatibility.capture", matches = "true")
    void captureCanonicalChromaticOrbDocument() throws Exception {
        Path directory = Path.of(System.getProperty("mechanics.compatibility.directory"));
        Files.createDirectories(directory);
        MechanicsJson.mapper().writeValue(directory.resolve("chromatic-orb.json").toFile(), document());
    }

    private void assertTextEvidence(MechanicsDocument document, String pointer, String phrase) throws Exception {
        var sourceTree = MechanicsJson.mapper().readTree(Files.readString(
                Path.of("src/main/resources/data/spells/spells-xphb.json")));
        String authored = sourceTree.at(pointer).textValue();
        int expectedStart = authored.indexOf(phrase);
        assertTrue(expectedStart >= 0);
        assertTrue(document.evidence().stream().filter(TextMechanicsEvidence.class::isInstance)
                .map(TextMechanicsEvidence.class::cast).anyMatch(text -> {
                    return text.syntax().location().jsonPointer().equals(pointer)
                            && text.syntax().span().startInclusive() == expectedStart
                            && text.syntax().span().endExclusive() - text.syntax().span().startInclusive()
                                    == phrase.length()
                            && text.textSha256().length() == 64;
                }));
    }

    private DamageMechanic damage() {
        return assertInstanceOf(DamageMechanic.class, document().interpretations().get(1).payload());
    }

    private MechanicsDocument document() {
        return inspection.documents().stream().filter(candidate -> candidate.owner().id().equals(
                "spell:xphb:chromatic-orb")).findFirst().orElseThrow();
    }
}
