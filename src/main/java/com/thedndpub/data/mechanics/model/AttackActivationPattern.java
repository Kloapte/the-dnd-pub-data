package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/** Initial and repeated attack opportunities linked to one persistent spell effect. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record AttackActivationPattern(
        AttackActivationTiming initialActivation,
        AttackActivationTiming repeatedActivation,
        boolean repeatsOnlyWhileEffectPersists,
        int attacksPerActivation,
        MechanicRevisionReference persistentEffect) {
    public AttackActivationPattern {
        ModelChecks.required(initialActivation, "initialActivation");
        ModelChecks.required(repeatedActivation, "repeatedActivation");
        ModelChecks.required(persistentEffect, "persistentEffect");
        ModelChecks.require(initialActivation == AttackActivationTiming.CAST_TIME
                        && repeatedActivation == AttackActivationTiming.BONUS_ACTION,
                "Spiritual Weapon activations must occur at cast time and later through a bonus action");
        ModelChecks.require(repeatsOnlyWhileEffectPersists,
                "Repeated attack opportunities must be bounded by effect persistence");
        ModelChecks.require(attacksPerActivation == 1,
                "Each Spiritual Weapon activation permits exactly one attack");
    }
}
