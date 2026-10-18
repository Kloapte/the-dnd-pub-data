package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record DamageTypeChoice(
        List<String> alternatives,
        Integer count) implements DamageTypeSelection {
    public DamageTypeChoice {
        alternatives = ModelChecks.list(alternatives);
        ModelChecks.require(!alternatives.isEmpty(), "Damage type choices require alternatives");
        alternatives.forEach(value -> ModelChecks.text(value, "damage type"));
        if (count != null) {
            ModelChecks.require(count > 0 && count <= alternatives.size(), "Invalid damage type choice count");
        }
    }
}
