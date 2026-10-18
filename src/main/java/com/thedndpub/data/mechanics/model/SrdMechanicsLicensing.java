package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SrdMechanicsLicensing(
        List<LicenseCoverage> coverage) implements MechanicsLicensing {
    public SrdMechanicsLicensing {
        coverage = ModelChecks.list(coverage);
        ModelChecks.require(!coverage.isEmpty(), "Established classification requires claim-scoped coverage");
        for (LicenseCoverage item : coverage) {
            ModelChecks.require(item.scope().coversWholeClaim(), "A token-only license cannot classify a complete mechanic");
            ModelChecks.require(item.scope().claim().equals(coverage.getFirst().scope().claim()),
                    "Mixed claim revisions in coverage");
            ModelChecks.require(item.document().id().startsWith("SRD-"), "SRD classification requires an SRD document reference");
        }
    }

    @Override
    public MechanicsLicensingStatus status() {
        return MechanicsLicensingStatus.SRD;
    }
}
