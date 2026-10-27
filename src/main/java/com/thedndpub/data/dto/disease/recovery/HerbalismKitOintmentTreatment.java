package com.thedndpub.data.dto.disease.recovery;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.disease.DiseaseInterval;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record HerbalismKitOintmentTreatment(
        String kind,
        String action,
        String outcome,
        Boolean preventsNextLongRestProgression,
        Integer usesRequired,
        String requiresProficiency,
        DiseaseInterval preparationTime,
        String material,
        DiseaseInterval duration,
        DiseaseInterval totalDurationRequired) implements DiseaseTreatment {
}
