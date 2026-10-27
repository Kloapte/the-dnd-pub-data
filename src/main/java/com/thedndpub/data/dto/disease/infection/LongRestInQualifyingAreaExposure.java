package com.thedndpub.data.dto.disease.infection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.disease.DiseaseSavingThrow;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record LongRestInQualifyingAreaExposure(
        String kind,
        String area,
        Boolean noRepellentSincePreviousLongRest,
        List<String> targetCreatureTypes,
        DiseaseSavingThrow savingThrow,
        String onFailure) implements DiseaseExposure {
    public LongRestInQualifyingAreaExposure {
        targetCreatureTypes = targetCreatureTypes == null ? List.of() : List.copyOf(targetCreatureTypes);
    }
}
