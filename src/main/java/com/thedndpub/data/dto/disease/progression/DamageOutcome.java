package com.thedndpub.data.dto.disease.progression;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record DamageOutcome(String kind, Integer count, Integer die, String damageType)
        implements DiseaseProgressionOutcome {
}
