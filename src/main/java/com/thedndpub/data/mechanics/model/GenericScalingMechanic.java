package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/** Describes authored scaling; it is not an evaluator or a change to the base mechanic. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record GenericScalingMechanic(
        ScalingAxis axis,
        ScalingProgressionType progressionType,
        ScalingQuantity quantity,
        Integer baseLevel,
        List<ScalingStep> steps) implements MechanicPayload {
    public GenericScalingMechanic {
        ModelChecks.required(axis, "axis");
        ModelChecks.required(progressionType, "progressionType");
        ModelChecks.required(quantity, "quantity");
        steps = ModelChecks.list(steps);
        ModelChecks.require(!steps.isEmpty(), "Scaling requires at least one step");
        if (baseLevel != null) {
            ModelChecks.require(baseLevel > 0, "Scaling base level must be positive");
        }
        int previous = 0;
        for (ScalingStep step : steps) {
            ModelChecks.required(step, "step");
            ModelChecks.require(step.minimumLevel() > previous, "Scaling thresholds must be strictly increasing");
            previous = step.minimumLevel();
            int maximum = axis == ScalingAxis.SPELL_SLOT_LEVEL ? 9 : 20;
            ModelChecks.require(step.minimumLevel() <= maximum, "Scaling threshold is outside its level axis");
        }
        if (progressionType == ScalingProgressionType.PER_LEVEL_INCREMENT) {
            ModelChecks.require(baseLevel != null && steps.getFirst().minimumLevel() > baseLevel,
                    "Per-level scaling requires a base level and a later first threshold");
        }
    }
}
