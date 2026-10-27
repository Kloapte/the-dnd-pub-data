package com.thedndpub.data.dto.disease.infection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.disease.DiseaseSavingThrow;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record QualifyingDamageAndEncounterEndExposure(
        String kind,
        List<String> sourceCreatures,
        Boolean encounterEndRequired,
        DiseaseSavingThrow savingThrow,
        String onFailure) implements DiseaseExposure {
    public QualifyingDamageAndEncounterEndExposure {
        sourceCreatures = sourceCreatures == null ? List.of() : List.copyOf(sourceCreatures);
    }
}
