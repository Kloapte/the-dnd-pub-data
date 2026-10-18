package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record UnknownSubject(
        UnknownValueReason reason) implements SubjectReference {
    public UnknownSubject {
        ModelChecks.required(reason, "reason");
    }
}
