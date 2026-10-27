package com.thedndpub.data.dto.disease.progression;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record GreatStressTrigger(String kind, List<String> examples) implements DiseaseProgressionTrigger {
    public GreatStressTrigger {
        examples = examples == null ? List.of() : List.copyOf(examples);
    }
}
