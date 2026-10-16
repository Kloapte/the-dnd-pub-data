package com.thedndpub.data.dto.sense;

import com.fasterxml.jackson.annotation.JsonValue;

public record SenseId(String value) {
    public SenseId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Sense ID must not be blank");
        }
    }

    @JsonValue
    public String serializedValue() {
        return value;
    }
}
