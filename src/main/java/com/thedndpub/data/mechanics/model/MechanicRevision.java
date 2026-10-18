package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record MechanicRevision(
        @JsonProperty(required = true) long number,
        String contentSha256,
        MechanicRevisionReference supersedes) {
    public MechanicRevision {
        ModelChecks.require(number > 0, "Revision number must be positive");
        ModelChecks.digest(contentSha256);
        ModelChecks.require((number == 1) == (supersedes == null), "Only first revisions omit supersession");
        if (supersedes != null) {
            ModelChecks.require(supersedes.number() == number - 1, "Revision must supersede its immediate predecessor");
        }
    }

    public MechanicRevisionReference reference(MechanicId id) {
        if (supersedes != null) {
            ModelChecks.require(supersedes.id().equals(id), "Supersession belongs to another mechanic");
        }
        return new MechanicRevisionReference(id, number, contentSha256);
    }
}
