package com.thedndpub.data.dto.disease.infection;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ContactWithContaminatedMaterialExposure(String kind, List<String> materials)
        implements DiseaseExposure {
    public ContactWithContaminatedMaterialExposure {
        materials = materials == null ? List.of() : List.copyOf(materials);
    }
}
