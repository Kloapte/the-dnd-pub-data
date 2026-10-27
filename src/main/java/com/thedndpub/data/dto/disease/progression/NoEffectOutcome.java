package com.thedndpub.data.dto.disease.progression;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record NoEffectOutcome(String kind) implements DiseaseProgressionOutcome {
}
