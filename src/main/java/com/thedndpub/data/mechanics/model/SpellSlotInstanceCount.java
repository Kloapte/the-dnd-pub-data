package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/** Slot-level progression for total independently resolved instances. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SpellSlotInstanceCount(
        int baseSpellLevel,
        int baseInstanceCount,
        int instancesAddedPerSlotLevel) implements InstanceCountProgression {
    public SpellSlotInstanceCount {
        ModelChecks.require(baseSpellLevel >= 1 && baseSpellLevel <= 9,
                "Base spell level must be between 1 and 9");
        ModelChecks.require(baseInstanceCount > 0, "Base instance count must be positive");
        ModelChecks.require(instancesAddedPerSlotLevel > 0,
                "Instances added per slot level must be positive");
    }

    /** Returns total instances at a valid slot level and rejects lower or overflowing progressions. */
    public int totalInstancesAtSlotLevel(int slotLevel) {
        ModelChecks.require(slotLevel >= baseSpellLevel && slotLevel <= 9,
                "Slot level must be between the base spell level and 9");
        long total = (long) baseInstanceCount
                + (long) (slotLevel - baseSpellLevel) * instancesAddedPerSlotLevel;
        ModelChecks.require(total <= Integer.MAX_VALUE, "Slot-level instance count overflows an integer");
        return (int) total;
    }
}
