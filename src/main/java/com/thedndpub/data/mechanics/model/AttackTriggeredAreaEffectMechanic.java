package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/** A single area effect that follows one attack regardless of its roll outcome. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record AttackTriggeredAreaEffectMechanic(
        MechanicRevisionReference attackRoll,
        AttackEffectTrigger trigger,
        AreaEffectCenter center,
        int radiusFeet,
        boolean includesAttackTarget,
        List<TargetCategory> affectedTargetCategories,
        AreaEffectFrequency frequency) implements MechanicPayload {
    public AttackTriggeredAreaEffectMechanic {
        ModelChecks.required(attackRoll, "attackRoll");
        ModelChecks.required(trigger, "trigger");
        ModelChecks.required(center, "center");
        ModelChecks.require(radiusFeet > 0, "Area radius must be positive");
        affectedTargetCategories = ModelChecks.list(affectedTargetCategories);
        ModelChecks.require(!affectedTargetCategories.isEmpty(), "Area effects require an affected target category");
        ModelChecks.require(affectedTargetCategories.stream().distinct().count() == affectedTargetCategories.size(),
                "Affected target categories must be unique");
        ModelChecks.required(frequency, "frequency");
        ModelChecks.require(trigger == AttackEffectTrigger.AFTER_ATTACK_REGARDLESS_OF_OUTCOME,
                "Only outcome-independent post-attack area effects are supported");
        ModelChecks.require(center == AreaEffectCenter.ATTACK_TARGET,
                "Only areas centered on the triggering attack target are supported");
        ModelChecks.require(includesAttackTarget, "The triggering attack target must be included in this area");
        ModelChecks.require(frequency == AreaEffectFrequency.ONCE_PER_ATTACK,
                "The area effect occurs once per triggering attack, not per affected creature");
    }
}
