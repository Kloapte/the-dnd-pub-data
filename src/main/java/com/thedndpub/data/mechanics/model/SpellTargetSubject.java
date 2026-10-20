package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/** The source-specified target scope for an effect; it does not select or execute targets. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SpellTargetSubject(
        TargetScope scope,
        CreatureTypeRestriction creatureTypeRestriction,
        Integer maximumTargets,
        List<TargetCategory> eligibleTargetCategories) implements SubjectReference {
    public SpellTargetSubject {
        ModelChecks.required(scope, "scope");
        ModelChecks.require(eligibleTargetCategories == null
                        || eligibleTargetCategories.stream().allMatch(java.util.Objects::nonNull),
                "Eligible target categories cannot contain null values");
        eligibleTargetCategories = eligibleTargetCategories == null
                ? null : ModelChecks.list(eligibleTargetCategories);
        if (scope == TargetScope.SINGLE_TARGET) {
            ModelChecks.require(creatureTypeRestriction == null && maximumTargets == null,
                    "A single target category choice has fixed cardinality and no creature-only restriction");
            ModelChecks.require(List.of(TargetCategory.CREATURE, TargetCategory.OBJECT)
                            .equals(eligibleTargetCategories),
                    "Single-target alternatives must be CREATURE and OBJECT in canonical order");
        } else if (scope == TargetScope.SINGLE_TARGET_UNSPECIFIED_CATEGORY) {
            ModelChecks.require(creatureTypeRestriction == null && maximumTargets == null
                            && eligibleTargetCategories == null,
                    "Unspecified-category single targets cannot assert target qualifications");
        } else {
            ModelChecks.require(eligibleTargetCategories == null,
                    "Eligible target alternatives require the single-target scope");
        }
        ModelChecks.require(creatureTypeRestriction == null || scope == TargetScope.SINGLE_TARGET_CREATURE
                        || scope == TargetScope.MULTIPLE_TARGET_CREATURES,
                "Creature type restrictions require selected creature targets");
        ModelChecks.require(maximumTargets == null || maximumTargets > 0
                        && scope == TargetScope.MULTIPLE_TARGET_CREATURES,
                "Maximum target counts require multiple selected creatures");
    }

    public SpellTargetSubject(TargetScope scope) {
        this(scope, null, null, null);
    }

    public SpellTargetSubject(TargetScope scope, CreatureTypeRestriction creatureTypeRestriction) {
        this(scope, creatureTypeRestriction, null, null);
    }

    public SpellTargetSubject(TargetScope scope, CreatureTypeRestriction creatureTypeRestriction,
                              Integer maximumTargets) {
        this(scope, creatureTypeRestriction, maximumTargets, null);
    }

    public SpellTargetSubject(TargetScope scope, List<TargetCategory> eligibleTargetCategories) {
        this(scope, null, null, eligibleTargetCategories);
    }
}
