package com.thedndpub.data.dto.disease;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record DiseaseEffectProfile(String kind, DiseaseReference sourceDisease, List<String> components) {
    public DiseaseEffectProfile {
        components = components == null ? List.of() : List.copyOf(components);
    }
}
