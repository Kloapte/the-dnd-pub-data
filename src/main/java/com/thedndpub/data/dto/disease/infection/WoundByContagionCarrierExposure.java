package com.thedndpub.data.dto.disease.infection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.disease.DiseaseSavingThrow;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record WoundByContagionCarrierExposure(
        String kind,
        DiseaseSavingThrow savingThrow,
        String onFailure,
        List<String> excludedCreatureTypes,
        Boolean automaticSuccessForExcludedTargets,
        List<String> sourceCreatures) implements DiseaseExposure {
    public WoundByContagionCarrierExposure {
        excludedCreatureTypes = excludedCreatureTypes == null ? List.of() : List.copyOf(excludedCreatureTypes);
        sourceCreatures = sourceCreatures == null ? List.of() : List.copyOf(sourceCreatures);
    }
}
