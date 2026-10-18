package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record UnknownSaveDifficulty(
        UnknownValueReason reason) implements SaveDifficulty {
    public UnknownSaveDifficulty {
        ModelChecks.required(reason, "reason");
    }
}
