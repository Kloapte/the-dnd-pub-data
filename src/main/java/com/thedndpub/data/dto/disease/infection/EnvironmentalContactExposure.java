package com.thedndpub.data.dto.disease.infection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.disease.DiseaseSavingThrow;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record EnvironmentalContactExposure(
        String kind,
        String environment,
        String trigger,
        DiseaseSavingThrow savingThrow,
        String onFailure) implements DiseaseExposure {
}
