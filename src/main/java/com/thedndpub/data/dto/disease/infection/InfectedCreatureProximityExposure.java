package com.thedndpub.data.dto.disease.infection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.disease.DiseaseSavingThrow;
import com.thedndpub.data.dto.disease.DiseaseReference;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record InfectedCreatureProximityExposure(
        String kind,
        Double distanceFeet,
        String trigger,
        String sourceState,
        DiseaseSavingThrow savingThrow,
        String onFailure,
        DiseaseImmunity onSuccessImmunity,
        List<String> targetCreatureTypes,
        List<DiseaseReference> excludedTargets) implements DiseaseExposure {
    public InfectedCreatureProximityExposure {
        targetCreatureTypes = targetCreatureTypes == null ? List.of() : List.copyOf(targetCreatureTypes);
        excludedTargets = excludedTargets == null ? List.of() : List.copyOf(excludedTargets);
    }
}
