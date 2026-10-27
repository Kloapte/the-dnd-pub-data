package com.thedndpub.data.dto.disease.symptoms;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SenseImpairmentSymptom(String kind, String sense) implements DiseaseSymptom {
}
