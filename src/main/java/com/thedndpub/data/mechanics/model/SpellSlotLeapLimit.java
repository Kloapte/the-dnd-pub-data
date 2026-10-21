package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/** Maximum conditional leaps, separate from the initial attack and damage-dice count. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SpellSlotLeapLimit(int minimumSlotLevel, int baseSlotLevel, int baseMaximumLeaps,
                                 int additionalLeapsPerSlotLevel) {
    public SpellSlotLeapLimit {
        ModelChecks.require(minimumSlotLevel >= 1 && minimumSlotLevel <= 9,
                "Minimum slot level must be between 1 and 9");
        ModelChecks.require(baseSlotLevel >= minimumSlotLevel && baseSlotLevel <= 9,
                "Base slot level must be between minimum and 9");
        ModelChecks.require(baseMaximumLeaps >= 0, "Base maximum leaps cannot be negative");
        ModelChecks.require(additionalLeapsPerSlotLevel >= 0,
                "Additional leaps per slot level cannot be negative");
    }

    public int maximumLeapsAtSlotLevel(int slotLevel) {
        ModelChecks.require(slotLevel >= minimumSlotLevel && slotLevel <= 9,
                "Slot level must be between minimum and 9");
        long maximum = (long) baseMaximumLeaps
                + (long) (slotLevel - baseSlotLevel) * additionalLeapsPerSlotLevel;
        ModelChecks.require(maximum >= 0 && maximum <= Integer.MAX_VALUE,
                "Spell-slot leap limit is invalid");
        return (int) maximum;
    }
}
