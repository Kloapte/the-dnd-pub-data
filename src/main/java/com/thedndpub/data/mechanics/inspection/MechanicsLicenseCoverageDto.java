package com.thedndpub.data.mechanics.inspection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.mechanics.model.AttributionReference;
import com.thedndpub.data.mechanics.model.LicenseReference;
import com.thedndpub.data.mechanics.model.LicensedDocumentReference;
import com.thedndpub.data.mechanics.model.ModelChecks;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record MechanicsLicenseCoverageDto(
        List<String> claimPaths,
        LicensedDocumentReference document,
        LicenseReference license,
        List<AttributionReference> attribution) {
    public MechanicsLicenseCoverageDto {
        claimPaths = ModelChecks.list(claimPaths);
        ModelChecks.required(document, "document");
        ModelChecks.required(license, "license");
        attribution = ModelChecks.list(attribution);
    }
}
