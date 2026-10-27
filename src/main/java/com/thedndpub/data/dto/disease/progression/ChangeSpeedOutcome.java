package com.thedndpub.data.dto.disease.progression;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ChangeSpeedOutcome(String kind, Double amount, String unit) implements DiseaseProgressionOutcome {
}
