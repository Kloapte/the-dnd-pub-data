package com.thedndpub.data.mechanics.experimental;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.source.SourceIdentity;
import com.thedndpub.data.dto.spell.SpellDefinition;
import com.thedndpub.data.dto.spell.SpellId;
import com.thedndpub.data.dto.spell.SpellIdentity;
import com.thedndpub.data.dto.spell.SpellImportMetadata;
import com.thedndpub.data.dto.spell.SpellMechanics;
import com.thedndpub.data.dto.spell.SpellScaling;
import com.thedndpub.data.dto.spell.SpellScalingValue;
import com.thedndpub.data.dto.spell.SpellSourceRelationships;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.mechanics.model.AttackActivationTiming;
import com.thedndpub.data.mechanics.model.AttackActorRole;
import com.thedndpub.data.mechanics.model.AttackKind;
import com.thedndpub.data.mechanics.model.AttackRollMechanic;
import com.thedndpub.data.mechanics.model.DamageMechanic;
import com.thedndpub.data.mechanics.model.DiceAmount;
import com.thedndpub.data.mechanics.model.FixedDamageType;
import com.thedndpub.data.mechanics.model.GenericScalingMechanic;
import com.thedndpub.data.mechanics.model.HealingMechanic;
import com.thedndpub.data.mechanics.model.HealingMode;
import com.thedndpub.data.mechanics.model.IntegerAmount;
import com.thedndpub.data.mechanics.model.MechanicContext;
import com.thedndpub.data.mechanics.model.ScalingAxis;
import com.thedndpub.data.mechanics.model.ScalingProgressionType;
import com.thedndpub.data.mechanics.model.ScalingQuantity;
import com.thedndpub.data.mechanics.model.ScalingStep;
import com.thedndpub.data.mechanics.model.SourceArtifactRevision;
import com.thedndpub.data.mechanics.model.SpellTargetSubject;
import com.thedndpub.data.mechanics.model.TargetScope;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GenericScalingStrategiesTests {
    private static final SpellId OWNER = new SpellId("spell:phb:test-scaling");
    private static final SourceArtifactRevision REVISION = new SourceArtifactRevision(
            "5eTools v2.37.0", "data/spells/spells-phb.json", "a".repeat(64));
    private final GenericScalingExtractionStrategy extraction = new GenericScalingExtractionStrategy();

    @Test
    void slotDamageAndHealingIncrementsRetainTheirAxisAndThreshold() {
        var damage = extract("When cast using a spell slot, the fire damage increases by "
                + "{@scaledamage 8d6|3-9|1d6} for each spell slot level above 3rd.", null);
        var damageScaling = assertInstanceOf(GenericScalingMechanic.class, damage.payload());
        assertEquals(ScalingAxis.SPELL_SLOT_LEVEL, damageScaling.axis());
        assertEquals(ScalingQuantity.DAMAGE_AMOUNT, damageScaling.quantity());
        assertEquals(3, damageScaling.baseLevel());
        assertEquals(new ScalingStep(4, new DiceAmount(1, 6, 0)), damageScaling.steps().getFirst());

        var healing = extract("When you cast this spell using a spell slot of 2nd level or higher, "
                + "the healing increases by {@scaledice 1d4|1-9|1d4} for each slot level above 1st.", null);
        var healingScaling = assertInstanceOf(GenericScalingMechanic.class, healing.payload());
        assertEquals(ScalingQuantity.HEALING_AMOUNT, healingScaling.quantity());
        assertEquals(1, healingScaling.baseLevel());
        assertEquals(new DiceAmount(1, 4, 0), healingScaling.steps().getFirst().value());
    }

    @Test
    void slotScalingPreservesProjectileAndTargetQuantitiesSeparately() {
        var projectiles = extract("When you cast this spell using a spell slot, it creates one additional "
                + "ray for each spell slot level above 2nd.", null);
        var projectileScaling = assertInstanceOf(GenericScalingMechanic.class, projectiles.payload());
        assertEquals(ScalingQuantity.PROJECTILE_COUNT, projectileScaling.quantity());
        assertEquals(new IntegerAmount(1), projectileScaling.steps().getFirst().value());

        var targets = extract("The spell affects one additional target for each spell slot level above 1st.", null);
        assertEquals(ScalingQuantity.TARGET_COUNT,
                assertInstanceOf(GenericScalingMechanic.class, targets.payload()).quantity());
    }

    @Test
    void explicitFixedSlotThresholdIncrementsAreNotTreatedAsPerLevelIncrements() {
        var result = extract("When cast using a 3rd-level spell slot, fire damage increases by {@damage 1d6}; "
                + "when cast using a 5th-level spell slot, it increases by {@damage 2d6}.", null);
        var scaling = assertInstanceOf(GenericScalingMechanic.class, result.payload());
        assertEquals(ScalingProgressionType.FIXED_THRESHOLD_INCREMENT, scaling.progressionType());
        assertEquals(ScalingAxis.SPELL_SLOT_LEVEL, scaling.axis());
        assertEquals(ScalingQuantity.DAMAGE_AMOUNT, scaling.quantity());
        assertEquals(List.of(3, 5), scaling.steps().stream().map(ScalingStep::minimumLevel).toList());
    }

    @Test
    void characterThresholdCountsAndStructuredDamageTiersRemainDistinctFromSlotScaling() {
        var beams = extract("The spell creates two beams at 5th level, three beams at 11th level, "
                + "and four beams at 17th level.", null);
        var beamScaling = assertInstanceOf(GenericScalingMechanic.class, beams.payload());
        assertEquals(ScalingAxis.CHARACTER_LEVEL, beamScaling.axis());
        assertEquals(List.of(5, 11, 17), beamScaling.steps().stream().map(ScalingStep::minimumLevel).toList());
        assertEquals(List.of(new IntegerAmount(2), new IntegerAmount(3), new IntegerAmount(4)),
                beamScaling.steps().stream().map(ScalingStep::value).toList());

        SpellDefinition rayOfFrost = spell(List.of(new SpellScaling("cold damage", List.of(
                new SpellScalingValue("1", "1d8"), new SpellScalingValue("5", "2d8"),
                new SpellScalingValue("11", "3d8"), new SpellScalingValue("17", "4d8")))));
        SourceFragment structured = new SourceFragment(OWNER.value() + "#/scalingLevelDice", "/scalingLevelDice",
                SourceFragment.Kind.STRUCTURED_FIELD,
                "{\"label\":\"cold damage\",\"scaling\":{\"1\":\"1d8\",\"5\":\"2d8\"}}",
                List.of("scalingLevelDice"), List.of(MechanicType.SCALING));
        var tierResult = extraction.extract(candidate(structured), structured, rayOfFrost);
        var tiers = assertInstanceOf(GenericScalingMechanic.class, tierResult.payload());
        assertEquals(ScalingAxis.CHARACTER_LEVEL, tiers.axis());
        assertEquals(ScalingQuantity.DAMAGE_AMOUNT, tiers.quantity());
        assertEquals(List.of(1, 5, 11, 17), tiers.steps().stream().map(ScalingStep::minimumLevel).toList());
    }

    @Test
    void scalingAbstainsWhenAmountThresholdOrQuantityIsNotAuthored() {
        assertFalse(extract("At higher levels, this spell becomes more powerful.", null).supported());
        assertFalse(extract("The damage increases when cast with a higher-level slot.", null).supported());
        assertFalse(extract("The numeric value increases by one for each spell slot level above 1st.", null)
                .supported());
    }

    @Test
    void healingExtractionPreservesTheSymbolicCasterModifierForSlotScaling() {
        SourceFragment fragment = fragment("/entries/0", "A creature regains hit points equal to "
                + "{@dice 1d4} + your spellcasting ability modifier.");
        var result = new GenericMechanicStrategy(MechanicType.HEALING).extract(
                candidate(fragment), fragment, null);
        var healing = assertInstanceOf(HealingMechanic.class, result.payload());
        assertInstanceOf(com.thedndpub.data.mechanics.model.SumAmount.class, healing.amount());
    }

    @Test
    void relationshipsResolveAcrossFragmentsAndRejectAmbiguousTargets() {
        SourceFragment scalingFragment = fragment("/entriesHigherLevel/0/entries/0",
                "At higher levels, the fire damage increases by {@scaledamage 8d6|3-9|1d6} "
                        + "for each slot level above 3rd.");
        SourceFragment baseFragment = fragment("/entries/0", "A creature takes {@damage 8d6} fire damage.");
        ExtractedMechanic scaling = extracted("scale", MechanicType.SCALING,
                new GenericScalingMechanic(ScalingAxis.SPELL_SLOT_LEVEL, ScalingProgressionType.PER_LEVEL_INCREMENT,
                        ScalingQuantity.DAMAGE_AMOUNT, 3, List.of(new ScalingStep(4, new DiceAmount(1, 6, 0)))),
                scalingFragment);
        ExtractedMechanic damage = extracted("damage", MechanicType.DAMAGE,
                new DamageMechanic(new DiceAmount(8, 6, 0), new FixedDamageType("fire")), baseFragment);
        var result = new ScalingRelationshipStrategy().resolve(List.of(damage, scaling), context(
                List.of(scalingFragment, baseFragment)));
        assertEquals(1, result.relationships().size());
        var relationship = result.relationships().getFirst();
        assertEquals("scale", relationship.sourceMechanicId());
        assertEquals("damage", relationship.targetMechanicId());
        assertEquals(ScalingQuantity.DAMAGE_AMOUNT, relationship.scalingRule().affectedQuantity());

        ExtractedMechanic secondDamage = extracted("other-damage", MechanicType.DAMAGE,
                new DamageMechanic(new DiceAmount(2, 6, 0), new FixedDamageType("fire")), baseFragment);
        var ambiguous = new ScalingRelationshipStrategy().resolve(List.of(damage, secondDamage, scaling), context(
                List.of(scalingFragment, baseFragment)));
        assertTrue(ambiguous.relationships().isEmpty());
        assertEquals("SCALING_AFFECTED_MECHANIC_AMBIGUOUS", ambiguous.diagnostics().getFirst().code());
    }

    @Test
    void relationshipValidationRequiresScalingSourceAndMatchingTargetFamily() {
        var relation = new ExperimentalMechanicRelationshipDto("rel", MechanicRelationshipType.SCALING_APPLIES_TO,
                OWNER, REVISION, "scale", "damage", List.of(), List.of("evidence"), List.of(),
                new ScalingRelationshipRule(ScalingQuantity.DAMAGE_AMOUNT));
        var scaling = new ExperimentalMechanicDto("scale", "SCALING",
                new GenericScalingMechanic(ScalingAxis.SPELL_SLOT_LEVEL, ScalingProgressionType.PER_LEVEL_INCREMENT,
                        ScalingQuantity.DAMAGE_AMOUNT, 1, List.of(new ScalingStep(2, new DiceAmount(1, 6, 0)))), null);
        var damage = new ExperimentalMechanicDto("damage", "DAMAGE",
                new DamageMechanic(new DiceAmount(2, 6, 0), new FixedDamageType("fire")), null);
        var evidence = new ExperimentalEvidenceDto("evidence", List.of("scale", "damage"), List.of("rel"),
                REVISION.resourcePath(), REVISION.datasetVersion(), REVISION.resourceSha256(), "/spell/0/entriesHigherLevel/0",
                0, 20, new SourceReference("PHB", null), "SUPPORTS_CANDIDATE", "scaling-applies-to", "1");
        var metadata = new ExperimentalMechanicsMetadataDto(OWNER, "engine", REVISION.datasetVersion(),
                REVISION.resourcePath(), REVISION.resourceSha256(),
                com.thedndpub.data.mechanics.model.CoverageStatus.PARTIAL, "RECOGNIZED_FRAGMENTS_ONLY",
                "PENDING_REVIEW", "NEEDS_REVIEW", false, List.of(), List.of());
        var dto = new ExperimentalMechanicsV2Dto(2, "EXPERIMENTAL", "CANDIDATE_PARTIAL",
                List.of(scaling, damage), List.of(relation), List.of(evidence), List.of(), metadata);
        assertTrue(new ObjectMapper().valueToTree(dto).path("relationships").get(0).has("scalingRule"));

        var invalid = new ExperimentalMechanicRelationshipDto("invalid", MechanicRelationshipType.SCALING_APPLIES_TO,
                OWNER, REVISION, "scale", "damage", List.of(), List.of("evidence"), List.of(),
                new ScalingRelationshipRule(ScalingQuantity.HEALING_AMOUNT));
        assertThrows(IllegalArgumentException.class, () -> new ExperimentalMechanicsV2Dto(2, "EXPERIMENTAL",
                "CANDIDATE_PARTIAL", List.of(scaling, damage), List.of(invalid), List.of(evidence), List.of(), metadata));
    }

    private static StrategyExtraction extract(String text, SpellDefinition spell) {
        SourceFragment fragment = fragment("/entriesHigherLevel/0/entries/0", text);
        return new GenericScalingExtractionStrategy().extract(candidate(fragment), fragment, spell);
    }

    private static SpellDefinition spell(List<SpellScaling> scaling) {
        SourceIdentity source = new SourceIdentity("PHB");
        return new SpellDefinition(OWNER, new SpellIdentity("Test Scaling", source), new SourceReference("PHB", null),
                0, "evocation", List.of(), List.of(), null, null, List.of(), false,
                new SpellMechanics(List.of(), List.of(), List.of(), List.of(), List.of(), List.of(), List.of(),
                        List.of(), List.of(), List.of(), List.of(), List.of()), scaling, List.of(), List.of(),
                new SpellSourceRelationships(List.of(), List.of(), List.of(), List.of()),
                new SpellImportMetadata(null, null, null, null, null, null));
    }

    private static MechanicCandidate candidate(SourceFragment fragment) {
        return new MechanicCandidate(MechanicType.SCALING, fragment.id(), "test", "1",
                List.of(fragment.pointer()), String.join(" > ", fragment.hierarchy()), false);
    }

    private static SourceFragment fragment(String pointer, String text) {
        return new SourceFragment(OWNER.value() + "#" + pointer, pointer, SourceFragment.Kind.AUTHORED_TEXT,
                text, List.of(pointer.startsWith("/entriesHigherLevel") ? "entriesHigherLevel" : "entries"),
                List.of(MechanicType.SCALING));
    }

    private static ExtractedMechanic extracted(String id, MechanicType type,
            com.thedndpub.data.mechanics.model.MechanicPayload payload, SourceFragment fragment) {
        return new ExtractedMechanic(id, type, payload, null, fragment.id());
    }

    private static MechanicInterpretationContext context(List<SourceFragment> fragments) {
        Map<String, SourceFragment> byId = fragments.stream().collect(java.util.stream.Collectors.toMap(
                SourceFragment::id, item -> item));
        Map<String, String> evidence = fragments.stream().collect(java.util.stream.Collectors.toMap(
                SourceFragment::id, item -> "ev:" + item.pointer()));
        return new MechanicInterpretationContext(OWNER, REVISION, byId, evidence);
    }
}
