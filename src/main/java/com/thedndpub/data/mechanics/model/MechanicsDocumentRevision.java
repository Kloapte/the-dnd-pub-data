package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record MechanicsDocumentRevision(
        @JsonProperty(required = true) long number,
        String contentSha256) {
    public MechanicsDocumentRevision {
        ModelChecks.require(number > 0, "Document revision must be positive");
        ModelChecks.digest(contentSha256);
    }
}
