package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record IntegerModifier(
        @JsonProperty(required = true) int value,
        String authoredToken) implements ModifierAmount {
    public IntegerModifier {
        if (authoredToken != null) {
            ModelChecks.require(authoredToken.matches("[+-]?[0-9]+"), "Unsupported integer modifier token");
            ModelChecks.require(Integer.parseInt(authoredToken) == value, "Modifier token/value mismatch");
        }
    }
}
