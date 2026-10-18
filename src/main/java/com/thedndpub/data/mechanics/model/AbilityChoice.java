package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record AbilityChoice(
        List<Ability> alternatives,
        Integer count) implements AbilitySelection {
    public AbilityChoice {
        alternatives = ModelChecks.list(alternatives);
        ModelChecks.require(!alternatives.isEmpty(), "Ability choices require alternatives");
        if (count != null) {
            ModelChecks.require(count > 0 && count <= alternatives.size(), "Invalid ability choice count");
        }
    }
}
