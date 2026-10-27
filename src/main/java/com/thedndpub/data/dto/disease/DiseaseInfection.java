package com.thedndpub.data.dto.disease;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.disease.infection.DiseaseExposure;
import com.thedndpub.data.dto.disease.infection.DiseaseSecondaryExposure;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record DiseaseInfection(
        List<String> transmission,
        DiseaseSavingThrow savingThrow,
        List<DiseaseExposure> exposures,
        List<DiseaseExposure> exposureProcedures,
        List<DiseaseSecondaryExposure> secondaryExposures) {
    public DiseaseInfection {
        transmission = transmission == null ? List.of() : List.copyOf(transmission);
        exposures = exposures == null ? List.of() : List.copyOf(exposures);
        exposureProcedures = exposureProcedures == null ? List.of() : List.copyOf(exposureProcedures);
        secondaryExposures = secondaryExposures == null ? List.of() : List.copyOf(secondaryExposures);
    }
}
