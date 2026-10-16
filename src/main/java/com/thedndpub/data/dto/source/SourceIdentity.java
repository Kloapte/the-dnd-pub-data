package com.thedndpub.data.dto.source;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;

/** Stable source-code identity, retaining the spelling used by the source dataset. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SourceIdentity(String code) {
    public SourceIdentity {
        Objects.requireNonNull(code, "code");
        if (code.isBlank()) {
            throw new IllegalArgumentException("Source code must not be blank");
        }
    }

    @JsonValue
    public String serializedCode() {
        return code;
    }
}
