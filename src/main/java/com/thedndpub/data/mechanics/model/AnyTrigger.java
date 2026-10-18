package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record AnyTrigger(
        List<TriggerExpression> alternatives) implements TriggerExpression {
    public AnyTrigger {
        alternatives = ModelChecks.list(alternatives);
        ModelChecks.require(!alternatives.isEmpty(), "An alternative group must contain triggers");
    }
}
