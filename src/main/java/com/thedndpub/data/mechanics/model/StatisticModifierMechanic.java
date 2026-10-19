package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record StatisticModifierMechanic(
        List<ModifiedStatistic> statistics,
        ModifierOperation operation,
        AmountExpression amount) implements MechanicPayload {
    public StatisticModifierMechanic {
        statistics = ModelChecks.list(statistics);
        ModelChecks.require(!statistics.isEmpty(), "A statistic modifier requires at least one statistic");
        ModelChecks.required(operation, "operation");
        ModelChecks.required(amount, "amount");
        ModelChecks.require(!(amount instanceof UnknownAmount), "Unknown modifier amounts must remain uninterpreted");
    }
}
