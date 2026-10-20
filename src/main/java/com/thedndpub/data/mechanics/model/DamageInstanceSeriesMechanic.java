package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/** A count-scaled series of independently assigned damage instances without attack rolls. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record DamageInstanceSeriesMechanic(
        AmountExpression amountPerInstance,
        DamageTypeSelection damageType,
        SpellSlotInstanceCount countProgression,
        InstanceTargetAllocation targetAllocation,
        DamageResolutionMode resolutionMode,
        EffectTiming timing,
        DamageRollGrouping damageRollGrouping) implements MechanicPayload {
    public DamageInstanceSeriesMechanic {
        ModelChecks.required(amountPerInstance, "amountPerInstance");
        ModelChecks.required(damageType, "damageType");
        ModelChecks.required(countProgression, "countProgression");
        ModelChecks.required(targetAllocation, "targetAllocation");
        ModelChecks.required(resolutionMode, "resolutionMode");
        ModelChecks.required(timing, "timing");
        ModelChecks.required(damageRollGrouping, "damageRollGrouping");
        ModelChecks.require(targetAllocation.cardinality() == TargetSelectionCardinality.EXACTLY_ONE_PER_INSTANCE,
                "A damage instance must select exactly one target");
    }
}
