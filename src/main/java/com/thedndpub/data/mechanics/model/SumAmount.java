package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SumAmount(
        List<AmountExpression> terms) implements AmountExpression {
    public SumAmount {
        terms = ModelChecks.list(terms);
        ModelChecks.require(terms.size() >= 2, "A sum must preserve at least two authored terms");
    }
}
