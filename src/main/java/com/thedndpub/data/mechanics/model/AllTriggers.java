package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record AllTriggers(
        List<TriggerExpression> requirements) implements TriggerExpression {
    public AllTriggers {
        requirements = ModelChecks.list(requirements);
        ModelChecks.require(!requirements.isEmpty(), "A conjunction must contain triggers");
    }
}
