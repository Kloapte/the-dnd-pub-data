package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record UnavailableOriginMechanicsEvidence(
        String id,
        SourceReference source,
        MechanicsOwnerReference effectiveOwner,
        String effectivePointer,
        String valueSha256,
        UnknownValueReason reason) implements MechanicsEvidence {
    public UnavailableOriginMechanicsEvidence {
        ModelChecks.text(id, "id");
        ModelChecks.required(source, "source");
        ModelChecks.required(effectiveOwner, "effectiveOwner");
        ModelChecks.required(reason, "reason");
        ModelChecks.pointer(effectivePointer);
        ModelChecks.digest(valueSha256);
    }
}
