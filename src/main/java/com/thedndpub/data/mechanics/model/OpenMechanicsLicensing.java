package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record OpenMechanicsLicensing(
        EstablishedReuseBasis basis) implements MechanicsLicensing {
    public OpenMechanicsLicensing {
        ModelChecks.required(basis, "basis");
    }

    @Override
    public MechanicsLicensingStatus status() {
        return MechanicsLicensingStatus.OPEN;
    }
}
