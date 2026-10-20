package com.thedndpub.data.mechanics.spell;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.mechanics.model.AppliesOnEachAttackRollOutcome;
import com.thedndpub.data.mechanics.model.AttackActivationTiming;
import com.thedndpub.data.mechanics.model.AttackActorRole;
import com.thedndpub.data.mechanics.model.AttackKind;
import com.thedndpub.data.mechanics.model.AttackRollInstanceScope;
import com.thedndpub.data.mechanics.model.AttackRollMechanic;
import com.thedndpub.data.mechanics.model.AttackRollOutcome;
import com.thedndpub.data.mechanics.model.DamageMechanic;
import com.thedndpub.data.mechanics.model.DiceAmount;
import com.thedndpub.data.mechanics.model.FixedDamageType;
import com.thedndpub.data.mechanics.model.InstanceTargetAllocation;
import com.thedndpub.data.mechanics.model.MechanicsDocument;
import com.thedndpub.data.mechanics.model.MechanicsJson;
import com.thedndpub.data.mechanics.model.SpellSlotInstanceCount;
import com.thedndpub.data.mechanics.model.TargetCategory;
import com.thedndpub.data.mechanics.model.TargetCategoryEligibilityStatus;
import com.thedndpub.data.mechanics.model.TargetReusePolicy;
import com.thedndpub.data.mechanics.model.TargetSelectionCardinality;
import com.thedndpub.data.mechanics.model.TextMechanicsEvidence;
import com.thedndpub.data.mechanics.model.UnknownSubject;
import com.thedndpub.data.mechanics.model.UnknownValueReason;
import com.thedndpub.data.mechanics.model.UnsupportedReason;
import com.thedndpub.data.mechanics.review.MechanicsReviewStatus;
import com.thedndpub.data.services.SpellMechanicsInspectionCatalog;
import com.thedndpub.data.services.SpellCatalog;
import com.thedndpub.data.dte.common.entry.NamedEntryDte;
import com.thedndpub.data.dte.common.entry.TextEntryDte;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ScorchingRayMultiAttackTests {
    private static final String OWNER_ID = "spell:phb:scorching-ray";

    @Autowired
    private SpellMechanicsInspectionCatalog inspection;
    @Autowired
    private SpellCatalog spells;
    @Autowired
    private ObjectMapper mapper;

    private MechanicsDocument document;
    private com.thedndpub.data.mechanics.model.MechanicRecord attack;
    private com.thedndpub.data.mechanics.model.MechanicRecord damage;

    @BeforeAll
    void loadScorchingRay() {
        document = inspection.documents().stream()
                .filter(value -> value.owner().id().equals(OWNER_ID))
                .findFirst().orElseThrow();
        attack = document.interpretations().stream()
                .filter(value -> value.id().value().endsWith(":attack-roll"))
                .findFirst().orElseThrow();
        damage = document.interpretations().stream()
                .filter(value -> value.id().value().endsWith(":damage"))
                .findFirst().orElseThrow();
    }

    @Test
    void attackClaimCapturesIndependentRaysAndExplicitlyUnknownTargetCategory() {
        AttackRollMechanic payload = assertInstanceOf(AttackRollMechanic.class, attack.payload());
        assertEquals("mechanic:spell:phb:scorching-ray:attack-roll", attack.id().value());
        assertEquals(1, attack.revision().number());
        assertEquals(AttackKind.RANGED_SPELL, payload.attackKind());
        assertEquals(AttackActorRole.CASTER, payload.actorRole());
        assertEquals(3, payload.attackCount());
        assertTrue(payload.attacksIndependent());
        assertEquals(AttackActivationTiming.CAST_TIME, payload.activationTiming());
        assertEquals(new SpellSlotInstanceCount(2, 3, 1), payload.countProgression());
        assertEquals(TargetSelectionCardinality.EXACTLY_ONE_PER_INSTANCE,
                payload.targetAllocation().cardinality());
        assertNull(payload.targetAllocation().eligibleTargetCategories());
        assertEquals(TargetCategoryEligibilityStatus.UNSPECIFIED_IN_SOURCE,
                payload.targetAllocation().targetCategoryEligibilityStatus());
        assertEquals(TargetReusePolicy.REUSE_ALLOWED, payload.targetAllocation().targetReusePolicy());
        assertTrue(payload.targetAllocation().withinSpellRange());
        UnknownSubject subject = assertInstanceOf(UnknownSubject.class, attack.context().subject());
        assertEquals(UnknownValueReason.NOT_AUTHORED, subject.reason());
        assertTrue(attack.context().bindings().isEmpty());
    }

    @Test
    void spellSlotProgressionCountsTotalRaysAndRejectsInvalidLevelsAndOverflow() {
        SpellSlotInstanceCount count = (SpellSlotInstanceCount)
                ((AttackRollMechanic) attack.payload()).countProgression();
        assertEquals(3, count.totalInstancesAtSlotLevel(2));
        assertEquals(4, count.totalInstancesAtSlotLevel(3));
        assertEquals(5, count.totalInstancesAtSlotLevel(4));
        assertEquals(6, count.totalInstancesAtSlotLevel(5));
        assertEquals(10, count.totalInstancesAtSlotLevel(9));
        assertThrows(IllegalArgumentException.class, () -> count.totalInstancesAtSlotLevel(1));
        assertThrows(IllegalArgumentException.class, () -> count.totalInstancesAtSlotLevel(10));
        assertThrows(IllegalArgumentException.class, () -> new SpellSlotInstanceCount(0, 3, 1));
        assertThrows(IllegalArgumentException.class, () -> new SpellSlotInstanceCount(10, 3, 1));
        assertThrows(IllegalArgumentException.class, () -> new SpellSlotInstanceCount(2, 0, 1));
        assertThrows(IllegalArgumentException.class, () -> new SpellSlotInstanceCount(2, 3, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new SpellSlotInstanceCount(Integer.MAX_VALUE, 3, 1)
                        .totalInstancesAtSlotLevel(Integer.MAX_VALUE));
        assertThrows(IllegalArgumentException.class,
                () -> new SpellSlotInstanceCount(2, Integer.MAX_VALUE, Integer.MAX_VALUE)
                        .totalInstancesAtSlotLevel(3));
        assertThrows(IllegalArgumentException.class, () -> new AttackRollMechanic(
                AttackKind.RANGED_SPELL, AttackActorRole.CASTER, 2, true, AttackActivationTiming.CAST_TIME,
                count, null));
        assertThrows(IllegalArgumentException.class, () -> new AttackRollMechanic(
                AttackKind.RANGED_SPELL, AttackActorRole.CASTER, 3, false, AttackActivationTiming.CAST_TIME,
                count, null));
    }

    @Test
    void eachRayHitTriggersOnlyItsCorrespondingDamageInstance() {
        assertEquals("mechanic:spell:phb:scorching-ray:damage", damage.id().value());
        assertEquals(1, damage.revision().number());
        DamageMechanic payload = assertInstanceOf(DamageMechanic.class, damage.payload());
        assertEquals(new DiceAmount(2, 6, 0), payload.amount());
        assertEquals(new FixedDamageType("fire"), payload.damageType());
        assertNull(payload.characterLevelScaling());
        AppliesOnEachAttackRollOutcome binding = assertInstanceOf(
                AppliesOnEachAttackRollOutcome.class, damage.context().bindings().getFirst());
        assertEquals(attack.revision().reference(attack.id()), binding.attackRoll());
        assertEquals(AttackRollOutcome.HIT, binding.outcome());
        assertEquals(AttackRollInstanceScope.CORRESPONDING_ATTACK_INSTANCE, binding.instanceScope());
        assertEquals(attack.context().subject(), damage.context().subject());
        assertEquals(1, damage.context().bindings().size());
    }

    @Test
    void evidenceSpansCoverCountAttackAllocationSlotScalingDamageAndUnknownCategory() {
        assertTextEvidence(attack, "/spell/285/entries/0", "three rays of fire",
                "/payload/attackCount");
        assertTextEvidence(attack, "/spell/285/entries/1", "Make a ranged spell attack for each ray",
                "/payload/attacksIndependent");
        assertTextEvidence(attack, "/spell/285/entries/0", "at one target or several",
                "/payload/targetAllocation/targetReusePolicy");
        assertTextEvidence(attack, "/spell/285/entries/0", "targets within range",
                "/payload/targetAllocation/withinSpellRange");
        assertTextEvidence(attack, "/spell/285/entriesHigherLevel/0/entries/0",
                "one additional ray for each slot level above 2nd",
                "/payload/countProgression/instancesAddedPerSlotLevel");
        assertTextEvidence(attack, "/spell/285/entriesHigherLevel/0/entries/0",
                "When you cast this spell using a spell slot of 3rd level or higher",
                "/payload/countProgression");
        assertTextEvidence(damage, "/spell/285/entries/1", "{@damage 2d6}", "/payload/amount");
        assertTextEvidence(damage, "/spell/285/entries/1", "On a hit", "/context/bindings/0");
        assertEquals("c9e54cce55e7ce0fc988feb1b604bf505faa5c042635542fa6b6ed36724e297a",
                document.sources().getFirst().resourceSha256());
    }

    @Test
    void unresolvedTargetCategoryRemainsAnEvidenceBackedPartialDiagnostic() {
        assertEquals(1, document.unsupported().size());
        var diagnostic = document.unsupported().getFirst();
        assertEquals("scorching-ray-target-category-unspecified", diagnostic.id());
        assertEquals(UnsupportedReason.SOURCE_UNSPECIFIED, diagnostic.reason());
        assertEquals(List.of(attack.evidenceAssociations().stream()
                .filter(value -> value.claimPath().equals("/payload/targetAllocation/targetCategoryEligibilityStatus"))
                .findFirst().orElseThrow().evidenceId()), diagnostic.evidenceIds());
        assertEquals("PARTIAL", document.coverage().status().name());
        assertEquals(MechanicsReviewStatus.PENDING_REVIEW, attack.review().recordedStatus());
        assertEquals(MechanicsReviewStatus.PENDING_REVIEW, damage.review().recordedStatus());
        assertTrue(attack.licensing().status().name().equals("NEEDS_REVIEW"));
        assertTrue(damage.licensing().status().name().equals("NEEDS_REVIEW"));
        assertEquals(0, document.reviewSummary().verified());
    }

    @Test
    void targetCategoryStatesCannotBeConfusedWithPositiveEligibility() {
        assertThrows(IllegalArgumentException.class, () -> new InstanceTargetAllocation(
                TargetSelectionCardinality.EXACTLY_ONE_PER_INSTANCE, List.of(),
                TargetReusePolicy.REUSE_ALLOWED, true,
                TargetCategoryEligibilityStatus.UNSPECIFIED_IN_SOURCE));
        assertThrows(IllegalArgumentException.class, () -> new InstanceTargetAllocation(
                TargetSelectionCardinality.EXACTLY_ONE_PER_INSTANCE, List.of(TargetCategory.CREATURE),
                TargetReusePolicy.REUSE_ALLOWED, true,
                TargetCategoryEligibilityStatus.UNSPECIFIED_IN_SOURCE));
        assertThrows(IllegalArgumentException.class, () -> new InstanceTargetAllocation(
                TargetSelectionCardinality.EXACTLY_ONE_PER_INSTANCE, null,
                TargetReusePolicy.REUSE_ALLOWED, true, TargetCategoryEligibilityStatus.SOURCE_SPECIFIED));
        assertThrows(IllegalArgumentException.class, () -> new InstanceTargetAllocation(
                TargetSelectionCardinality.EXACTLY_ONE_PER_INSTANCE, null,
                TargetReusePolicy.REUSE_ALLOWED, true));
    }

    @Test
    void canonicalSerializationIsStableAndRoundTrips() throws Exception {
        byte[] first = MechanicsJson.mapper().writeValueAsBytes(document);
        byte[] second = MechanicsJson.mapper().writeValueAsBytes(document);
        assertArrayEquals(first, second);
        assertEquals(document, MechanicsJson.mapper().readValue(first, MechanicsDocument.class));
        String allocation = mapper.writeValueAsString(
                ((AttackRollMechanic) attack.payload()).targetAllocation());
        assertTrue(allocation.contains("UNSPECIFIED_IN_SOURCE"));
        assertFalse(allocation.contains("eligibleTargetCategories"));
    }

    private void assertTextEvidence(com.thedndpub.data.mechanics.model.MechanicRecord claim,
                                    String pointer, String phrase, String claimPath) {
        var association = claim.evidenceAssociations().stream()
                .filter(value -> value.claimPath().equals(claimPath))
                .findFirst().orElseThrow();
        TextMechanicsEvidence evidence = assertInstanceOf(TextMechanicsEvidence.class, document.evidence().stream()
                .filter(value -> value.id().equals(association.evidenceId()))
                .findFirst().orElseThrow());
        assertEquals(pointer, evidence.syntax().location().jsonPointer());
        String authoredText;
        if (pointer.contains("entriesHigherLevel")) {
            NamedEntryDte higherLevels = assertInstanceOf(NamedEntryDte.class,
                    spells.findById(OWNER_ID).entriesHigherLevel().getFirst());
            authoredText = assertInstanceOf(TextEntryDte.class, higherLevels.entries().getFirst()).text();
        } else {
            authoredText = assertInstanceOf(TextEntryDte.class, spells.findById(OWNER_ID).entries()
                    .get(pointer.endsWith("/entries/0") ? 0 : 1)).text();
        }
        int start = authoredText.indexOf(phrase);
        assertTrue(start >= 0, "Source phrase missing: " + phrase);
        assertEquals(start, evidence.syntax().span().startInclusive());
        assertEquals(start + phrase.length(), evidence.syntax().span().endExclusive());
    }
}
