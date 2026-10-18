package com.thedndpub.data.mechanics.inspection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.mechanics.model.ModelChecks;
import com.thedndpub.data.mechanics.model.UnknownValueReason;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record UnavailableEvidenceLocatorDto(
        String id,
        String effectiveOwnerId,
        String effectivePointer,
        UnknownValueReason reason) implements EvidenceLocatorDto {
    public UnavailableEvidenceLocatorDto {
        ModelChecks.text(id, "id");
        ModelChecks.text(effectiveOwnerId, "effectiveOwnerId");
        ModelChecks.pointer(effectivePointer);
        ModelChecks.required(reason, "reason");
    }
}
