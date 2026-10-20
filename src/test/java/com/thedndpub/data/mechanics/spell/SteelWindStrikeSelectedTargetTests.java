package com.thedndpub.data.mechanics.spell;

import com.thedndpub.data.mechanics.inspection.SpellMechanicsInspectionMapper;
import com.thedndpub.data.mechanics.model.AppliesOnEachAttackRollOutcome;
import com.thedndpub.data.mechanics.model.AttackActivationTiming;
import com.thedndpub.data.mechanics.model.AttackActorRole;
import com.thedndpub.data.mechanics.model.AttackKind;
import com.thedndpub.data.mechanics.model.AttackRollInstanceScope;
import com.thedndpub.data.mechanics.model.AttackRollMechanic;
import com.thedndpub.data.mechanics.model.AttackRollOutcome;
import com.thedndpub.data.mechanics.model.CharacterLevelInstanceCount;
import com.thedndpub.data.mechanics.model.CoverageStatus;
import com.thedndpub.data.mechanics.model.DamageMechanic;
import com.thedndpub.data.mechanics.model.DiceAmount;
import com.thedndpub.data.mechanics.model.FixedDamageType;
import com.thedndpub.data.mechanics.model.InstanceTargetAllocation;
import com.thedndpub.data.mechanics.model.InstanceTargetRequirement;
import com.thedndpub.data.mechanics.model.MechanicContext;
import com.thedndpub.data.mechanics.model.MechanicRecord;
import com.thedndpub.data.mechanics.model.MechanicsCoverage;
import com.thedndpub.data.mechanics.model.MechanicsDocument;
import com.thedndpub.data.mechanics.model.MechanicsHashes;
import com.thedndpub.data.mechanics.model.MechanicsJson;
import com.thedndpub.data.mechanics.model.MechanicsProcessing;
import com.thedndpub.data.mechanics.model.SelectedTargetCount;
import com.thedndpub.data.mechanics.model.SpellTargetSubject;
import com.thedndpub.data.mechanics.model.TargetCategory;
import com.thedndpub.data.mechanics.model.TargetCategoryEligibilityStatus;
import com.thedndpub.data.mechanics.model.TargetReusePolicy;
import com.thedndpub.data.mechanics.model.TargetScope;
import com.thedndpub.data.mechanics.model.TargetSelectionCardinality;
import com.thedndpub.data.mechanics.model.TextMechanicsEvidence;
import com.thedndpub.data.mechanics.model.UnsupportedReason;
import com.thedndpub.data.mechanics.review.MechanicsReviewStatus;
import com.thedndpub.data.services.SpellMechanicsInspectionCatalog;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SteelWindStrikeSelectedTargetTests {
    private static final String OWNER = "spell:xge:steel-wind-strike";

    @Autowired private SpellMechanicsInspectionCatalog inspection;

    private MechanicsDocument document;
    private MechanicRecord attack;
    private MechanicRecord damage;

    @BeforeAll
    void loadOwner() {
        document = inspection.documents().stream().filter(value -> value.owner().id().equals(OWNER))
                .findFirst().orElseThrow();
        attack = document.interpretations().stream()
                .filter(value -> value.id().value().endsWith(":attack-roll")).findFirst().orElseThrow();
        damage = document.interpretations().stream()
                .filter(value -> value.id().value().endsWith(":damage")).findFirst().orElseThrow();
    }

    @Test
    void attackCountIsDerivedFromUpToFiveDistinctVisibleCreatures() {
        AttackRollMechanic payload = assertInstanceOf(AttackRollMechanic.class, attack.payload());
        assertEquals("mechanic:spell:xge:steel-wind-strike:attack-roll", attack.id().value());
        assertEquals(2, document.interpretations().size());
        assertEquals(1, attack.revision().number());
        assertEquals(AttackKind.MELEE_SPELL, payload.attackKind());
        assertEquals(AttackActorRole.CASTER, payload.actorRole());
        assertNull(payload.attackCount(), "No source-authored minimum attack count is established");
        assertNull(payload.countProgression());
        assertEquals(new SelectedTargetCount(5), payload.selectedTargetCount());
        assertTrue(payload.attacksIndependent());
        assertEquals(AttackActivationTiming.CAST_TIME, payload.activationTiming());
        assertEquals(TargetScope.MULTIPLE_TARGET_CREATURES,
                assertInstanceOf(SpellTargetSubject.class, attack.context().subject()).scope());
        assertEquals(5, ((SpellTargetSubject) attack.context().subject()).maximumTargets());
        assertEquals(TargetSelectionCardinality.EXACTLY_ONE_PER_INSTANCE,
                payload.targetAllocation().cardinality());
        assertEquals(List.of(TargetCategory.CREATURE), payload.targetAllocation().eligibleTargetCategories());
        assertEquals(TargetCategoryEligibilityStatus.SOURCE_SPECIFIED,
                payload.targetAllocation().targetCategoryEligibilityStatus());
        assertEquals(TargetReusePolicy.DISTINCT_TARGETS_ONLY,
                payload.targetAllocation().targetReusePolicy());
        assertTrue(payload.targetAllocation().withinSpellRange());
        assertEquals(List.of(InstanceTargetRequirement.VISIBLE_TO_CASTER),
                payload.targetAllocation().requirements());
    }

    @Test
    void oneHitAppliesOneDamageInstanceOnlyToItsCorrespondingTarget() {
        DamageMechanic payload = assertInstanceOf(DamageMechanic.class, damage.payload());
        assertEquals("mechanic:spell:xge:steel-wind-strike:damage", damage.id().value());
        assertEquals(1, damage.revision().number());
        assertEquals(new DiceAmount(6, 10, 0), payload.amount());
        assertEquals(new FixedDamageType("force"), payload.damageType());
        assertNull(payload.characterLevelScaling());
        AppliesOnEachAttackRollOutcome binding = assertInstanceOf(
                AppliesOnEachAttackRollOutcome.class, damage.context().bindings().getFirst());
        assertEquals(attack.revision().reference(attack.id()), binding.attackRoll());
        assertEquals(AttackRollOutcome.HIT, binding.outcome());
        assertEquals(AttackRollInstanceScope.CORRESPONDING_ATTACK_INSTANCE, binding.instanceScope());
        assertEquals(attack.context().subject(), damage.context().subject());
        assertEquals(1, damage.context().bindings().size());
        assertEquals(MechanicsReviewStatus.PENDING_REVIEW, attack.review().recordedStatus());
        assertEquals(MechanicsReviewStatus.PENDING_REVIEW, damage.review().recordedStatus());
        assertEquals(MechanicsReviewStatus.PENDING_REVIEW, attack.review().effectiveStatus());
        assertEquals(MechanicsReviewStatus.PENDING_REVIEW, damage.review().effectiveStatus());
        assertEquals("NEEDS_REVIEW", attack.licensing().status().name());
        assertEquals("NEEDS_REVIEW", damage.licensing().status().name());
        assertEquals(0, document.reviewSummary().verified());
    }

    @Test
    void sourceEvidenceUsesPinnedXgePointersAndExactUtf16Spans() {
        assertEquals("17e7e9fe7ad20c7df094d2a4929558c5d2a331f7e1498c7575181864ec5dc1dc",
                document.sources().getFirst().resourceSha256());
        assertEvidence(attack, "/spell/72/entries/0", 85, 138,
                "/payload/selectedTargetCount/maximumSelectedTargets");
        assertEvidence(attack, "/spell/72/entries/0", 139, 185,
                "/payload/attacksIndependent");
        assertEvidence(attack, "/spell/72/entries/0", 139, 185,
                "/payload/selectedTargetCount");
        assertEvidence(attack, "/spell/72/entries/0", 0, 43,
                "/payload/actorRole");
        assertEvidence(attack, "/spell/72/entries/0", 85, 138,
                "/payload/targetAllocation/requirements/0");
        assertEvidence(damage, "/spell/72/entries/0", 211, 225,
                "/payload/amount");
        assertEvidence(damage, "/spell/72/entries/0", 186, 194,
                "/context/bindings/0");
        assertTrue(attack.revision().contentSha256().equals(
                MechanicsHashes.claimHash(new com.thedndpub.data.mechanics.model.MechanicRevisionContent(
                        attack.id(), attack.payload(), attack.context(), attack.evidenceAssociations(),
                        attack.derivation(), attack.assessment()))));
        assertTrue(damage.revision().contentSha256().equals(
                MechanicsHashes.claimHash(new com.thedndpub.data.mechanics.model.MechanicRevisionContent(
                        damage.id(), damage.payload(), damage.context(), damage.evidenceAssociations(),
                        damage.derivation(), damage.assessment()))));
    }

    @Test
    void teleportationIsRetainedAsAnEvidenceBackedUnsupportedEffect() {
        assertEquals(1, document.unsupported().size());
        var unsupported = document.unsupported().getFirst();
        assertEquals("steel-wind-strike-teleportation", unsupported.id());
        assertEquals(UnsupportedReason.FAMILY_NOT_IMPLEMENTED, unsupported.reason());
        TextMechanicsEvidence evidence = (TextMechanicsEvidence) document.evidence().stream()
                .filter(value -> value.id().equals(unsupported.evidenceIds().getFirst()))
                .findFirst().orElseThrow();
        assertEquals("/spell/72/entries/1", evidence.syntax().location().jsonPointer());
        assertEquals(0, evidence.syntax().span().startInclusive());
        assertEquals(111, evidence.syntax().span().endExclusive());
        assertEquals("PARTIAL", document.coverage().status().name());
    }

    @Test
    void selectedTargetCountCannotContradictFixedCountsOrAllocationRules() {
        var validAllocation = allocation(TargetReusePolicy.DISTINCT_TARGETS_ONLY,
                List.of(InstanceTargetRequirement.VISIBLE_TO_CASTER));
        assertThrows(IllegalArgumentException.class, () -> new SelectedTargetCount(0));
        assertThrows(IllegalArgumentException.class, () -> new AttackRollMechanic(
                AttackKind.MELEE_SPELL, AttackActorRole.CASTER, 5, true, AttackActivationTiming.CAST_TIME,
                null, validAllocation, new SelectedTargetCount(5)));
        assertThrows(IllegalArgumentException.class, () -> new AttackRollMechanic(
                AttackKind.MELEE_SPELL, AttackActorRole.CASTER, null, true, AttackActivationTiming.CAST_TIME,
                new CharacterLevelInstanceCount(List.of(
                        new com.thedndpub.data.mechanics.model.CharacterLevelInstanceCountTier(5, 2))),
                validAllocation, new SelectedTargetCount(5)));
        assertThrows(IllegalArgumentException.class, () -> new AttackRollMechanic(
                AttackKind.MELEE_SPELL, AttackActorRole.CASTER, null, true, AttackActivationTiming.CAST_TIME,
                null, allocation(TargetReusePolicy.REUSE_ALLOWED,
                        List.of(InstanceTargetRequirement.VISIBLE_TO_CASTER)), new SelectedTargetCount(5)));
        assertThrows(IllegalArgumentException.class, () -> new AttackRollMechanic(
                AttackKind.MELEE_SPELL, AttackActorRole.CASTER, null, true, AttackActivationTiming.CAST_TIME,
                null, allocation(TargetReusePolicy.DISTINCT_TARGETS_ONLY, List.of()), new SelectedTargetCount(5)));
        assertThrows(IllegalArgumentException.class, () -> new AttackRollMechanic(
                AttackKind.MELEE_SPELL, AttackActorRole.CASTER, null, true, AttackActivationTiming.CAST_TIME,
                null, new InstanceTargetAllocation(TargetSelectionCardinality.EXACTLY_ONE_PER_INSTANCE,
                        List.of(TargetCategory.CREATURE), TargetReusePolicy.DISTINCT_TARGETS_ONLY, false,
                        TargetCategoryEligibilityStatus.SOURCE_SPECIFIED,
                        List.of(InstanceTargetRequirement.VISIBLE_TO_CASTER)), new SelectedTargetCount(5)));
        assertThrows(IllegalArgumentException.class, () -> new AttackRollMechanic(
                AttackKind.MELEE_SPELL, AttackActorRole.CASTER, null, true, AttackActivationTiming.CAST_TIME,
                null, new InstanceTargetAllocation(TargetSelectionCardinality.EXACTLY_ONE_PER_INSTANCE,
                        List.of(TargetCategory.OBJECT), TargetReusePolicy.DISTINCT_TARGETS_ONLY, true,
                        TargetCategoryEligibilityStatus.SOURCE_SPECIFIED,
                        List.of(InstanceTargetRequirement.VISIBLE_TO_CASTER)), new SelectedTargetCount(5)));
    }

    @Test
    void documentValidationRejectsTargetMaximumThatDisagreesWithAttackCountMode() {
        SpellTargetSubject wrongMaximum = new SpellTargetSubject(
                TargetScope.MULTIPLE_TARGET_CREATURES, null, 4);
        MechanicRecord inconsistent = MechanicRecord.propose(attack.id(), attack.payload(),
                new MechanicContext(wrongMaximum, List.of()), attack.evidenceAssociations(),
                attack.derivation(), attack.assessment());
        MechanicsCoverage coverage = new MechanicsCoverage(CoverageStatus.PARTIAL,
                document.coverage().scopeId(), document.coverage().scopeVersion(),
                document.coverage().consideredSourcePaths(), List.of(), document.unsupported().size());
        assertThrows(IllegalArgumentException.class, () -> MechanicsDocument.create(
                document.owner(), 2, document.sources(), document.processing(), List.of(inconsistent),
                document.observations(), document.unsupported(), document.evidence(), coverage));
    }

    @Test
    void optionalSelectedTargetModeDoesNotChangeExistingAttackSerialization() throws Exception {
        var mapper = MechanicsJson.mapper();
        var tree = mapper.readTree(mapper.writeValueAsBytes(new AttackRollMechanic(
                AttackKind.RANGED_SPELL, AttackActorRole.CASTER, 1, true,
                AttackActivationTiming.CAST_TIME)));
        assertEquals(1, tree.path("attackCount").intValue());
        assertFalse(tree.has("selectedTargetCount"));
        assertTrue(tree.has("attackCount"));
        var selectedTree = mapper.valueToTree(attack.payload());
        assertFalse(selectedTree.has("attackCount"));
        assertEquals(5, selectedTree.path("selectedTargetCount").path("maximumSelectedTargets").intValue());
        assertEquals(969, inspection.size());
        assertEquals("PRIVATE_INSPECTION",
                inspection.findById(new com.thedndpub.data.dto.spell.SpellId(OWNER)).projection().name());
    }

    private InstanceTargetAllocation allocation(
            TargetReusePolicy reusePolicy, List<InstanceTargetRequirement> requirements) {
        return new InstanceTargetAllocation(TargetSelectionCardinality.EXACTLY_ONE_PER_INSTANCE,
                List.of(TargetCategory.CREATURE), reusePolicy, true,
                TargetCategoryEligibilityStatus.SOURCE_SPECIFIED, requirements);
    }

    private void assertEvidence(MechanicRecord record, String pointer, int start, int end, String path) {
        var association = record.evidenceAssociations().stream()
                .filter(value -> value.claimPath().equals(path)).findFirst().orElseThrow();
        TextMechanicsEvidence evidence = (TextMechanicsEvidence) document.evidence().stream()
                .filter(value -> value.id().equals(association.evidenceId())).findFirst().orElseThrow();
        assertEquals(pointer, evidence.syntax().location().jsonPointer());
        assertEquals(start, evidence.syntax().span().startInclusive());
        assertEquals(end, evidence.syntax().span().endExclusive());
    }
}
