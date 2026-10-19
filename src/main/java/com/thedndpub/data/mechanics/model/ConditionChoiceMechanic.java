package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.HashSet;
import java.util.List;

/** A mutually exclusive selection among condition operations; this is descriptive, not executable. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ConditionChoiceMechanic(
        ConditionOperation operation,
        List<ConditionName> alternatives,
        int choicesToSelect) implements MechanicPayload {
    public ConditionChoiceMechanic {
        ModelChecks.required(operation, "operation");
        alternatives = ModelChecks.list(alternatives);
        ModelChecks.require(alternatives.size() >= 2, "Condition choice requires at least two alternatives");
        ModelChecks.require(new HashSet<>(alternatives).size() == alternatives.size(),
                "Condition choice alternatives must be unique");
        ModelChecks.require(choicesToSelect == 1, "Exactly one condition alternative must be selected");
    }
}
