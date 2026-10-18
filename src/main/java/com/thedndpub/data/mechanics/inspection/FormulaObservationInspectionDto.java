package com.thedndpub.data.mechanics.inspection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.thedndpub.data.mechanics.model.ModelChecks;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record FormulaObservationInspectionDto(
        String id,
        String spellId,
        @JsonProperty(required = true) int occurrenceIndex,
        String sourceRevisionId,
        EvidenceLocatorDto evidence) implements MechanicsObservationDto {
    public FormulaObservationInspectionDto {
        ModelChecks.text(id, "id");
        ModelChecks.text(spellId, "spellId");
        ModelChecks.require(occurrenceIndex >= 0, "Occurrence index cannot be negative");
        ModelChecks.text(sourceRevisionId, "sourceRevisionId");
        ModelChecks.required(evidence, "evidence");
    }
}
