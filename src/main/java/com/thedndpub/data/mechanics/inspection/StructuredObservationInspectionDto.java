package com.thedndpub.data.mechanics.inspection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.mechanics.model.ModelChecks;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record StructuredObservationInspectionDto(
        String id,
        String normalizedProperty,
        String sourceRevisionId,
        String jsonPointer) implements MechanicsObservationDto {
    public StructuredObservationInspectionDto {
        ModelChecks.text(id, "id");
        ModelChecks.text(normalizedProperty, "normalizedProperty");
        ModelChecks.text(sourceRevisionId, "sourceRevisionId");
        ModelChecks.pointer(jsonPointer);
    }
}
