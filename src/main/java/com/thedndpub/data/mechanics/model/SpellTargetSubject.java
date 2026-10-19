package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/** The source-specified target scope for an effect; it does not select or execute targets. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SpellTargetSubject(
        TargetScope scope,
        CreatureTypeRestriction creatureTypeRestriction,
        Integer maximumTargets) implements SubjectReference {
    public SpellTargetSubject {
        ModelChecks.required(scope, "scope");
        ModelChecks.require(creatureTypeRestriction == null || scope == TargetScope.SINGLE_TARGET_CREATURE
                        || scope == TargetScope.MULTIPLE_TARGET_CREATURES,
                "Creature type restrictions require selected creature targets");
        ModelChecks.require(maximumTargets == null || maximumTargets > 0
                        && scope == TargetScope.MULTIPLE_TARGET_CREATURES,
                "Maximum target counts require multiple selected creatures");
    }

    public SpellTargetSubject(TargetScope scope) {
        this(scope, null, null);
    }

    public SpellTargetSubject(TargetScope scope, CreatureTypeRestriction creatureTypeRestriction) {
        this(scope, creatureTypeRestriction, null);
    }
}
