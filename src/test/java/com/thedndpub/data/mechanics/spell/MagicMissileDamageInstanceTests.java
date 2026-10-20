package com.thedndpub.data.mechanics.spell;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.mechanics.model.DamageInstanceSeriesMechanic;
import com.thedndpub.data.mechanics.model.DamageRollGrouping;
import com.thedndpub.data.mechanics.model.DamageResolutionMode;
import com.thedndpub.data.mechanics.model.EvidenceRole;
import com.thedndpub.data.mechanics.model.DiceAmount;
import com.thedndpub.data.mechanics.model.EffectTiming;
import com.thedndpub.data.mechanics.model.FixedDamageType;
import com.thedndpub.data.mechanics.model.InstanceTargetRequirement;
import com.thedndpub.data.mechanics.model.MechanicPayload;
import com.thedndpub.data.mechanics.model.MechanicsJson;
import com.thedndpub.data.mechanics.model.SpellSlotInstanceCount;
import com.thedndpub.data.mechanics.model.SumAmount;
import com.thedndpub.data.mechanics.model.TargetCategory;
import com.thedndpub.data.mechanics.model.TargetReusePolicy;
import com.thedndpub.data.mechanics.model.TargetSelectionCardinality;
import com.thedndpub.data.mechanics.model.ProcessingState;
import com.thedndpub.data.mechanics.model.TextMechanicsEvidence;
import com.thedndpub.data.services.SpellMechanicsInspectionCatalog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class MagicMissileDamageInstanceTests {
    @Autowired
    private SpellMechanicsInspectionCatalog inspection;
    @Autowired
    private ObjectMapper applicationMapper;

    @Test
    void extractsOneAutomaticHitDamageSeriesWithoutAttackClaims() throws Exception {
        var document = inspection.documents().stream()
                .filter(value -> value.owner().id().equals("spell:phb:magic-missile"))
                .findFirst().orElseThrow();
        assertEquals(ProcessingState.PROCESSED, document.processing().state());
        assertEquals("spell:phb:magic-missile", document.owner().id());
        assertEquals(1, document.interpretations().size());
        assertEquals(1, document.observations().size());
        var claim = document.interpretations().getFirst();
        assertEquals("mechanic:spell:phb:magic-missile:damage-per-dart", claim.id().value());
        assertEquals(1, claim.revision().number());
        assertEquals("c9e54cce55e7ce0fc988feb1b604bf505faa5c042635542fa6b6ed36724e297a",
                claim.derivation().sources().getFirst().resourceSha256());
        var series = assertInstanceOf(DamageInstanceSeriesMechanic.class, claim.payload());
        assertEquals(new SumAmount(java.util.List.of(new DiceAmount(1, 4, 0),
                new com.thedndpub.data.mechanics.model.IntegerAmount(1))), series.amountPerInstance());
        assertEquals(new FixedDamageType("force"), series.damageType());
        assertEquals(new SpellSlotInstanceCount(1, 3, 1), series.countProgression());
        assertEquals(DamageResolutionMode.AUTOMATIC_HIT, series.resolutionMode());
        assertEquals(EffectTiming.SIMULTANEOUS, series.timing());
        assertEquals(DamageRollGrouping.UNSPECIFIED, series.damageRollGrouping());
        assertEquals(TargetSelectionCardinality.EXACTLY_ONE_PER_INSTANCE,
                series.targetAllocation().cardinality());
        assertEquals(java.util.List.of(TargetCategory.CREATURE),
                series.targetAllocation().eligibleTargetCategories());
        assertEquals(InstanceTargetRequirement.VISIBLE_TO_CASTER,
                series.targetAllocation().requirements().getFirst());
        assertEquals(TargetReusePolicy.REUSE_ALLOWED, series.targetAllocation().targetReusePolicy());
        assertTrue(series.targetAllocation().withinSpellRange());
        assertTrue(claim.context().bindings().isEmpty());
        assertTrue(document.unsupported().isEmpty());
        assertTrue(document.interpretations().stream().noneMatch(value ->
                value.payload().getClass().getSimpleName().equals("AttackRollMechanic")));
        assertEquals("PENDING_REVIEW", claim.review().recordedStatus().name());
        assertEquals("NEEDS_REVIEW", claim.licensing().status().name());

        String json = MechanicsJson.mapper().writerFor(MechanicPayload.class).writeValueAsString(series);
        assertTrue(json.contains("\"kind\":\"DAMAGE_INSTANCE_SERIES\""));
        assertFalse(json.contains("attackRoll"));
        assertEquals(series, MechanicsJson.mapper().readValue(json, MechanicPayload.class));

        var damageAssociation = claim.evidenceAssociations().stream()
                .filter(value -> value.role() == EvidenceRole.EFFECT_ROLE).findFirst().orElseThrow();
        var damageEvidence = assertInstanceOf(TextMechanicsEvidence.class, document.evidence().stream()
                .filter(value -> value.id().equals(damageAssociation.evidenceId())).findFirst().orElseThrow());
        assertEquals("/spell/213/entries/0", damageEvidence.syntax().location().jsonPointer());
        assertEquals(134, damageEvidence.syntax().span().startInclusive());
        assertEquals(151, damageEvidence.syntax().span().endExclusive());
        var targetingAssociation = claim.evidenceAssociations().stream()
                .filter(value -> value.role() == EvidenceRole.SUBJECT).findFirst().orElseThrow();
        var targetingEvidence = (TextMechanicsEvidence) document.evidence().stream()
                .filter(value -> value.id().equals(targetingAssociation.evidenceId())).findFirst().orElseThrow();
        assertEquals("/spell/213/entries/0", targetingEvidence.syntax().location().jsonPointer());
        assertEquals(49, targetingEvidence.syntax().span().startInclusive());
        assertEquals(120, targetingEvidence.syntax().span().endExclusive());
        var scalingAssociation = claim.evidenceAssociations().stream()
                .filter(value -> value.role() == EvidenceRole.SCALING
                        && value.claimPath().equals("/payload/countProgression")).toList();
        assertEquals(2, scalingAssociation.size());
        var scalingText = (TextMechanicsEvidence) document.evidence().stream()
                .filter(value -> value.id().equals(scalingAssociation.getLast().evidenceId())).findFirst().orElseThrow();
        assertEquals("/spell/213/entriesHigherLevel/0/entries/0",
                scalingText.syntax().location().jsonPointer());
        assertEquals(86, scalingText.syntax().span().startInclusive());
        assertEquals(129, scalingText.syntax().span().endExclusive());
    }

    @Test
    void spellSlotProgressionCountsDartsWithoutScalingDamagePerDart() {
        var progression = new SpellSlotInstanceCount(1, 3, 1);
        assertEquals(3, progression.totalInstancesAtSlotLevel(1));
        assertEquals(4, progression.totalInstancesAtSlotLevel(2));
        assertEquals(5, progression.totalInstancesAtSlotLevel(3));
        assertEquals(6, progression.totalInstancesAtSlotLevel(4));
        assertEquals(11, progression.totalInstancesAtSlotLevel(9));
    }

    @Test
    void visibilityRequirementIsTypedAndAbsentFromLegacyAllocations() throws Exception {
        var legacy = new com.thedndpub.data.mechanics.model.InstanceTargetAllocation(
                TargetSelectionCardinality.EXACTLY_ONE_PER_INSTANCE,
                java.util.List.of(TargetCategory.CREATURE), TargetReusePolicy.REUSE_ALLOWED, true);
        String legacyJson = applicationMapper.writeValueAsString(legacy);
        assertFalse(legacyJson.contains("requirements"));
        assertFalse(legacyJson.contains("targetCategoryEligibilityStatus"));
    }
}
