package com.thedndpub.data.mechanics.inspection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.mechanics.model.LicensingReviewReason;
import com.thedndpub.data.mechanics.model.MechanicsLicensingStatus;
import com.thedndpub.data.mechanics.model.ModelChecks;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record MechanicsLicensingDto(
        MechanicsLicensingStatus status,
        LicensingReviewReason reviewReason,
        List<MechanicsLicenseCoverageDto> coverage,
        String reuseBasisReference) {
    public MechanicsLicensingDto {
        ModelChecks.required(status, "status");
        coverage = ModelChecks.list(coverage);
    }
}
