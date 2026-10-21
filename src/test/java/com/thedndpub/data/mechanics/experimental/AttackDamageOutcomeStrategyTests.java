package com.thedndpub.data.mechanics.experimental;

import com.thedndpub.data.dto.spell.SpellId;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.mechanics.model.AttackActivationTiming;
import com.thedndpub.data.mechanics.model.AttackActorRole;
import com.thedndpub.data.mechanics.model.AttackKind;
import com.thedndpub.data.mechanics.model.AttackRollMechanic;
import com.thedndpub.data.mechanics.model.AttackRollOutcome;
import com.thedndpub.data.mechanics.model.DamageMechanic;
import com.thedndpub.data.mechanics.model.DiceAmount;
import com.thedndpub.data.mechanics.model.FixedDamageType;
import com.thedndpub.data.mechanics.model.MechanicContext;
import com.thedndpub.data.mechanics.model.SourceArtifactRevision;
import com.thedndpub.data.mechanics.model.UnknownSubject;
import com.thedndpub.data.mechanics.model.UnknownValueReason;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AttackDamageOutcomeStrategyTests {
    private static final SpellId OWNER = new SpellId("spell:phb:chill-touch");
    private static final SourceArtifactRevision REVISION = new SourceArtifactRevision(
            "5eTools v2.37.0", "data/spells/spells-phb.json", "a".repeat(64));
    private final AttackDamageOutcomeStrategy strategy = new AttackDamageOutcomeStrategy();

    @Test
    void attackExtractionRetainsOnlyExplicitTypeActorAndTiming() {
        SourceFragment unspecified = fragment("/entries/0", "Make a ranged spell attack against the target.");
        var extraction = new GenericMechanicStrategy(MechanicType.ATTACK).extract(
                candidate(MechanicType.ATTACK, unspecified), unspecified, null);
        assertTrue(extraction.supported());
        var attack = (AttackRollMechanic) extraction.payload();
        assertEquals(AttackKind.RANGED_SPELL, attack.attackKind());
        assertEquals(AttackActorRole.UNSPECIFIED, attack.actorRole());
        assertEquals(AttackActivationTiming.UNSPECIFIED, attack.activationTiming());

        SourceFragment explicit = fragment("/entries/1",
                "When you cast this spell, you make a melee spell attack.");
        var explicitAttack = (AttackRollMechanic) new GenericMechanicStrategy(MechanicType.ATTACK)
                .extract(candidate(MechanicType.ATTACK, explicit), explicit, null).payload();
        assertEquals(AttackActorRole.CASTER, explicitAttack.actorRole());
        assertEquals(AttackActivationTiming.CAST_TIME, explicitAttack.activationTiming());
    }

    @Test
    void explicitPluralAttackCountsAreIndependentAndUnclearGroupsAreRejected() {
        SourceFragment multiple = fragment("/entries/2", "You make three ranged spell attacks against targets.");
        var extraction = new GenericMechanicStrategy(MechanicType.ATTACK).extract(
                candidate(MechanicType.ATTACK, multiple), multiple, null);
        AttackRollMechanic attack = (AttackRollMechanic) extraction.payload();
        assertEquals(3, attack.attackCount());
        assertTrue(attack.attacksIndependent());

        SourceFragment projectiles = fragment("/entries/6", "You create three darts. Each dart targets a creature. "
                + "Make a ranged spell attack for each missile.");
        AttackRollMechanic projectileAttacks = (AttackRollMechanic) new GenericMechanicStrategy(MechanicType.ATTACK)
                .extract(candidate(MechanicType.ATTACK, projectiles), projectiles, null).payload();
        assertEquals(3, projectileAttacks.attackCount());
        assertTrue(projectileAttacks.attacksIndependent());
        assertEquals(AttackActorRole.CASTER, projectileAttacks.actorRole());

        SourceFragment unclear = fragment("/entries/3", "Make a ranged spell attack for each ray.");
        assertFalse(new GenericMechanicStrategy(MechanicType.ATTACK)
                .extract(candidate(MechanicType.ATTACK, unclear), unclear, null).supported());

        SourceFragment check = fragment("/entries/4", "The target makes a Dexterity ability check.");
        assertFalse(new GenericMechanicStrategy(MechanicType.ATTACK)
                .extract(candidate(MechanicType.ATTACK, check), check, null).supported());
    }

    @Test
    void explicitHitAndMissOutcomesBindDamageComponentsInTheirClauses() {
        SourceFragment fragment = fragment("/entries/0", "Make a ranged spell attack. On a hit, the target takes "
                + "{@damage 2d6} fire damage; on a miss, the target takes {@damage 1d6} fire damage.");
        ExtractedMechanic attack = attack("attack", fragment);
        ExtractedMechanic hitDamage = damageAtTag("hit", fragment, 2, "fire", "{@damage 2d6}");
        ExtractedMechanic missDamage = damageAtTag("miss", fragment, 1, "fire", "{@damage 1d6}");

        RelationshipResolutionResult result = resolve(List.of(attack, hitDamage, missDamage), List.of(fragment));

        assertEquals(2, result.relationships().size());
        assertEquals(List.of(new AttackDamageOutcomeRule(AttackRollOutcome.HIT,
                DamageApplicationBehavior.FULL_DAMAGE)), relationship(result, "hit").attackOutcomes());
        assertEquals(List.of(new AttackDamageOutcomeRule(AttackRollOutcome.MISS,
                DamageApplicationBehavior.FULL_DAMAGE)), relationship(result, "miss").attackOutcomes());
    }

    @Test
    void halfDamageOnMissAndMultipleComponentsRemainSeparateRules() {
        SourceFragment fragment = fragment("/entries/0", "The target takes {@damage 2d6} fire damage on a hit. "
                + "On a miss, it takes half damage, including {@damage 1d6} cold damage.");
        RelationshipResolutionResult result = resolve(List.of(attack("attack", fragment),
                damageAtTag("fire", fragment, 2, "fire", "{@damage 2d6}"),
                damageAtTag("cold", fragment, 1, "cold", "{@damage 1d6}")), List.of(fragment));

        assertEquals(DamageApplicationBehavior.HALF_DAMAGE,
                relationship(result, "cold").attackOutcomes().getFirst().application());
        assertEquals(2, result.relationships().size());
    }

    @Test
    void coOccurrenceAndAmbiguousAttackPairingDoNotCreateRelationships() {
        SourceFragment fragment = fragment("/entries/0", "Make a ranged spell attack and the target takes "
                + "{@damage 2d6} fire damage later.");
        RelationshipResolutionResult cooccurrence = resolve(List.of(attack("attack", fragment),
                damageAtTag("damage", fragment, 2, "fire", "{@damage 2d6}")), List.of(fragment));
        assertTrue(cooccurrence.relationships().isEmpty());
        assertTrue(cooccurrence.diagnostics().stream().anyMatch(item ->
                item.code().equals("ATTACK_DAMAGE_CO_OCCURRENCE_ONLY")));

        SourceFragment outcome = fragment("/entries/1", "On a hit, the target takes {@damage 2d6} fire damage.");
        RelationshipResolutionResult ambiguous = resolve(List.of(attack("first", fragment),
                attack("second", fragment), damageAtTag("damage", outcome, 2, "fire", "{@damage 2d6}")),
                List.of(fragment, outcome));
        assertTrue(ambiguous.relationships().isEmpty());
        assertTrue(ambiguous.diagnostics().stream().anyMatch(item ->
                item.code().equals("AMBIGUOUS_ATTACK_DAMAGE_PAIRING")));
    }

    @Test
    void attackAndSaveRelationshipsCanBeResolvedIndependently() {
        SourceFragment fragment = fragment("/entries/0", "On a hit, the target takes {@damage 2d6} fire damage. "
                + "The target must also make a Dexterity saving throw or take {@damage 1d6} cold damage.");
        RelationshipResolutionResult result = resolve(List.of(attack("attack", fragment),
                damageAtTag("fire", fragment, 2, "fire", "{@damage 2d6}"),
                damageAtTag("cold", fragment, 1, "cold", "{@damage 1d6}")),
                List.of(fragment));
        assertTrue(result.relationships().stream().anyMatch(item ->
                item.type() == MechanicRelationshipType.ATTACK_DAMAGE_OUTCOME));
    }

    @Test
    void laterConditionalAttackIsDiagnosticOnly() {
        SourceFragment fragment = fragment("/entries/0", "On a hit, the target takes {@damage 3d8} acid damage. "
                + "If two dice match, another attack can be made.");
        RelationshipResolutionResult result = resolve(List.of(attack("attack", fragment),
                damageAtTag("damage", fragment, 3, "acid", "{@damage 3d8}")), List.of(fragment));
        assertEquals(1, result.relationships().size());
        assertTrue(result.diagnostics().stream().anyMatch(item ->
                item.code().equals("CONDITIONAL_ATTACK_CHAIN_UNSUPPORTED")));
    }

    @Test
    void damageForALaterActionActivationIsNotBoundToTheInitialAttack() {
        SourceFragment fragment = fragment("/entries/5", "On a hit, the target takes {@damage 1d12} lightning damage, "
                + "and on each of your turns you can use your action to deal {@damage 1d12} lightning damage automatically.");
        RelationshipResolutionResult result = resolve(List.of(attack("attack", fragment),
                damageAtTag("initial", fragment, 1, "lightning", "{@damage 1d12}"),
                damageAtTagOccurrence("later", fragment, 1, "lightning", "{@damage 1d12}", 1)), List.of(fragment));
        assertEquals(1, result.relationships().size());
        assertEquals("initial", result.relationships().getFirst().targetMechanicId());
        assertTrue(result.diagnostics().stream().anyMatch(item ->
                item.code().equals("ATTACK_DAMAGE_SEPARATE_ACTIVATION_UNSUPPORTED")));
    }

    @Test
    void attackRelationshipIsTopLevelAndRejectsStaleSourceReferences() throws Exception {
        SourceFragment fragment = fragment("/entries/0", "On a hit, the target takes {@damage 2d6} fire damage.");
        ExtractedMechanic attack = attack("attack", fragment);
        ExtractedMechanic damage = damageAtTag("damage", fragment, 2, "fire", "{@damage 2d6}");
        ExperimentalMechanicRelationshipDto relation = resolve(List.of(attack, damage), List.of(fragment))
                .relationships().getFirst();
        var attackDto = new ExperimentalMechanicDto("attack", "ATTACK", attack.payload(), attack.context());
        var damageDto = new ExperimentalMechanicDto("damage", "DAMAGE", damage.payload(), damage.context());
        var evidence = new ExperimentalEvidenceDto(relation.evidenceIds().getFirst(), List.of("attack", "damage"),
                List.of(relation.id()), REVISION.resourcePath(), REVISION.datasetVersion(), REVISION.resourceSha256(),
                "/spells/0/entries/0", 0, fragment.text().length(), new SourceReference("PHB", null),
                "SUPPORTS_CANDIDATE", "attack-damage-outcome", "1");
        var metadata = new ExperimentalMechanicsMetadataDto(OWNER, "engine", REVISION.datasetVersion(),
                REVISION.resourcePath(), REVISION.resourceSha256(),
                com.thedndpub.data.mechanics.model.CoverageStatus.PARTIAL, "RECOGNIZED_FRAGMENTS_ONLY",
                "PENDING_REVIEW", "NEEDS_REVIEW", false, List.of(), List.of());
        var projection = new ExperimentalMechanicsV2Dto(2, "EXPERIMENTAL", "CANDIDATE_PARTIAL",
                List.of(attackDto, damageDto), List.of(relation), List.of(evidence), List.of(), metadata);
        var json = new ObjectMapper().valueToTree(projection);
        assertTrue(json.path("relationships").get(0).path("attackOutcomes").isArray());
        assertFalse(json.path("relationships").get(0).has("outcomes"));
        assertFalse(json.path("mechanics").get(0).has("evidence"));

        var stale = new ExperimentalMechanicRelationshipDto(relation.id(), relation.type(), OWNER,
                new SourceArtifactRevision(REVISION.datasetVersion(), REVISION.resourcePath(), "b".repeat(64)),
                "attack", "damage", List.of(), relation.evidenceIds(), relation.attackOutcomes());
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> new ExperimentalMechanicsV2Dto(2, "EXPERIMENTAL", "CANDIDATE_PARTIAL",
                        List.of(attackDto, damageDto), List.of(stale), List.of(evidence), List.of(), metadata));
    }

    private RelationshipResolutionResult resolve(List<ExtractedMechanic> mechanics, List<SourceFragment> fragments) {
        Map<String, SourceFragment> byId = fragments.stream()
                .collect(java.util.stream.Collectors.toMap(SourceFragment::id, item -> item));
        Map<String, String> evidence = fragments.stream().collect(java.util.stream.Collectors.toMap(
                SourceFragment::id, item -> "evidence-" + item.pointer()));
        return strategy.resolve(mechanics, new MechanicInterpretationContext(OWNER, REVISION, byId, evidence));
    }

    private static ExperimentalMechanicRelationshipDto relationship(RelationshipResolutionResult result, String damageId) {
        return result.relationships().stream().filter(item -> item.targetMechanicId().equals(damageId))
                .findFirst().orElseThrow();
    }

    private static ExtractedMechanic attack(String id, SourceFragment fragment) {
        return new ExtractedMechanic(id, MechanicType.ATTACK,
                new AttackRollMechanic(AttackKind.RANGED_SPELL, AttackActorRole.UNSPECIFIED, 1, false,
                        AttackActivationTiming.UNSPECIFIED), null, fragment.id());
    }

    private static ExtractedMechanic damage(String id, SourceFragment fragment, int dice, String type, int start, int end) {
        return new ExtractedMechanic(id, MechanicType.DAMAGE,
                new DamageMechanic(new DiceAmount(dice, 6, 0), new FixedDamageType(type)),
                new MechanicContext(new UnknownSubject(UnknownValueReason.NOT_INTERPRETED), List.of()),
                fragment.id(), start, end);
    }

    private static ExtractedMechanic damageAtTag(String id, SourceFragment fragment, int dice, String type, String tag) {
        int start = fragment.text().indexOf(tag);
        return damage(id, fragment, dice, type, start, start + tag.length());
    }

    private static ExtractedMechanic damageAtTagOccurrence(
            String id, SourceFragment fragment, int dice, String type, String tag, int occurrence) {
        int start = fragment.text().indexOf(tag);
        for (int index = 0; index < occurrence; index++) start = fragment.text().indexOf(tag, start + tag.length());
        return damage(id, fragment, dice, type, start, start + tag.length());
    }

    private static SourceFragment fragment(String pointer, String text) {
        return new SourceFragment(OWNER.value() + "#" + pointer, pointer, SourceFragment.Kind.AUTHORED_TEXT,
                text, List.of("entries"), List.of(MechanicType.ATTACK, MechanicType.DAMAGE));
    }

    private static MechanicCandidate candidate(MechanicType type, SourceFragment fragment) {
        return new MechanicCandidate(type, fragment.id(), "test", "1", List.of(fragment.pointer()),
                "entries", false);
    }
}
