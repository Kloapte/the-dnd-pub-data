package com.thedndpub.data.dto.encounter;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record EncounterId(String value) {
    public EncounterId {
        Objects.requireNonNull(value, "value");
        if (value.isBlank()) {
            throw new IllegalArgumentException("Encounter ID must not be blank");
        }
    }
}
