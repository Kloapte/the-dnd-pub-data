package com.thedndpub.data.dto.disease.symptoms;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record AbilityRollModifierSymptom(String kind, String roll, String direction, String ability)
        implements DiseaseSymptom {
}
