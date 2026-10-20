package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/** A source-backed description of an attack roll, not an executable combat instruction. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record AttackRollMechanic(
        AttackKind attackKind,
        AttackActorRole actorRole,
        Integer attackCount,
        boolean attacksIndependent,
        AttackActivationTiming activationTiming,
        InstanceCountProgression countProgression,
        InstanceTargetAllocation targetAllocation,
        SelectedTargetCount selectedTargetCount) implements MechanicPayload {
    public AttackRollMechanic {
        ModelChecks.required(attackKind, "attackKind");
        ModelChecks.required(actorRole, "actorRole");
        ModelChecks.required(activationTiming, "activationTiming");
        if (selectedTargetCount != null) {
            ModelChecks.require(attackCount == null,
                    "Selected-target attack counts cannot also have a fixed attack count");
            ModelChecks.require(attacksIndependent,
                    "Selected-target attack counts require independent attacks");
            ModelChecks.require(countProgression == null,
                    "Selected-target attack counts cannot use level-based progression");
            ModelChecks.require(targetAllocation != null
                            && targetAllocation.cardinality() == TargetSelectionCardinality.EXACTLY_ONE_PER_INSTANCE
                            && targetAllocation.targetReusePolicy() == TargetReusePolicy.DISTINCT_TARGETS_ONLY
                            && targetAllocation.eligibleTargetCategories().equals(
                                    java.util.List.of(TargetCategory.CREATURE))
                            && targetAllocation.targetCategoryEligibilityStatus()
                                    == TargetCategoryEligibilityStatus.SOURCE_SPECIFIED,
                    "Selected-target attacks require one distinct source-eligible target per attack");
            ModelChecks.require(targetAllocation.withinSpellRange()
                            && targetAllocation.requirements().contains(InstanceTargetRequirement.VISIBLE_TO_CASTER),
                    "Selected-target attacks require source-specified visibility and range constraints");
        } else {
            ModelChecks.require(attackCount != null && attackCount > 0, "attackCount must be positive");
        }
        if (countProgression instanceof CharacterLevelInstanceCount characterLevel) {
            ModelChecks.require(attacksIndependent,
                    "Character-level instance progression requires independent attack rolls");
            int previousCount = attackCount;
            for (CharacterLevelInstanceCountTier tier : characterLevel.tiers()) {
                ModelChecks.require(tier.totalInstances() > previousCount,
                        "Progressive total instance counts must exceed the preceding total");
                previousCount = tier.totalInstances();
            }
        }
        if (countProgression instanceof SpellSlotInstanceCount spellSlot) {
            ModelChecks.require(attacksIndependent,
                    "Spell-slot instance progression requires independent attack rolls");
            ModelChecks.require(attackCount == spellSlot.baseInstanceCount(),
                    "Base attack count must match the spell-slot progression");
        }
        if (selectedTargetCount == null && targetAllocation != null
                && targetAllocation.targetReusePolicy() == TargetReusePolicy.DISTINCT_TARGETS_ONLY) {
            int maximumInstances = attackCount;
            if (countProgression instanceof CharacterLevelInstanceCount characterLevel) {
                maximumInstances = characterLevel.tiers().getLast().totalInstances();
            } else if (countProgression instanceof SpellSlotInstanceCount spellSlot) {
                maximumInstances = spellSlot.totalInstancesAtSlotLevel(9);
            }
            ModelChecks.require(maximumInstances > 1,
                    "Distinct target allocation requires multiple possible attack instances");
        }
    }

    public AttackRollMechanic(
            AttackKind attackKind,
            AttackActorRole actorRole,
            int attackCount,
            boolean attacksIndependent,
            AttackActivationTiming activationTiming) {
        this(attackKind, actorRole, attackCount, attacksIndependent, activationTiming, null, null, null);
    }

    public AttackRollMechanic(
            AttackKind attackKind,
            AttackActorRole actorRole,
            int attackCount,
            boolean attacksIndependent,
            AttackActivationTiming activationTiming,
            InstanceCountProgression countProgression,
            InstanceTargetAllocation targetAllocation) {
        this(attackKind, actorRole, Integer.valueOf(attackCount), attacksIndependent, activationTiming,
                countProgression, targetAllocation, null);
    }
}
