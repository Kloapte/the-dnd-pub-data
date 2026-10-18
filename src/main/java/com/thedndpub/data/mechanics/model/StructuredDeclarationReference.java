package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record StructuredDeclarationReference(
        String id,
        String normalizedProperty,
        SourceArtifactRevision source,
        String jsonPointer) implements MechanicsObservationReference {
    public StructuredDeclarationReference {
        ModelChecks.text(id, "id");
        ModelChecks.text(normalizedProperty, "normalizedProperty");
        ModelChecks.required(source, "source");
        ModelChecks.pointer(jsonPointer);
    }
}
