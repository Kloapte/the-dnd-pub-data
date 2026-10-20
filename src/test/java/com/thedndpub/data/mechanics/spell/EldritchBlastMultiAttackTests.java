package com.thedndpub.data.mechanics.spell;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.mechanics.model.AppliesOnEachAttackRollOutcome;
import com.thedndpub.data.mechanics.model.AttackActivationTiming;
import com.thedndpub.data.mechanics.model.AttackActorRole;
import com.thedndpub.data.mechanics.model.AttackKind;
import com.thedndpub.data.mechanics.model.AttackRollInstanceScope;
import com.thedndpub.data.mechanics.model.AttackRollMechanic;
import com.thedndpub.data.mechanics.model.AttackRollOutcome;
import com.thedndpub.data.mechanics.model.CharacterLevelInstanceCount;
import com.thedndpub.data.mechanics.model.CharacterLevelInstanceCountTier;
import com.thedndpub.data.mechanics.model.DamageMechanic;
import com.thedndpub.data.mechanics.model.DiceAmount;
import com.thedndpub.data.mechanics.model.FixedDamageType;
import com.thedndpub.data.mechanics.model.InstanceTargetAllocation;
import com.thedndpub.data.mechanics.model.MechanicContext;
import com.thedndpub.data.mechanics.model.MechanicRecord;
import com.thedndpub.data.mechanics.model.MechanicId;
import com.thedndpub.data.mechanics.model.MechanicsDocument;
import com.thedndpub.data.mechanics.model.MechanicsJson;
import com.thedndpub.data.mechanics.model.MechanicRevisionReference;
import com.thedndpub.data.mechanics.model.SpellTargetSubject;
import com.thedndpub.data.mechanics.model.TargetCategory;
import com.thedndpub.data.mechanics.model.TargetReusePolicy;
import com.thedndpub.data.mechanics.model.TargetScope;
import com.thedndpub.data.mechanics.model.TargetSelectionCardinality;
import com.thedndpub.data.mechanics.model.TextMechanicsEvidence;
import com.thedndpub.data.mechanics.model.ProcessingState;
import com.thedndpub.data.mechanics.review.MechanicsReviewStatus;
import com.thedndpub.data.services.SpellCatalog;
import com.thedndpub.data.services.SpellMechanicsInspectionCatalog;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
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
class EldritchBlastMultiAttackTests {
    private static final String OWNER_ID = "spell:phb:eldritch-blast";

    @Autowired
    private SpellMechanicsInspectionCatalog inspection;
    @Autowired
    private SpellCatalog spells;
    @Autowired
    private ObjectMapper mapper;

    private MechanicsDocument document;
    private MechanicRecord attack;
    private MechanicRecord damage;

