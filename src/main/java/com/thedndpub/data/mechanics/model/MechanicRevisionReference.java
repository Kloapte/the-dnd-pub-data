package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record MechanicRevisionReference(
        MechanicId id,
        @JsonProperty(required = true) long number,
        String contentSha256) {
    public MechanicRevisionReference {
        ModelChecks.required(id, "id");
        ModelChecks.require(number > 0, "Revision number must be positive");
        ModelChecks.digest(contentSha256);
    }
}
