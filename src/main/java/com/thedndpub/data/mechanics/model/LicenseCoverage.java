package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record LicenseCoverage(
        ClaimRightsScope scope,
        LicensedDocumentReference document,
        LicenseReference license,
        List<RightsEvidenceReference> evidence,
        List<AttributionReference> attribution,
        RightsDecisionReference decision) {
    public LicenseCoverage {
        ModelChecks.required(scope, "scope");
        ModelChecks.required(document, "document");
        ModelChecks.required(license, "license");
        ModelChecks.required(decision, "decision");
        evidence = ModelChecks.list(evidence);
        attribution = ModelChecks.list(attribution);
        ModelChecks.require(!evidence.isEmpty() && !attribution.isEmpty(), "Established licensing requires evidence and attribution");
    }
}
