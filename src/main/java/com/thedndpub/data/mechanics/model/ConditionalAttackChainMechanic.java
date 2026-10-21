package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/** Declarative bounds and trigger for source-supported conditional spell attack chaining. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ConditionalAttackChainMechanic(
        MechanicRevisionReference initialAttack,
        MechanicRevisionReference reusableDamage,
        DamageDiceMatchRule continuationTrigger,
        boolean atMostOneLeapPerQualifyingRoll,
        ChainTargetCategory subsequentTargetCategory,
        ChainTargetReusePolicy targetReusePolicy,
        ChainTargetDistance targetDistance,
        SpellSlotLeapLimit leapLimit,
        DamageTypeChoiceTiming damageTypeChoiceTiming,
        boolean damageTypePersistsAcrossLeaps,
        boolean eachNewTargetRequiresAttackRoll,
        AdditionalAttackMissBehavior additionalAttackMissBehavior,
        boolean maximumLeapsCountsInitialAttack) implements MechanicPayload {
    public ConditionalAttackChainMechanic {
        ModelChecks.required(initialAttack, "initialAttack");
        ModelChecks.required(reusableDamage, "reusableDamage");
        ModelChecks.required(continuationTrigger, "continuationTrigger");
        ModelChecks.require(atMostOneLeapPerQualifyingRoll,
                "Each qualifying damage roll can cause at most one leap");
        ModelChecks.required(subsequentTargetCategory, "subsequentTargetCategory");
        ModelChecks.required(targetReusePolicy, "targetReusePolicy");
        ModelChecks.required(targetDistance, "targetDistance");
        ModelChecks.required(leapLimit, "leapLimit");
        ModelChecks.required(damageTypeChoiceTiming, "damageTypeChoiceTiming");
        ModelChecks.required(additionalAttackMissBehavior, "additionalAttackMissBehavior");
        ModelChecks.require(continuationTrigger.damage().equals(reusableDamage),
                "The matching-dice trigger must use the reusable damage definition");
        ModelChecks.require(eachNewTargetRequiresAttackRoll,
                "Each conditional leap requires a new attack roll");
    }
}
