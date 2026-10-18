package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record EstablishedReuseBasis(
        String id,
        ClaimRightsScope scope,
        List<RightsEvidenceReference> evidence,
        RightsDecisionReference decision) {
    public EstablishedReuseBasis {
        ModelChecks.text(id, "reuse basis ID");
        ModelChecks.required(scope, "scope");
        ModelChecks.required(decision, "decision");
        evidence = ModelChecks.list(evidence);
        ModelChecks.require(scope.coversWholeClaim() && !evidence.isEmpty(), "OPEN requires a documented whole-claim basis");
    }
}
