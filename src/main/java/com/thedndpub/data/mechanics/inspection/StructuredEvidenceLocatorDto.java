package com.thedndpub.data.mechanics.inspection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.mechanics.model.MechanicsOrigin;
import com.thedndpub.data.mechanics.model.ModelChecks;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record StructuredEvidenceLocatorDto(
        String id,
        SourceReference source,
        String sourceRevisionId,
        String ownerPointer,
        String jsonPointer,
        MechanicsOrigin origin) implements EvidenceLocatorDto {
    public StructuredEvidenceLocatorDto {
        ModelChecks.text(id, "id");
        ModelChecks.required(source, "source");
        ModelChecks.text(sourceRevisionId, "sourceRevisionId");
        ModelChecks.pointer(ownerPointer);
        ModelChecks.pointer(jsonPointer);
        ModelChecks.required(origin, "origin");
    }
}
