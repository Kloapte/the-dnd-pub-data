package com.thedndpub.data.mechanics.experimental;

import com.thedndpub.data.dto.spell.SpellId;
import com.thedndpub.data.mechanics.model.ConditionChangeMechanic;
import com.thedndpub.data.mechanics.model.ConditionChoiceMechanic;
import com.thedndpub.data.mechanics.model.ConditionName;
import com.thedndpub.data.mechanics.model.ConditionOperation;
import com.thedndpub.data.mechanics.model.DamageMechanic;
import com.thedndpub.data.mechanics.model.DiceAmount;
import com.thedndpub.data.mechanics.model.EffectDurationMechanic;
import com.thedndpub.data.mechanics.model.FixedDamageType;
import com.thedndpub.data.mechanics.model.FixedDuration;
import com.thedndpub.data.mechanics.model.FixedAbility;
import com.thedndpub.data.mechanics.model.DurationUnit;
import com.thedndpub.data.mechanics.model.EffectDurationMechanic;
import com.thedndpub.data.mechanics.model.MechanicContext;
import com.thedndpub.data.mechanics.model.RepeatSaveTiming;
import com.thedndpub.data.mechanics.model.SavingThrowRequirementMechanic;
import com.thedndpub.data.mechanics.model.SourceArtifactRevision;
import com.thedndpub.data.mechanics.model.UnknownSaveDifficulty;
import com.thedndpub.data.mechanics.model.UnknownSubject;
import com.thedndpub.data.mechanics.model.UnknownValueReason;
import com.thedndpub.data.mechanics.model.Ability;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GenericConditionStrategiesTests {
    private static final GenericConditionExtractionStrategy CONDITIONS = new GenericConditionExtractionStrategy();
    private static final GenericMechanicStrategy SAVES = new GenericMechanicStrategy(MechanicType.SAVING_THROW);

    @Test
    void extractsExplicitApplyRemoveImmunityAndPreventionAsDistinctOperations() {
        assertOperation("The target becomes blinded.", ConditionName.BLINDED, ConditionOperation.APPLY);
        assertOperation("The creature gains the poisoned condition after the event.",
                ConditionName.POISONED, ConditionOperation.APPLY);
        assertOperation("The target has the poisoned condition imposed by the effect.",
                ConditionName.POISONED, ConditionOperation.APPLY);
        assertOperation("The target must succeed on a Wisdom saving throw or have the paralyzed condition.",
                ConditionName.PARALYZED, ConditionOperation.APPLY);
        assertOperation("You can remove the poisoned condition.", ConditionName.POISONED, ConditionOperation.REMOVE);
        assertOperation("The creature is immune to being frightened.",
                ConditionName.FRIGHTENED, ConditionOperation.GRANT_IMMUNITY);
        assertOperation("The effect prevents the target from being paralyzed.",
                ConditionName.PARALYZED, ConditionOperation.PREVENT);
        assertOperation("Creatures can't gain the frightened condition while in the area.",
                ConditionName.FRIGHTENED, ConditionOperation.PREVENT);
    }

    @Test
    void structuredConditionTagsDoNotProveAnOperation() {
        SourceFragment fragment = fragment("/conditionInflict/0", SourceFragment.Kind.STRUCTURED_FIELD,
                "paralyzed");
        StrategyExtraction result = CONDITIONS.extract(candidate(fragment), fragment, null);
        assertFalse(result.supported());
        assertEquals("CONDITION_TAG_SEMANTICS_UNRESOLVED", result.diagnosticCode());
    }

    @Test
    void preexistingConditionMentionIsNotTreatedAsApplication() {
        SourceFragment fragment = authored("The target already has the charmed condition from a creature.");
        StrategyExtraction result = CONDITIONS.extract(candidate(fragment), fragment, null);
        assertFalse(result.supported());
        assertEquals("CONDITION_OPERATION_UNRESOLVED", result.diagnosticCode());
    }

    @Test
    void explicitChoiceIsOneChoicePayloadAndDoesNotApplyEveryAlternative() {
        SourceFragment fragment = authored("The target becomes blinded or deafened (your choice).");
        StrategyExtraction result = CONDITIONS.extract(candidate(fragment), fragment, null);
        assertEquals(1, result.payloads().size());
        ConditionChoiceMechanic choice = assertInstanceOf(ConditionChoiceMechanic.class, result.payload());
        assertEquals(List.of(ConditionName.BLINDED, ConditionName.DEAFENED), choice.alternatives());
        assertEquals(ConditionOperation.APPLY, choice.operation());
    }

    @Test
    void conditionRemovalChoiceDoesNotBecomeConditionApplication() {
        SourceFragment fragment = authored("You touch a creature and can end either one disease or one condition "
                + "afflicting it. The condition can be blinded, deafened, paralyzed, or poisoned.");
        StrategyExtraction result = CONDITIONS.extract(candidate(fragment), fragment, null);
        ConditionChoiceMechanic choice = assertInstanceOf(ConditionChoiceMechanic.class, result.payload());
        assertEquals(ConditionOperation.REMOVE, choice.operation());
        assertEquals(4, choice.alternatives().size());
    }

    @Test
    void prohibitionIsPreventionAndAlsoAppliesAcrossAConditionList() {
        SourceFragment fragment = authored("The target can't be charmed, {@condition frightened}, or possessed.");
        StrategyExtraction result = CONDITIONS.extract(candidate(fragment), fragment, null);
        assertEquals(2, result.payloads().size());
        assertEquals(List.of(ConditionName.CHARMED, ConditionName.FRIGHTENED), result.payloads().stream()
                .map(item -> assertInstanceOf(ConditionChangeMechanic.class, item.payload()))
                .map(ConditionChangeMechanic::condition).toList());
        assertTrue(result.payloads().stream().map(item -> (ConditionChangeMechanic) item.payload())
                .allMatch(item -> item.operation() == ConditionOperation.PREVENT));
    }

    @Test
    void extractsExplicitConditionDurationAsSeparateMechanic() {
        SourceFragment fragment = authored("The target becomes paralyzed for 1 minute.");
        StrategyExtraction result = CONDITIONS.extract(candidate(fragment), fragment, null);
        StrategyExtraction durationResult = new GenericConditionDurationExtractionStrategy().extract(
                candidate(MechanicType.DURATION, fragment), fragment, null);
        assertEquals(1, result.payloads().size());
        assertInstanceOf(ConditionChangeMechanic.class, result.payload());
        EffectDurationMechanic duration = assertInstanceOf(EffectDurationMechanic.class, durationResult.payload());
        FixedDuration fixed = assertInstanceOf(FixedDuration.class, duration.specification());
        assertEquals(DurationUnit.MINUTE, fixed.unit());
        assertEquals(1, fixed.amount());
    }

    @Test
    void unrelatedSpellDurationDoesNotAttachToEarlierConditionMention() {
        SourceFragment fragment = authored("The target is immune to being blinded. The spell lasts for 1 minute.");
        StrategyExtraction result = new GenericConditionDurationExtractionStrategy().extract(
                candidate(MechanicType.DURATION, fragment), fragment, null);
        assertFalse(result.supported());
        assertEquals("CONDITION_DURATION_RELATION_UNRESOLVED", result.diagnosticCode());
    }

    @Test
    void multiFamilyRelationshipStrategyIsEnumeratedOnce() {
        List<MechanicRelationshipStrategy> strategies = MechanicRelationshipStrategyRegistry.standard().allStrategies();
        assertEquals(strategies.size(), strategies.stream().map(MechanicRelationshipStrategy::strategyId).distinct().count());
        assertEquals(4, strategies.size());
    }

    @Test
    void actionAndSuccessfulCheckCanEndAConditionAcrossSiblingEntries() {
        SourceFragment conditionFragment = authored(
                "A creature must succeed on a Strength saving throw or be restrained by the plants.",
                "/entries/0");
        SourceFragment eventFragment = authored(
                "A creature {@condition restrained} by the plants, or one that can touch the creature, "
                        + "can use its action to make a Strength check. "
                        + "On a success, it frees itself.", "/entries/1");
        ConditionChangeMechanic condition = (ConditionChangeMechanic) CONDITIONS.extract(
                candidate(conditionFragment), conditionFragment, null).payload();
        var extraction = new GenericConditionTerminationExtractionStrategy().extract(
                candidate(MechanicType.CONDITION_TERMINATION, eventFragment), eventFragment, null);
        var event = assertInstanceOf(com.thedndpub.data.mechanics.model.ConditionTerminationEventMechanic.class,
                extraction.payload());
        assertEquals(com.thedndpub.data.mechanics.model.ConditionTerminationActor
                .AFFECTED_SUBJECT_OR_ELIGIBLE_OTHER_CREATURE, event.actor());
        assertEquals(Ability.STRENGTH, event.checkAbility());
        var context = new MechanicInterpretationContext(new SpellId("spell:phb:entangling-example"), revision(),
                Map.of(conditionFragment.id(), conditionFragment, eventFragment.id(), eventFragment),
                Map.of(conditionFragment.id(), "e1", eventFragment.id(), "e2"));
        var result = new ConditionRelationshipStrategy().resolve(List.of(
                extracted("condition", MechanicType.CONDITION, condition, conditionFragment),
                extracted("termination", MechanicType.CONDITION_TERMINATION, event, eventFragment)), context);
        assertEquals(1, result.relationships().size());
        assertEquals(ConditionTerminationTrigger.ACTION_SUCCESSFUL_CHECK,
                result.relationships().getFirst().terminationRule().trigger());
    }

    @Test
    void resolvesAnExplicitConditionDurationAcrossSiblingFragments() {
        SourceFragment conditionFragment = authored("The target becomes paralyzed.", "/entries/0");
        SourceFragment durationFragment = authored("That condition lasts for 1 minute.", "/entries/1");
        var condition = CONDITIONS.extract(candidate(conditionFragment), conditionFragment, null).payload();
        var duration = new GenericConditionDurationExtractionStrategy().extract(
                candidate(MechanicType.DURATION, durationFragment), durationFragment, null).payload();
        var context = new MechanicInterpretationContext(new SpellId("spell:xge:condition-duration"), revision(),
                Map.of(conditionFragment.id(), conditionFragment, durationFragment.id(), durationFragment),
                Map.of(conditionFragment.id(), "e1", durationFragment.id(), "e2"));
        var result = new ConditionRelationshipStrategy().resolve(List.of(
                extracted("condition", MechanicType.CONDITION, condition, conditionFragment),
                extracted("duration", MechanicType.DURATION, duration, durationFragment)), context);
        assertEquals(1, result.relationships().stream()
                .filter(item -> item.type() == MechanicRelationshipType.CONDITION_DURATION).count());
    }

    @Test
    void composesInitialFailedSaveApplicationAndRepeatedSaveTermination() {
        String wording = "The target must succeed on a Wisdom saving throw or be paralyzed. "
                + "At the end of each of its turns, it repeats the saving throw, ending the effect on a success.";
        SourceFragment fragment = authored(wording);
        var saveExtraction = SAVES.extract(candidate(MechanicType.SAVING_THROW, fragment), fragment, null);
        var conditionExtraction = CONDITIONS.extract(candidate(MechanicType.CONDITION, fragment), fragment, null);
        ExtractedMechanic save = extracted("save-1", MechanicType.SAVING_THROW,
                saveExtraction.payload(), fragment);
        ExtractedMechanic condition = extracted("condition-1", MechanicType.CONDITION,
                conditionExtraction.payload(), fragment);
        var sourceRevision = revision();
        var context = new MechanicInterpretationContext(new SpellId("spell:phb:test"), sourceRevision,
                Map.of(fragment.id(), fragment), Map.of(fragment.id(), "evidence-1"));

        RelationshipResolutionResult result = new ConditionRelationshipStrategy()
                .resolve(List.of(save, condition), context);

        var outcome = result.relationships().stream()
                .filter(item -> item.type() == MechanicRelationshipType.SAVE_CONDITION_OUTCOME).findFirst().orElseThrow();
        assertEquals(new ConditionSaveOutcomeRule(com.thedndpub.data.mechanics.model.SaveOutcome.FAILURE,
                ConditionSaveEffect.APPLY, ConditionSaveTiming.INITIAL), outcome.conditionOutcomes().getFirst());
        assertTrue(outcome.conditionOutcomes().stream().anyMatch(rule ->
                rule.outcome() == com.thedndpub.data.mechanics.model.SaveOutcome.SUCCESS
                        && rule.effect() == ConditionSaveEffect.PREVENT_APPLICATION
                        && rule.timing() == ConditionSaveTiming.INITIAL));
        var termination = result.relationships().stream()
                .filter(item -> item.type() == MechanicRelationshipType.CONDITION_TERMINATION).findFirst().orElseThrow();
        assertEquals(ConditionTerminationTrigger.REPEATED_SAVE_SUCCESS, termination.terminationRule().trigger());
        assertEquals(RepeatSaveTiming.END_OF_EACH_TARGET_TURN, termination.terminationRule().saveTiming());
        assertTrue(result.diagnostics().isEmpty());
    }

    @Test
    void sourceMarkupAndSpellDurationStillAllowConditionApplicationExtraction() {
        SourceFragment fragment = authored("The target must succeed on a Wisdom saving throw or be "
                + "{@condition paralyzed} for the duration. At the end of each of its turns, the target can "
                + "make another Wisdom saving throw. On a success, the spell ends on the target.");
        StrategyExtraction result = CONDITIONS.extract(candidate(fragment), fragment, null);
        assertTrue(result.supported(), result.explanation());
        ConditionChangeMechanic condition = assertInstanceOf(ConditionChangeMechanic.class, result.payload());
        assertEquals(ConditionOperation.APPLY, condition.operation());
        assertEquals(ConditionName.PARALYZED, condition.condition());
    }

    @Test
    void repeatedOccurrenceOfSameSaveAbilityRemainsOneTypedSaveRequirement() {
        SourceFragment fragment = authored("The target must succeed on a Wisdom saving throw or be paralyzed. "
                + "At the end of each turn, it repeats the Wisdom saving throw.");
        var result = SAVES.extract(candidate(MechanicType.SAVING_THROW, fragment), fragment, null);
        assertTrue(result.supported());
        assertEquals(Ability.WISDOM,
                ((FixedAbility) ((SavingThrowRequirementMechanic) result.payload()).ability()).ability());
    }

    @Test
    void successfulSavePreventionIsNotConditionImmunity() {
        SourceFragment fragment = authored(
                "On a successful Wisdom saving throw, the target is not paralyzed.");
        var save = SAVES.extract(candidate(MechanicType.SAVING_THROW, fragment), fragment, null).payload();
        var condition = CONDITIONS.extract(candidate(MechanicType.CONDITION, fragment), fragment, null).payload();
        assertInstanceOf(ConditionChangeMechanic.class, condition);
        assertEquals(ConditionOperation.PREVENT, ((ConditionChangeMechanic) condition).operation());
        var result = new ConditionRelationshipStrategy().resolve(List.of(
                extracted("save", MechanicType.SAVING_THROW, save, fragment),
                extracted("condition", MechanicType.CONDITION, condition, fragment)),
                context(fragment));
        var relation = result.relationships().stream()
                .filter(item -> item.type() == MechanicRelationshipType.SAVE_CONDITION_OUTCOME).findFirst().orElseThrow();
        assertEquals(ConditionSaveEffect.PREVENT_APPLICATION, relation.conditionOutcomes().getFirst().effect());
        assertEquals(ConditionOperation.PREVENT, ((ConditionChangeMechanic) condition).operation());
    }

    @Test
    void repeatedFailedSaveCanMaintainConditionWhenAuthored() {
        SourceFragment fragment = authored("The target becomes frightened. At the end of each turn, it repeats "
                + "the Wisdom saving throw. On a failed save, the target remains frightened.");
        var save = SAVES.extract(candidate(MechanicType.SAVING_THROW, fragment), fragment, null).payload();
        var condition = CONDITIONS.extract(candidate(MechanicType.CONDITION, fragment), fragment, null).payload();
        var result = new ConditionRelationshipStrategy().resolve(List.of(
                extracted("save", MechanicType.SAVING_THROW, save, fragment),
                extracted("condition", MechanicType.CONDITION, condition, fragment)), context(fragment));
        var relation = result.relationships().stream()
                .filter(item -> item.type() == MechanicRelationshipType.SAVE_CONDITION_OUTCOME)
                .findFirst().orElseThrow();
        assertTrue(relation.conditionOutcomes().stream().anyMatch(rule ->
                rule.outcome() == com.thedndpub.data.mechanics.model.SaveOutcome.FAILURE
                        && rule.effect() == ConditionSaveEffect.MAINTAIN
                        && rule.timing() == ConditionSaveTiming.REPEATED));
    }

    @Test
    void ambiguousCrossFragmentPairingAbstainsInsteadOfUsingCooccurrence() {
        SourceFragment saveFragment = authored("A Wisdom saving throw is required.", "/entries/0");
        SourceFragment secondSaveFragment = authored("A Dexterity saving throw is also required.", "/entries/1");
        SourceFragment conditionFragment = authored("On a failed save, the target becomes paralyzed.", "/entries/2");
        var save = SAVES.extract(candidate(MechanicType.SAVING_THROW, saveFragment), saveFragment, null).payload();
        var secondSave = SAVES.extract(candidate(MechanicType.SAVING_THROW, secondSaveFragment),
                secondSaveFragment, null).payload();
        var condition = CONDITIONS.extract(candidate(MechanicType.CONDITION, conditionFragment),
                conditionFragment, null).payload();
        var allFragments = Map.of(saveFragment.id(), saveFragment,
                secondSaveFragment.id(), secondSaveFragment, conditionFragment.id(), conditionFragment);
        var evidence = Map.of(saveFragment.id(), "e1", secondSaveFragment.id(), "e3", conditionFragment.id(), "e2");
        var context = new MechanicInterpretationContext(new SpellId("spell:xge:ambiguous"), revision(),
                allFragments, evidence);
        var result = new ConditionRelationshipStrategy().resolve(List.of(
                extracted("save", MechanicType.SAVING_THROW, save, saveFragment),
                extracted("second-save", MechanicType.SAVING_THROW, secondSave, secondSaveFragment),
                extracted("condition", MechanicType.CONDITION, condition, conditionFragment)), context);
        assertTrue(result.relationships().isEmpty());
        assertTrue(result.diagnostics().stream().anyMatch(item ->
                item.code().equals("CONDITION_SAVE_PAIRING_AMBIGUOUS")
                        || item.code().equals("CONDITION_SAVE_ABILITY_UNRESOLVED")));
    }

    @Test
    void unrelatedDamageDoesNotReceiveAConditionRelationship() {
        SourceFragment fragment = authored("A Wisdom saving throw is required. The target becomes blinded.");
        var save = SAVES.extract(candidate(MechanicType.SAVING_THROW, fragment), fragment, null).payload();
        var condition = CONDITIONS.extract(candidate(MechanicType.CONDITION, fragment), fragment, null).payload();
        var damage = new DamageMechanic(new DiceAmount(1, 6, 0), new FixedDamageType("fire"));
        var result = new ConditionRelationshipStrategy().resolve(List.of(
                extracted("save", MechanicType.SAVING_THROW, save, fragment),
                extracted("condition", MechanicType.CONDITION, condition, fragment),
                extracted("damage", MechanicType.DAMAGE, damage, fragment)), context(fragment));
        assertTrue(result.relationships().isEmpty());
    }

    private static void assertOperation(String text, ConditionName name, ConditionOperation expected) {
        SourceFragment fragment = authored(text);
        StrategyExtraction result = CONDITIONS.extract(candidate(fragment), fragment, null);
        assertTrue(result.supported(), result.explanation());
        ConditionChangeMechanic change = assertInstanceOf(ConditionChangeMechanic.class, result.payload());
        assertEquals(name, change.condition());
        assertEquals(expected, change.operation());
    }

    private static SourceFragment authored(String text) { return authored(text, "/entries/0"); }

    private static SourceFragment authored(String text, String pointer) {
        return new SourceFragment("spell:test#" + pointer, pointer, SourceFragment.Kind.AUTHORED_TEXT,
                text, List.of("entries"), List.of(MechanicType.CONDITION, MechanicType.SAVING_THROW));
    }

    private static SourceFragment fragment(String pointer, SourceFragment.Kind kind, String text) {
        return new SourceFragment("spell:test#" + pointer, pointer, kind, text, List.of("conditionInflict"),
                List.of(MechanicType.CONDITION));
    }

    private static MechanicCandidate candidate(SourceFragment fragment) {
        return candidate(MechanicType.CONDITION, fragment);
    }

    private static MechanicCandidate candidate(MechanicType type, SourceFragment fragment) {
        return new MechanicCandidate(type, fragment.id(), "test-condition", "1", List.of(fragment.pointer()),
                String.join(" > ", fragment.hierarchy()), false);
    }

    private static ExtractedMechanic extracted(String id, MechanicType type, Object payload, SourceFragment fragment) {
        return new ExtractedMechanic(id, type, (com.thedndpub.data.mechanics.model.MechanicPayload) payload,
                payload instanceof SavingThrowRequirementMechanic
                        ? new MechanicContext(new UnknownSubject(UnknownValueReason.NOT_INTERPRETED), List.of())
                        : payload instanceof ConditionChangeMechanic change
                                ? new MechanicContext(new UnknownSubject(UnknownValueReason.NOT_INTERPRETED), List.of())
                                : null,
                fragment.id());
    }

    private static MechanicInterpretationContext context(SourceFragment fragment) {
        return new MechanicInterpretationContext(new SpellId("spell:phb:test"), revision(),
                Map.of(fragment.id(), fragment), Map.of(fragment.id(), "evidence-1"));
    }

    private static SourceArtifactRevision revision() {
        return new SourceArtifactRevision("5eTools v2.37.0", "data/spells/spells-phb.json", "a".repeat(64));
    }
}
