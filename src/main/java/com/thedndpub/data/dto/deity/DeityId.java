package com.thedndpub.data.dto.deity;

import com.fasterxml.jackson.annotation.JsonValue;

public record DeityId(String value) {
    public DeityId {
        if (value == null || !value.startsWith("deity:")) {
            throw new IllegalArgumentException("Deity ID must start with deity:");
        }
    }

    @JsonValue
    public String serializedValue() {
        return value;
    }
}
