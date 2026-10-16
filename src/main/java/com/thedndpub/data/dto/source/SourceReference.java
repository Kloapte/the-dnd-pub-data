package com.thedndpub.data.dto.source;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/** Reference to a source identity with an optional page locator. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SourceReference(
        @JsonProperty("source") SourceIdentity identity,
        Integer page) {
    public SourceReference {
        Objects.requireNonNull(identity, "identity");
    }

    public SourceReference(String source, Integer page) {
        this(new SourceIdentity(source), page);
    }
}
