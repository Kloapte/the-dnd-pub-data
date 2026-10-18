package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record UnassessedMechanicsLicensing(
        LicensingReviewReason reason,
        List<LicenseCoverage> partialCoverage,
        List<MechanicsLicensing> priorDeterminations) implements MechanicsLicensing {
    public UnassessedMechanicsLicensing {
        ModelChecks.required(reason, "reason");
        partialCoverage = ModelChecks.list(partialCoverage);
        priorDeterminations = ModelChecks.list(priorDeterminations);
        ModelChecks.require(priorDeterminations.stream().noneMatch(value -> value.status() == MechanicsLicensingStatus.NEEDS_REVIEW),
                "Historical determinations must retain their established classification");
    }

    public static UnassessedMechanicsLicensing pending() {
        return new UnassessedMechanicsLicensing(LicensingReviewReason.NOT_ASSESSED, List.of(), List.of());
    }

    @Override
    public MechanicsLicensingStatus status() {
        return MechanicsLicensingStatus.NEEDS_REVIEW;
    }
}
