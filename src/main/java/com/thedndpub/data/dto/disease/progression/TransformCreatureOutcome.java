package com.thedndpub.data.dto.disease.progression;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.disease.DiseaseTransformationTarget;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record TransformCreatureOutcome(String kind, DiseaseTransformationTarget target)
        implements DiseaseProgressionOutcome {
}
