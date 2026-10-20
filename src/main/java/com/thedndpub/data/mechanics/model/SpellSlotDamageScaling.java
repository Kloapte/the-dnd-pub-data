package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/** Typed amount increase for a damage claim at each qualifying spell-slot level. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SpellSlotDamageScaling(
        DiceAmount increment,
        int baseSlotLevel,
        int minimumCastingSlotLevel,
        int slotLevelInterval) {
    public SpellSlotDamageScaling {
        ModelChecks.required(increment, "increment");
        ModelChecks.require(baseSlotLevel >= 1 && baseSlotLevel <= 9,
                "Base spell slot level must be between 1 and 9");
        ModelChecks.require(minimumCastingSlotLevel > baseSlotLevel && minimumCastingSlotLevel <= 9,
                "Minimum casting slot level must exceed the base and not exceed 9");
        ModelChecks.require(slotLevelInterval > 0, "Slot level interval must be positive");
    }

    public int incrementsAtSlotLevel(int slotLevel) {
        ModelChecks.require(slotLevel >= baseSlotLevel && slotLevel <= 9,
                "Slot level must be between the base spell level and 9");
        if (slotLevel < minimumCastingSlotLevel) {
            return 0;
        }
        return 1 + (slotLevel - minimumCastingSlotLevel) / slotLevelInterval;
    }
}
