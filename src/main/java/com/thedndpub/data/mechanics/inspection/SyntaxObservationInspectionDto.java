package com.thedndpub.data.mechanics.inspection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.mechanics.model.ModelChecks;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SyntaxObservationInspectionDto(
        String id,
        String tagName,
        EvidenceLocatorDto evidence) implements MechanicsObservationDto {
    public SyntaxObservationInspectionDto {
        ModelChecks.text(id, "id");
        ModelChecks.text(tagName, "tagName");
        ModelChecks.required(evidence, "evidence");
    }
}
