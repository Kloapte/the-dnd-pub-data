package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record MechanicContext(
        SubjectReference subject,
        List<MechanicContextBinding> bindings) {
    public MechanicContext {
        ModelChecks.required(subject, "subject");
        bindings = ModelChecks.list(bindings);
    }
}
