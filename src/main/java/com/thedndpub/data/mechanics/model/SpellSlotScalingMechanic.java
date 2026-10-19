package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/** Authored per-slot-level increment; this value is descriptive and is never evaluated. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SpellSlotScalingMechanic(
        ScalingEffect effect,
        AmountExpression increment,
        int baseSlotLevel,
        int minimumCastingSlotLevel,
        int slotLevelInterval) implements MechanicPayload {
    public SpellSlotScalingMechanic {
        ModelChecks.required(effect, "effect");
        ModelChecks.required(increment, "increment");
        ModelChecks.require(baseSlotLevel > 0, "baseSlotLevel must be positive");
        ModelChecks.require(minimumCastingSlotLevel > baseSlotLevel,
                "minimumCastingSlotLevel must exceed baseSlotLevel");
        ModelChecks.require(slotLevelInterval > 0, "slotLevelInterval must be positive");
    }
}
