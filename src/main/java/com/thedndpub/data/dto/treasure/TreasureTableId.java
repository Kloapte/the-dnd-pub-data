package com.thedndpub.data.dto.treasure;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record TreasureTableId(String value) {
    public TreasureTableId {
        Objects.requireNonNull(value, "value");
        if (value.isBlank()) {
            throw new IllegalArgumentException("Treasure table ID must not be blank");
        }
    }

    @JsonValue
    public String serializedValue() {
        return value;
    }
}
