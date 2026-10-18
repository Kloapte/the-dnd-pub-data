package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/** Rights classification only. It never grants publication authorization. */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "status")
@JsonSubTypes({
        @JsonSubTypes.Type(value = UnassessedMechanicsLicensing.class, name = "NEEDS_REVIEW"),
        @JsonSubTypes.Type(value = SrdMechanicsLicensing.class, name = "SRD"),
        @JsonSubTypes.Type(value = OtherMechanicsLicensing.class, name = "LICENSED"),
        @JsonSubTypes.Type(value = OpenMechanicsLicensing.class, name = "OPEN")
})
public sealed interface MechanicsLicensing permits UnassessedMechanicsLicensing, SrdMechanicsLicensing,
        OtherMechanicsLicensing, OpenMechanicsLicensing {
    MechanicsLicensingStatus status();
}
