package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/** A source-backed condition operation. Qualifier phrases are exact authored text, never executable rules. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ConditionChangeMechanic(
        ConditionOperation operation,
        ConditionName condition,
        List<String> qualifiers) implements MechanicPayload {
    public ConditionChangeMechanic {
        ModelChecks.required(operation, "operation");
        ModelChecks.required(condition, "condition");
        qualifiers = ModelChecks.list(qualifiers);
    }
}
