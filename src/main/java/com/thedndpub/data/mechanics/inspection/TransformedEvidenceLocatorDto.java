package com.thedndpub.data.mechanics.inspection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.mechanics.model.ModelChecks;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record TransformedEvidenceLocatorDto(
        String id,
        String effectiveOwnerId,
        String effectivePointer,
        List<String> inputEvidenceIds) implements EvidenceLocatorDto {
    public TransformedEvidenceLocatorDto {
        ModelChecks.text(id, "id");
        ModelChecks.text(effectiveOwnerId, "effectiveOwnerId");
        ModelChecks.pointer(effectivePointer);
        inputEvidenceIds = ModelChecks.list(inputEvidenceIds);
    }
}
