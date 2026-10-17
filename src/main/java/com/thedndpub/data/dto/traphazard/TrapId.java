package com.thedndpub.data.dto.traphazard;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record TrapId(String value) {
    public TrapId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Trap ID must not be blank");
        }
    }
}
