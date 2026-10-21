package com.thedndpub.data.mechanics.experimental;

import com.thedndpub.data.mechanics.model.ModelChecks;
import com.thedndpub.data.mechanics.model.RepeatSaveTiming;

public record ConditionTerminationRule(
        ConditionTerminationTrigger trigger,
        RepeatSaveTiming saveTiming) {
    public ConditionTerminationRule {
        ModelChecks.required(trigger, "trigger");
        if (trigger == ConditionTerminationTrigger.REPEATED_SAVE_SUCCESS) {
            ModelChecks.required(saveTiming, "saveTiming");
        } else {
            ModelChecks.require(saveTiming == null, "Non-save termination has no repeated-save timing");
        }
    }
}
