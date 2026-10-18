package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record MechanicsFinding(
        MechanicsFindingCode code,
        FindingSeverity severity,
        String claimPath,
        List<String> evidenceIds) {
    public MechanicsFinding {
        ModelChecks.required(code, "code");
        ModelChecks.required(severity, "severity");
        if (code != MechanicsFindingCode.PARTIAL_COVERAGE && code != MechanicsFindingCode.UNSUPPORTED_CONSTRUCT) {
            ModelChecks.require(severity == FindingSeverity.REQUIRES_REVIEW,
                    "A correctness blocker cannot be downgraded to information");
        }
        ModelChecks.pointer(claimPath);
        evidenceIds = ModelChecks.list(evidenceIds);
        evidenceIds.forEach(id -> ModelChecks.text(id, "evidenceId"));
    }
}
