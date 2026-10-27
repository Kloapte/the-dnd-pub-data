package com.thedndpub.data.dto.disease.infection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.disease.DiseaseInterval;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record IngestContaminatedMaterialExposure(
        String kind,
        String material,
        List<String> materials,
        String onFailure,
        com.thedndpub.data.dto.disease.DiseaseSavingThrow savingThrow,
        DiseaseInterval contaminationAfter,
        List<String> targetCreatureTypes,
        List<String> safeAlternatives) implements DiseaseExposure {
    public IngestContaminatedMaterialExposure {
        materials = materials == null ? List.of() : List.copyOf(materials);
        targetCreatureTypes = targetCreatureTypes == null ? List.of() : List.copyOf(targetCreatureTypes);
        safeAlternatives = safeAlternatives == null ? List.of() : List.copyOf(safeAlternatives);
    }
}
