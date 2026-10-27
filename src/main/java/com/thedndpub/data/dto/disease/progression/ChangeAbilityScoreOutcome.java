package com.thedndpub.data.dto.disease.progression;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ChangeAbilityScoreOutcome(String kind, String ability, Integer amount, Integer minimum)
        implements DiseaseProgressionOutcome {
}