    @BeforeAll
    void loadExtractedOwner() {
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
    void extractionCapturesIndependentBeamCountsAndOneCreatureTargetPerBeam() {
        AttackRollMechanic payload = assertInstanceOf(AttackRollMechanic.class, attack.payload());
        assertEquals("mechanic:spell:phb:eldritch-blast:attack-roll", attack.id().value());
        assertEquals(1, attack.revision().number());
        assertEquals(AttackKind.RANGED_SPELL, payload.attackKind());
        assertEquals(AttackActorRole.CASTER, payload.actorRole());
        assertEquals(1, payload.attackCount());
        assertTrue(payload.attacksIndependent());
        assertEquals(AttackActivationTiming.CAST_TIME, payload.activationTiming());
        CharacterLevelInstanceCount count = assertInstanceOf(
                CharacterLevelInstanceCount.class, payload.countProgression());
        assertEquals(List.of(new CharacterLevelInstanceCountTier(5, 2),
                        new CharacterLevelInstanceCountTier(11, 3),
                        new CharacterLevelInstanceCountTier(17, 4)), count.tiers());
        assertEquals(TargetSelectionCardinality.EXACTLY_ONE_PER_INSTANCE,
                payload.targetAllocation().cardinality());
        assertEquals(List.of(TargetCategory.CREATURE), payload.targetAllocation().eligibleTargetCategories());
        assertEquals(TargetReusePolicy.REUSE_ALLOWED, payload.targetAllocation().targetReusePolicy());
        assertTrue(payload.targetAllocation().withinSpellRange());
        assertEquals(TargetScope.SINGLE_TARGET_CREATURE,
                assertInstanceOf(SpellTargetSubject.class, attack.context().subject()).scope());
        assertTrue(attack.context().bindings().isEmpty());
        assertEquals(TargetReusePolicy.DISTINCT_TARGETS_ONLY,
                new InstanceTargetAllocation(TargetSelectionCardinality.EXACTLY_ONE_PER_INSTANCE,
                        List.of(TargetCategory.CREATURE), TargetReusePolicy.DISTINCT_TARGETS_ONLY, true)
                        .targetReusePolicy());
    }

    @Test
    void levelBoundariesRepresentTotalBeamCounts() {
        CharacterLevelInstanceCount count = (CharacterLevelInstanceCount)
                ((AttackRollMechanic) attack.payload()).countProgression();
        assertEquals(1, totalAtLevel(1, count));
        assertEquals(1, totalAtLevel(4, count));
        assertEquals(2, totalAtLevel(5, count));
        assertEquals(2, totalAtLevel(10, count));
        assertEquals(3, totalAtLevel(11, count));
        assertEquals(3, totalAtLevel(16, count));
        assertEquals(4, totalAtLevel(17, count));
    }

    @Test
    void eachHitBindsOneDamageInstanceToItsOwnBeamAndTarget() {
        assertEquals("mechanic:spell:phb:eldritch-blast:damage", damage.id().value());
        assertEquals(1, damage.revision().number());
        DamageMechanic payload = assertInstanceOf(DamageMechanic.class, damage.payload());
        assertEquals(new DiceAmount(1, 10, 0), payload.amount());
        assertEquals(new FixedDamageType("force"), payload.damageType());
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
    void sourceEvidenceCoversCountsAllocationAttackIndependenceAndDamage() {
        assertTextEvidence(attack, "/spell/113/entries/0", "Make a ranged spell attack against the target",
                "/payload");
        assertTextEvidence(attack, "/spell/113/entries/0", "A beam", "/payload/attackCount");
        assertTextEvidence(attack, "/spell/113/entries/1",
                "two beams at 5th level, three beams at 11th level, and four beams at 17th level",
                "/payload/countProgression/tiers/0");
        assertTextEvidence(attack, "/spell/113/entries/1",
                "You can direct the beams at the same target or at different ones",
                "/payload/targetAllocation/targetReusePolicy");
        assertTextEvidence(attack, "/spell/113/entries/1",
                "Make a separate attack roll for each beam", "/payload/attacksIndependent");
        assertTextEvidence(damage, "/spell/113/entries/0", "{@damage 1d10}", "/payload/amount");
        assertTextEvidence(damage, "/spell/113/entries/0", "On a hit", "/context/bindings/0");
        assertEquals("c9e54cce55e7ce0fc988feb1b604bf505faa5c042635542fa6b6ed36724e297a",
                document.sources().getFirst().resourceSha256());
        assertEquals(ProcessingState.PROCESSED, document.processing().state());
        assertEquals(2, document.interpretations().size());
        assertTrue(document.unsupported().isEmpty());
        assertEquals(MechanicsReviewStatus.PENDING_REVIEW, attack.review().recordedStatus());
        assertEquals(MechanicsReviewStatus.PENDING_REVIEW, damage.review().recordedStatus());
        assertEquals(0, document.reviewSummary().verified());
        assertTrue(document.interpretations().stream().allMatch(value ->
                value.licensing().status().name().equals("NEEDS_REVIEW")));
    }

    @Test
    void canonicalDocumentSerializesDeterministicallyAndRoundTrips() throws Exception {
        byte[] first = MechanicsJson.mapper().writeValueAsBytes(document);
        byte[] second = MechanicsJson.mapper().writeValueAsBytes(document);
        assertArrayEquals(first, second);
        assertEquals(document, MechanicsJson.mapper().readValue(first, MechanicsDocument.class));
    }

    @Test
    void invalidCountProgressionsAndTargetAllocationsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new CharacterLevelInstanceCount(List.of()));
        assertThrows(IllegalArgumentException.class,
                () -> new CharacterLevelInstanceCountTier(1, 2));
        assertThrows(IllegalArgumentException.class,
                () -> new CharacterLevelInstanceCountTier(5, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new CharacterLevelInstanceCount(List.of(
                        new CharacterLevelInstanceCountTier(5, 2),
                        new CharacterLevelInstanceCountTier(5, 3))));
        assertThrows(IllegalArgumentException.class,
                () -> new CharacterLevelInstanceCount(List.of(
                        new CharacterLevelInstanceCountTier(5, 2),
                        new CharacterLevelInstanceCountTier(11, 2))));
        assertThrows(IllegalArgumentException.class, () -> new AttackRollMechanic(
                AttackKind.RANGED_SPELL, AttackActorRole.CASTER, 1, false, AttackActivationTiming.CAST_TIME,
                new CharacterLevelInstanceCount(List.of(new CharacterLevelInstanceCountTier(5, 2))), null));
        assertThrows(IllegalArgumentException.class, () -> new AttackRollMechanic(
                AttackKind.RANGED_SPELL, AttackActorRole.CASTER, 2, true, AttackActivationTiming.CAST_TIME,
                new CharacterLevelInstanceCount(List.of(new CharacterLevelInstanceCountTier(5, 2))), null));
        assertThrows(IllegalArgumentException.class, () -> new InstanceTargetAllocation(
                TargetSelectionCardinality.EXACTLY_ONE_PER_INSTANCE, List.of(),
                TargetReusePolicy.REUSE_ALLOWED, true));
        assertThrows(IllegalArgumentException.class, () -> new InstanceTargetAllocation(
                TargetSelectionCardinality.EXACTLY_ONE_PER_INSTANCE,
                List.of(TargetCategory.CREATURE, TargetCategory.CREATURE),
                TargetReusePolicy.REUSE_ALLOWED, true));
        assertThrows(IllegalArgumentException.class, () -> new AttackRollMechanic(
                AttackKind.RANGED_SPELL, AttackActorRole.CASTER, 1, true, AttackActivationTiming.CAST_TIME,
                null, new InstanceTargetAllocation(TargetSelectionCardinality.EXACTLY_ONE_PER_INSTANCE,
                        List.of(TargetCategory.CREATURE), TargetReusePolicy.DISTINCT_TARGETS_ONLY, true)));
        assertThrows(IllegalArgumentException.class, () -> replaceAttackAllocation(
                new InstanceTargetAllocation(TargetSelectionCardinality.EXACTLY_ONE_PER_INSTANCE,
                        List.of(TargetCategory.OBJECT), TargetReusePolicy.REUSE_ALLOWED, true)));
    }

    @Test
    void legacyAttackPayloadSerializationOmitsNewOptionalFields() throws Exception {
        AttackRollMechanic legacy = new AttackRollMechanic(AttackKind.RANGED_SPELL, AttackActorRole.CASTER,
                1, true, AttackActivationTiming.CAST_TIME);
        String json = mapper.writeValueAsString(legacy);
        assertFalse(json.contains("countProgression"));
        assertFalse(json.contains("targetAllocation"));
        assertEquals(legacy, mapper.readValue(json, AttackRollMechanic.class));
    }

    @Test
    void damageDependencyRejectsStaleHashesAndNonAttackClaimReferences() {
        AppliesOnEachAttackRollOutcome valid = (AppliesOnEachAttackRollOutcome) damage.context().bindings().getFirst();
        MechanicRevisionReference stale = new MechanicRevisionReference(valid.attackRoll().id(),
                valid.attackRoll().number(), "0".repeat(64));
        assertThrows(IllegalArgumentException.class, () -> replaceDamageBinding(
                new AppliesOnEachAttackRollOutcome(stale, AttackRollOutcome.HIT,
                        AttackRollInstanceScope.CORRESPONDING_ATTACK_INSTANCE, valid.evidenceIds())));

        assertThrows(IllegalArgumentException.class, () -> replaceDamageBinding(
                new AppliesOnEachAttackRollOutcome(damage.revision().reference(damage.id()), AttackRollOutcome.HIT,
                        AttackRollInstanceScope.CORRESPONDING_ATTACK_INSTANCE, valid.evidenceIds())));

        MechanicRevisionReference foreignOwner = new MechanicRevisionReference(
                new MechanicId("mechanic:spell:phb:ray-of-frost:attack-roll"), 1, "0".repeat(64));
        assertThrows(IllegalArgumentException.class, () -> replaceDamageBinding(
                new AppliesOnEachAttackRollOutcome(foreignOwner, AttackRollOutcome.HIT,
                        AttackRollInstanceScope.CORRESPONDING_ATTACK_INSTANCE, valid.evidenceIds())));
    }

    private void replaceDamageBinding(AppliesOnEachAttackRollOutcome binding) {
        MechanicContext context = new MechanicContext(damage.context().subject(), List.of(binding));
        MechanicRecord revisedContent = MechanicRecord.propose(damage.id(), damage.payload(), context,
                damage.evidenceAssociations(), damage.derivation(), damage.assessment());
        List<MechanicRecord> claims = new ArrayList<>(document.interpretations());
        claims.removeIf(record -> record.id().equals(damage.id()));
        claims.add(revisedContent);
        MechanicsDocument.create(document.owner(), 1, document.sources(), document.processing(), claims,
                document.observations(), document.unsupported(), document.evidence(), document.coverage());
    }

    private void replaceAttackAllocation(InstanceTargetAllocation allocation) {
        AttackRollMechanic old = (AttackRollMechanic) attack.payload();
        AttackRollMechanic payload = new AttackRollMechanic(old.attackKind(), old.actorRole(), old.attackCount(),
                old.attacksIndependent(), old.activationTiming(), old.countProgression(), allocation);
        MechanicRecord replacement = MechanicRecord.propose(attack.id(), payload, attack.context(),
                attack.evidenceAssociations(), attack.derivation(), attack.assessment());
        List<MechanicRecord> claims = new ArrayList<>(document.interpretations());
        claims.removeIf(record -> record.id().equals(attack.id()));
        claims.add(replacement);
        MechanicsDocument.create(document.owner(), 1, document.sources(), document.processing(), claims,
                document.observations(), document.unsupported(), document.evidence(), document.coverage());
    }

    private int totalAtLevel(int level, CharacterLevelInstanceCount progression) {
        int total = 1;
        for (CharacterLevelInstanceCountTier tier : progression.tiers()) {
            if (level < tier.minimumCharacterLevel()) {
                break;
            }
            total = tier.totalInstances();
        }
        return total;
    }

    private void assertTextEvidence(MechanicRecord claim, String pointer, String text, String claimPath) {
        var association = claim.evidenceAssociations().stream()
                .filter(value -> value.claimPath().equals(claimPath))
                .findFirst().orElseThrow();
        TextMechanicsEvidence evidence = assertInstanceOf(TextMechanicsEvidence.class, document.evidence().stream()
                .filter(value -> value.id().equals(association.evidenceId()))
                .findFirst().orElseThrow());
        assertEquals(pointer, evidence.syntax().location().jsonPointer());
        String sourceText = spells.findById(OWNER_ID).entries().stream()
                .filter(entry -> entry instanceof com.thedndpub.data.dte.common.entry.TextEntryDte)
                .map(entry -> ((com.thedndpub.data.dte.common.entry.TextEntryDte) entry).text())
                .toList().get(pointer.endsWith("/entries/0") ? 0 : 1);
        int start = sourceText.indexOf(text);
        assertTrue(start >= 0, "Source phrase missing: " + text);
        assertEquals(start, evidence.syntax().span().startInclusive());
        assertEquals(start + text.length(), evidence.syntax().span().endExclusive());
    }
}
