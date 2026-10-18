package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record UnsupportedMechanicsConstruct(
        String id,
        MechanicFamily candidateFamily,
        UnsupportedReason reason,
        List<String> evidenceIds,
        List<MechanicRevisionReference> alternatives) {
    public UnsupportedMechanicsConstruct {
        ModelChecks.text(id, "id");
        ModelChecks.required(candidateFamily, "candidateFamily");
        ModelChecks.required(reason, "reason");
        evidenceIds = ModelChecks.list(evidenceIds);
        alternatives = ModelChecks.list(alternatives);
        ModelChecks.require(!evidenceIds.isEmpty(), "Unsupported constructs require source evidence");
    }
}
