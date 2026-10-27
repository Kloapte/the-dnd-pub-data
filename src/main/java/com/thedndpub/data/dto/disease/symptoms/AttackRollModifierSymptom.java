package com.thedndpub.data.dto.disease.symptoms;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record AttackRollModifierSymptom(
        String kind,
        String actor,
        String direction,
        String roll,
        String ability,
        String lifecycle) implements DiseaseSymptom {
}
