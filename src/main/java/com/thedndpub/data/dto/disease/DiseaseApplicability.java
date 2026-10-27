package com.thedndpub.data.dto.disease;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.disease.infection.DiseaseSourceNamedCreatureGroup;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record DiseaseApplicability(
        List<String> targetCreatureTypes,
        Boolean automaticSuccessForDiseaseImmuneTargets,
        List<DiseaseReference> excludedTargets,
        List<DiseaseSourceNamedCreatureGroup> excludedSourceGroups) {
    public DiseaseApplicability {
        targetCreatureTypes = targetCreatureTypes == null ? List.of() : List.copyOf(targetCreatureTypes);
        excludedTargets = excludedTargets == null ? List.of() : List.copyOf(excludedTargets);
        excludedSourceGroups = excludedSourceGroups == null ? List.of() : List.copyOf(excludedSourceGroups);
    }
}
