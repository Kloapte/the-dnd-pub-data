package com.thedndpub.data.mechanics.experimental;

import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dto.spell.SpellId;
import com.thedndpub.data.mechanics.model.Ability;
import com.thedndpub.data.mechanics.model.DamageMechanic;
import com.thedndpub.data.mechanics.model.DiceAmount;
import com.thedndpub.data.mechanics.model.FixedAbility;
import com.thedndpub.data.mechanics.model.FixedDamageType;
import com.thedndpub.data.mechanics.model.MechanicContext;
import com.thedndpub.data.mechanics.model.SavingThrowRequirementMechanic;
import com.thedndpub.data.mechanics.model.SourceArtifactRevision;
import com.thedndpub.data.mechanics.model.TargetScope;
import com.thedndpub.data.mechanics.model.UnknownSaveDifficulty;
import com.thedndpub.data.mechanics.model.UnknownSubject;
import com.thedndpub.data.mechanics.model.UnknownValueReason;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SavingThrowDamageOutcomeStrategyTests {
    private static final SpellId OWNER = new SpellId("spell:phb:fireball");
    private static final SourceArtifactRevision REVISION = new SourceArtifactRevision(
            "5eTools v2.37.0", "data/spells/spells-phb.json", "a".repeat(64));
    private final SavingThrowDamageOutcomeStrategy strategy = new SavingThrowDamageOutcomeStrategy();

    @Test
    void fireballStyleFailureAndSuccessOutcomesResolveToTheSameDamageEffect() {
        String text = "Each creature in the area must make a Dexterity saving throw. "
                + "A target takes {@damage 8d6} fire damage on a failed save, "
                + "or half as much damage on a successful one.";
        SourceFragment fragment = fragment("/entries/0", text);
        ExtractedMechanic save = save("save", fragment, Ability.DEXTERITY);
        ExtractedMechanic damage = damage("damage", fragment, 8, "fire");

        RelationshipResolutionResult result = resolve(List.of(save, damage), List.of(fragment));

        assertEquals(1, result.relationships().size());
        assertEquals(List.of(
                new SaveDamageOutcomeRule(SaveDamageOutcomeCondition.FAILURE, DamageApplicationBehavior.FULL_DAMAGE),
                new SaveDamageOutcomeRule(SaveDamageOutcomeCondition.SUCCESS, DamageApplicationBehavior.HALF_DAMAGE)),
                result.relationships().getFirst().outcomes());
        assertEquals(List.of("evidence-/entries/0"), result.relationships().getFirst().evidenceIds());
    }

    @Test
    void oneSaveCanGovernSeveralDamageComponentsAndExplicitNoDamageSuccess() {
        String text = "Each creature makes a Dexterity saving throw. On a failed save it takes "
                + "{@damage 2d6} fire damage and {@damage 1d6} cold damage; "
                + "on a successful save, it takes no damage.";
        SourceFragment fragment = fragment("/entries/0/entries/0", text);
        ExtractedMechanic save = save("save", fragment, Ability.DEXTERITY);
        ExtractedMechanic fire = damage("fire", fragment, 2, "fire");
        ExtractedMechanic cold = damage("cold", fragment, 1, "cold");

        RelationshipResolutionResult result = resolve(List.of(save, fire, cold), List.of(fragment));

        assertEquals(2, result.relationships().size());
        assertTrue(result.relationships().stream().allMatch(item -> item.outcomes().contains(
                new SaveDamageOutcomeRule(SaveDamageOutcomeCondition.FAILURE, DamageApplicationBehavior.FULL_DAMAGE))));
        assertTrue(result.relationships().stream().allMatch(item -> item.outcomes().contains(
                new SaveDamageOutcomeRule(SaveDamageOutcomeCondition.SUCCESS, DamageApplicationBehavior.NO_DAMAGE))));
    }

    @Test
    void distinctExplicitAmountsCanBeBoundToDifferentSaveOutcomes() {
        String text = "The target makes a Constitution saving throw. On a failed save, it takes "
                + "{@damage 3d6} necrotic damage; on a successful save, it takes {@damage 1d6} necrotic damage.";
        SourceFragment fragment = fragment("/entries/0", text);
        ExtractedMechanic save = save("save", fragment, Ability.CONSTITUTION);
        ExtractedMechanic failureDamage = damage("damage-failure", fragment, 3, "necrotic");
        ExtractedMechanic successDamage = damage("damage-success", fragment, 1, "necrotic");

        RelationshipResolutionResult result = resolve(List.of(save, failureDamage, successDamage), List.of(fragment));

        assertEquals(2, result.relationships().size());
        assertEquals(List.of(new SaveDamageOutcomeRule(SaveDamageOutcomeCondition.FAILURE,
                        DamageApplicationBehavior.FULL_DAMAGE)), relationshipFor(result, "damage-failure").outcomes());
        assertEquals(List.of(new SaveDamageOutcomeRule(SaveDamageOutcomeCondition.SUCCESS,
                        DamageApplicationBehavior.FULL_DAMAGE)), relationshipFor(result, "damage-success").outcomes());
    }

    @Test
    void explicitHalfDamageOnFailedSaveOverridesTheDefaultOutcomeInterpretation() {
        SourceFragment fragment = fragment("/entries/0", "The target makes a Constitution saving throw. "
                + "On a failed save, it takes half as much damage from {@damage 3d6} necrotic damage.");
        ExtractedMechanic save = save("save", fragment, Ability.CONSTITUTION);
        ExtractedMechanic damage = damage("damage", fragment, 3, "necrotic");

        RelationshipResolutionResult result = resolve(List.of(save, damage), List.of(fragment));

        assertEquals(List.of(new SaveDamageOutcomeRule(SaveDamageOutcomeCondition.FAILURE,
                DamageApplicationBehavior.HALF_DAMAGE)), result.relationships().getFirst().outcomes());
    }

    @Test
    void multipleIndependentSaveDamageRelationsAreComposedDeterministically() {
        SourceFragment first = fragment("/entries/0", "The target must succeed on a Wisdom saving throw or take "
                + "{@damage 1d6} psychic damage.");
        SourceFragment second = fragment("/entries/1", "The creature makes a Dexterity saving throw. "
                + "On a failed save, it takes {@damage 2d6} fire damage.");
        List<ExtractedMechanic> mechanics = List.of(save("wisdom", first, Ability.WISDOM),
                damage("psychic", first, 1, "psychic"), save("dexterity", second, Ability.DEXTERITY),
                damage("fire", second, 2, "fire"));

        List<String> firstRun = resolve(mechanics, List.of(first, second)).relationships().stream()
                .map(ExperimentalMechanicRelationshipDto::id).toList();
        List<String> secondRun = resolve(mechanics, List.of(second, first)).relationships().stream()
                .map(ExperimentalMechanicRelationshipDto::id).toList();

        assertEquals(firstRun, secondRun);
        assertEquals(2, firstRun.size());
    }

    @Test
    void identicalWordingKeepsEditionQualifiedOwnersAndRelationshipIdsSeparate() {
        SourceFragment phb = fragment("/entries/0", "The target must succeed on a Dexterity saving throw or take "
                + "{@damage 2d6} fire damage.");
        SourceFragment xphb = new SourceFragment("spell:xphb:fireball#/entries/0", "/entries/0",
                SourceFragment.Kind.AUTHORED_TEXT, phb.text(), List.of("entries"),
                List.of(MechanicType.SAVING_THROW, MechanicType.DAMAGE));
        List<ExtractedMechanic> phbMechanics = List.of(save("save", phb, Ability.DEXTERITY), damage("damage", phb, 2, "fire"));
        List<ExtractedMechanic> xphbMechanics = List.of(save("save", xphb, Ability.DEXTERITY), damage("damage", xphb, 2, "fire"));
        var phbRelationship = resolve(phbMechanics, List.of(phb)).relationships().getFirst();
        Map<String, SourceFragment> fragments = Map.of(xphb.id(), xphb);
        var xphbResult = strategy.resolve(xphbMechanics, new MechanicInterpretationContext(
                new SpellId("spell:xphb:fireball"),
                new SourceArtifactRevision("5eTools v2.37.0", "data/spells/spells-xphb.json", "b".repeat(64)),
                fragments, Map.of(xphb.id(), "evidence-xphb")));

        assertEquals("spell:phb:fireball", phbRelationship.ownerId().value());
        assertEquals("spell:xphb:fireball", xphbResult.relationships().getFirst().ownerId().value());
        assertFalse(phbRelationship.id().equals(xphbResult.relationships().getFirst().id()));
    }

    @Test
    void coOccurrenceWithoutOutcomeWordingDoesNotCreateRelationship() {
        SourceFragment fragment = fragment("/entries/0", "The target makes a Dexterity saving throw. "
                + "The spell also deals {@damage 2d6} fire damage later.");

        RelationshipResolutionResult result = resolve(List.of(save("save", fragment, Ability.DEXTERITY),
                damage("damage", fragment, 2, "fire")), List.of(fragment));

        assertTrue(result.relationships().isEmpty());
        assertTrue(result.diagnostics().stream().anyMatch(item -> item.code().equals("SAVE_DAMAGE_CO_OCCURRENCE_ONLY")));
    }

    @Test
    void unspecifiedSuccessfulSaveDamageBehaviorRemainsUnresolved() {
        SourceFragment fragment = fragment("/entries/0", "The target makes a Dexterity saving throw. "
                + "The spell deals {@damage 2d6} fire damage. On a successful save, the spell's other effects end.");

        RelationshipResolutionResult result = resolve(List.of(save("save", fragment, Ability.DEXTERITY),
                damage("damage", fragment, 2, "fire")), List.of(fragment));

        assertTrue(result.relationships().isEmpty());
        assertTrue(result.diagnostics().stream().anyMatch(
                item -> item.code().equals("SAVE_DAMAGE_OUTCOME_UNRESOLVED")));
    }

    @Test
    void multipleCandidateSavesKeepOutcomeRelationshipUnresolved() {
        SourceFragment dex = fragment("/entries/0", "The target makes a Dexterity saving throw.");
        SourceFragment wis = fragment("/entries/1", "The creature makes a Wisdom saving throw.");
        SourceFragment damageText = fragment("/entries/2", "On a failed save, it takes {@damage 2d6} fire damage.");

        RelationshipResolutionResult result = resolve(List.of(save("dex", dex, Ability.DEXTERITY),
                save("wis", wis, Ability.WISDOM), damage("damage", damageText, 2, "fire")),
                List.of(dex, wis, damageText));

        assertTrue(result.relationships().isEmpty());
        assertTrue(result.diagnostics().stream().anyMatch(item -> item.code().equals("AMBIGUOUS_SAVE_DAMAGE_PAIRING")));
    }

    @Test
    void differentExplicitAbilitySelectsOnlyTheMatchingSave() {
        SourceFragment dex = fragment("/entries/0", "The target makes a Dexterity saving throw.");
        SourceFragment wis = fragment("/entries/1", "The creature makes a Wisdom saving throw.");
        SourceFragment damageText = fragment("/entries/2", "The target must succeed on a Wisdom saving throw or take "
                + "{@damage 2d6} fire damage.");

        RelationshipResolutionResult result = resolve(List.of(save("dex", dex, Ability.DEXTERITY),
                save("wis", wis, Ability.WISDOM), damage("damage", damageText, 2, "fire")),
                List.of(dex, wis, damageText));

        assertEquals(1, result.relationships().size());
        assertEquals("wis", result.relationships().getFirst().sourceMechanicId());
        assertEquals(2, result.relationships().getFirst().evidenceIds().size());
    }

    @Test
    void relationshipDtoRejectsCrossOwnerAndStaleSourceReferences() {
        SourceFragment fragment = fragment("/entries/0", "A target must succeed on a Dexterity saving throw or take "
                + "{@damage 2d6} fire damage.");
        ExperimentalMechanicRelationshipDto relationship = resolve(List.of(save("save", fragment, Ability.DEXTERITY),
                damage("damage", fragment, 2, "fire")), List.of(fragment)).relationships().getFirst();
        var saveDto = new ExperimentalMechanicDto("save", "SAVING_THROW",
                save("save", fragment, Ability.DEXTERITY).payload(), save("save", fragment, Ability.DEXTERITY).context());
        var damageDto = new ExperimentalMechanicDto("damage", "DAMAGE",
                damage("damage", fragment, 2, "fire").payload(), damage("damage", fragment, 2, "fire").context());
        var evidence = new ExperimentalEvidenceDto("evidence-/entries/0", List.of("save", "damage"),
                List.of(relationship.id()), REVISION.resourcePath(), REVISION.datasetVersion(), REVISION.resourceSha256(),
                "/spells/0/entries/0", 0, fragment.text().length(), new SourceReference("PHB", null),
                "SUPPORTS_CANDIDATE", "generic-source-signals", "1");
        var metadata = new ExperimentalMechanicsMetadataDto(OWNER, "engine", REVISION.datasetVersion(),
                REVISION.resourcePath(), REVISION.resourceSha256(),
                com.thedndpub.data.mechanics.model.CoverageStatus.PARTIAL, "RECOGNIZED_FRAGMENTS_ONLY",
                "PENDING_REVIEW", "NEEDS_REVIEW", false, List.of(), List.of());
        var validDto = new ExperimentalMechanicsV2Dto(2, "EXPERIMENTAL", "CANDIDATE_PARTIAL",
                List.of(saveDto, damageDto), List.of(relationship), List.of(evidence), List.of(), metadata);
        var json = new ObjectMapper().valueToTree(validDto);
        assertTrue(json.path("relationships").isArray());
        assertFalse(json.path("mechanics").get(0).has("relationships"));

        assertThrows(IllegalArgumentException.class, () -> new ExperimentalMechanicsV2Dto(2, "EXPERIMENTAL",
                "CANDIDATE_PARTIAL", List.of(saveDto, damageDto),
                List.of(new ExperimentalMechanicRelationshipDto(relationship.id(), relationship.type(),
                        new SpellId("spell:xphb:fireball"), REVISION, "save", "damage", relationship.outcomes(),
                        relationship.evidenceIds())), List.of(evidence), List.of(), metadata));
        assertThrows(IllegalArgumentException.class, () -> new ExperimentalMechanicsV2Dto(2, "EXPERIMENTAL",
                "CANDIDATE_PARTIAL", List.of(saveDto, damageDto),
                List.of(new ExperimentalMechanicRelationshipDto(relationship.id(), relationship.type(), OWNER,
                        new SourceArtifactRevision(REVISION.datasetVersion(), REVISION.resourcePath(), "b".repeat(64)),
                        "save", "damage", relationship.outcomes(), relationship.evidenceIds())),
                List.of(evidence), List.of(), metadata));
        var incompleteEvidence = new ExperimentalEvidenceDto(evidence.id(), List.of("save"), List.of(),
                evidence.sourceResource(), evidence.sourceRevision(), evidence.sourceSha256(), evidence.jsonPointer(),
                evidence.textStart(), evidence.textEnd(), evidence.source(), evidence.role(), evidence.recognizer(),
                evidence.recognizerVersion());
        assertThrows(IllegalArgumentException.class, () -> new ExperimentalMechanicsV2Dto(2, "EXPERIMENTAL",
                "CANDIDATE_PARTIAL", List.of(saveDto, damageDto), List.of(relationship),
                List.of(incompleteEvidence), List.of(), metadata));
    }

    private RelationshipResolutionResult resolve(List<ExtractedMechanic> mechanics, List<SourceFragment> fragments) {
        Map<String, SourceFragment> fragmentMap = fragments.stream()
                .collect(java.util.stream.Collectors.toMap(SourceFragment::id, item -> item));
        Map<String, String> evidence = fragments.stream().collect(java.util.stream.Collectors.toMap(
                SourceFragment::id, item -> "evidence-" + item.pointer()));
        return strategy.resolve(mechanics, new MechanicInterpretationContext(OWNER, REVISION, fragmentMap, evidence));
    }

    private static ExperimentalMechanicRelationshipDto relationshipFor(
            RelationshipResolutionResult result, String damageId) {
        return result.relationships().stream().filter(item -> item.targetMechanicId().equals(damageId))
                .findFirst().orElseThrow();
    }

    private static SourceFragment fragment(String pointer, String text) {
        return new SourceFragment("spell:phb:fireball#" + pointer, pointer, SourceFragment.Kind.AUTHORED_TEXT,
                text, List.of("entries"), List.of(MechanicType.SAVING_THROW, MechanicType.DAMAGE));
    }

    private static ExtractedMechanic save(String id, SourceFragment fragment, Ability ability) {
        return new ExtractedMechanic(id, MechanicType.SAVING_THROW,
                new SavingThrowRequirementMechanic(new FixedAbility(ability),
                        new UnknownSaveDifficulty(UnknownValueReason.NOT_INTERPRETED)),
                new MechanicContext(new UnknownSubject(UnknownValueReason.NOT_INTERPRETED), List.of()), fragment.id());
    }

    private static ExtractedMechanic damage(String id, SourceFragment fragment, int dice, String type) {
        return new ExtractedMechanic(id, MechanicType.DAMAGE,
                new DamageMechanic(new DiceAmount(dice, 6, 0), new FixedDamageType(type)),
                new MechanicContext(new UnknownSubject(UnknownValueReason.NOT_INTERPRETED), List.of()), fragment.id());
    }
}
